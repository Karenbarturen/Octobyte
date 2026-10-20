/* LÓGICA: todo lo que se mueve en la página, en JavaScript puro.
   Las 5 ideas clave:
   1. S           -> el objeto con todos los datos actuales (batería, viento, modo...).
   2. simulate()  -> inventa datos nuevos mientras no hay hardware.
   3. fetchApi()  -> pide datos reales a la API cuando pegás una URL.
   4. render()    -> escribe los datos de S en la pantalla.
   5. setInterval(tick, 3000) -> repite todo cada 3 segundos. */
"use strict";
const $ = (id) => document.getElementById(id);
const RM = matchMedia("(prefers-reduced-motion: reduce)").matches;
const COLORBLIND_STORAGE_KEY = "sistema-energetico-modo-daltonico";
let modeAlertTimeout = 0;
let assistantFlightAnimation = null;
const LEVELS = {
	normal: { n: "Normal", i: "✔", t: "Sin acción necesaria." },
	low: {
		n: "Bajo",
		i: "ℹ",
		t: "Revisar generación y evitar cargas prescindibles.",
	},
	medium: { n: "Medio", i: "⚠", t: "Pasar a modo ahorro." },
	high: {
		n: "Alto",
		i: "▲",
		t: "Modo ahorro activo. Preparar fuente alternativa.",
	},
	critical: {
		n: "Crítico",
		i: "✖",
		t: "Emergencia automática: conservar la energía para las cargas esenciales.",
	},
};
const TAGC = {
	normal: "tn",
	low: "tl",
	medium: "tm",
	high: "th",
	critical: "tc",
};
// Umbrales de batería (%). Documentarlos en docs/
const levelFor = (p) =>
	p > 50
		? "normal"
		: p > 30
			? "low"
			: p > 20
				? "medium"
				: p > 10
					? "high"
					: "critical";
const LOADS = [
	{ id: "com", name: "Comunicaciones", prio: "Crítica", w: 4 },
	{ id: "sen", name: "Sensores del nodo", prio: "Crítica", w: 2 },
	{ id: "agu", name: "Bomba de agua", prio: "Importante", w: 10 },
	{ id: "luz", name: "Iluminación", prio: "Prescindible", w: 6 },
	{ id: "cal", name: "Calefacción", prio: "Prescindible", w: 20 },
];
const MN = { normal: "Normal", ahorro: "Ahorro", emergencia: "Emergencia" };
// Consumo estimado por kit en cada modo (W). Los modos reducen demanda;
// nunca limitan la generación del panel o del aerogenerador.
const KIT_CONSUMPTION = {
	pv: { normal: 26, ahorro: 8, emergencia: 2 },
	wind: { normal: 10, ahorro: 5, emergencia: 2 },
	thermal: { normal: 6, ahorro: 3, emergencia: 2 },
};
const S = {
	batt: 100,
	cap: 60,
	autoThreshold: 40,
	emergencyThreshold: 10,
	solar: 0,
	solarV: 0,
	windMs: 4,
	windW: 0,
	thermalW: 0,
	generatedWh: { pv: 0, wind: 0, thermal: 0 },
	tempC: 22,
	cons: 42,
	// Dos muestras iniciales para que el gráfico aparezca desde la primera carga.
	hist: [
		{ g: 8, c: 6 },
		{ g: 8, c: 6 },
	],
	events: [],
	api: "http://localhost:8080",
	online: true,
	source: "simulated",
	phase: 0.6,
	simulationStarted: false,
	lastLevel: null,
	last: null,
	apiLevel: null,
	selectedKit: null,
	kitModes: { pv: "normal", wind: "normal", thermal: "normal" },
	autoMode: "normal",
	modeReason: "",
};
const fmt = (n) => Number(n).toFixed(1);
const clock = () =>
	new Date().toLocaleTimeString("es-AR", {
		hour: "2-digit",
		minute: "2-digit",
		second: "2-digit",
	});
const consumptionForKit = (kitId) => KIT_CONSUMPTION[kitId][S.kitModes[kitId]];
const totalKitConsumption = () =>
	Object.keys(KIT_CONSUMPTION).reduce(
		(total, kitId) => total + consumptionForKit(kitId),
		0,
	);
const normalKitConsumption = () =>
	Object.values(KIT_CONSUMPTION).reduce((total, modes) => total + modes.normal, 0);
function applyAutomaticMode(generation, measuredConsumption = S.cons) {
	const batteryPercent = Math.round(S.batt);
	const emergencyBattery = batteryPercent <= S.emergencyThreshold;
	const lowBattery = batteryPercent < S.autoThreshold;
	const expectedConsumption =
		!S.api && S.autoMode !== "normal"
			? normalKitConsumption()
			: measuredConsumption;
	const highUse = expectedConsumption > generation;
	const mode = emergencyBattery
		? "emergencia"
		: lowBattery
			? "ahorro"
			: "normal";
	const reason = emergencyBattery
		? "Batería en el umbral de emergencia (" + S.emergencyThreshold + "% o menos)."
		: lowBattery
		? "Batería por debajo del " + S.autoThreshold + "%."
		: highUse
			? "Batería sobre el umbral de ahorro; vuelve a Normal aunque el consumo supere la generación."
			: "Batería suficiente y generación cubre el consumo normal.";
	const previousMode = S.autoMode;
	const modeChanged = previousMode !== mode;
	const forceAllKits = mode !== "normal" || previousMode !== "normal";
	let kitModesChanged = false;
	if (forceAllKits)
		Object.keys(S.kitModes).forEach((kitId) => {
			if (S.kitModes[kitId] !== mode) kitModesChanged = true;
			S.kitModes[kitId] = mode;
		});
	S.autoMode = mode;
	S.modeReason = reason;
	if (modeChanged) {
		const message =
			"Modo operacional cambió a " + MN[mode] + ". " + reason;
		addEvent(
			mode === "emergencia" ? "critical" : mode === "ahorro" ? "medium" : "normal",
			message + " Se aplica a los tres kits.",
		);
		showModeAlert("Modo operacional cambió a " + MN[mode] + ".", mode);
	} else if (kitModesChanged) {
		addEvent("normal", "Los tres kits se sincronizaron al modo " + MN[mode] + ".");
	}
}
// Potencia eólica estimada (kit chico): 0,5·ρ·A·Cp·v³ con A≈0,05 m² y Cp≈0,3
const windPower = (v) => Math.min(15, 0.009 * v * v * v);
const kitPower = (_kitId, rawPower) => rawPower;
function addEvent(level, text) {
	S.events.unshift({ level, text, t: clock() });
	S.events = S.events.slice(0, 40);
}
function showModeAlert(message, mode) {
	const alert = $("mode-alert");
	const overlay = $("mode-alert-overlay");
	const assistantBot = $("assistant-bot");
	$("mode-alert-message").textContent = message;
	alert.className = "mode-alert mode-alert-" + mode;
	overlay.hidden = false;
	overlay.classList.remove("is-open");
	void alert.offsetWidth;
	overlay.classList.add("is-open");
	if (assistantFlightAnimation) assistantFlightAnimation.cancel();
	const botRect = assistantBot.getBoundingClientRect();
	const deltaX = window.innerWidth / 2 - (botRect.left + botRect.width / 2);
	const deltaY = 78 - (botRect.top + botRect.height / 2);
	assistantFlightAnimation = assistantBot.animate(
		[
			{ transform: "translate(0, 0) scale(1) rotate(0deg)" },
			{
				transform:
					"translate(" +
					deltaX * 0.48 +
					"px, " +
					(deltaY * 0.48 - 42) +
					"px) scale(1.2) rotate(-7deg)",
				offset: 0.48,
			},
			{
				transform:
					"translate(" +
					deltaX +
					"px, " +
					deltaY +
					"px) scale(1.16) rotate(0deg)",
			},
		],
		{
			duration: RM ? 0 : 1050,
			easing: "cubic-bezier(0.2, 0.8, 0.25, 1)",
			fill: "forwards",
		},
	);
	clearTimeout(modeAlertTimeout);
	modeAlertTimeout = setTimeout(() => {
		closeModeAlert();
	}, 4000);
}
function closeModeAlert() {
	const overlay = $("mode-alert-overlay");
	if (overlay.hidden) return;
	clearTimeout(modeAlertTimeout);
	overlay.hidden = true;
	overlay.classList.remove("is-open");
	if (!assistantFlightAnimation) return;
	const flight = assistantFlightAnimation;
	flight.onfinish = () => {
		flight.cancel();
		if (assistantFlightAnimation === flight) assistantFlightAnimation = null;
	};
	flight.reverse();
}
$("mode-alert-close").addEventListener("click", closeModeAlert);
document.addEventListener("keydown", (event) => {
	if (event.key === "Escape" && !$("mode-alert-overlay").hidden)
		closeModeAlert();
});

const KIT_DATA = {
	pv: {
		title: "Kit fotovoltaico",
		description: "Paneles solares · generación eléctrica",
		position: [-37.81755, -71.1009],
		power: () => kitPower("pv", S.solar),
		generated: () => S.generatedWh.pv,
		stored: () => (S.batt / 100) * S.cap,
		storageNote: () =>
			"Reserva eléctrica compartida · " + Math.round(S.batt) + "% de batería",
	},
	wind: {
		title: "Kit eólico",
		description: "Aerogenerador · generación eléctrica estimada",
		position: [-37.81735, -71.09685],
		power: () => kitPower("wind", S.windW),
		generated: () => S.generatedWh.wind,
		stored: () => (S.batt / 100) * S.cap,
		storageNote: () =>
			"Reserva eléctrica compartida · " + Math.round(S.batt) + "% de batería",
	},
	thermal: {
		title: "Kit termosolar",
		description: "Colector solar · generación térmica estimada",
		position: [-37.82105, -71.09905],
		power: () => kitPower("thermal", S.thermalW),
		generated: () => S.generatedWh.thermal,
		stored: () => Math.max(0, S.tempC - 8) * 1.163 * 20,
		storageNote: () =>
			"Reserva estimada · agua a " +
			fmt(S.tempC) +
			" °C; tanque simulado de 20 L",
	},
};
const KIT_MODELS = {
	pv: `<svg viewBox="0 0 120 100" role="img" aria-hidden="true"><defs><linearGradient id="pv-face" x2="0" y2="1"><stop stop-color="#3ba5e9"/><stop offset="1" stop-color="#07508c"/></linearGradient><linearGradient id="pv-edge" x2="0" y2="1"><stop stop-color="#cbd5dd"/><stop offset="1" stop-color="#536575"/></linearGradient></defs><ellipse cx="60" cy="86" rx="40" ry="7" fill="#142832" opacity=".45"/><path d="M38 53 82 42 101 55 54 68Z" fill="#243c4e" stroke="#d7e7f2" stroke-width="2"/><path d="M34 19 80 9 101 22 53 34Z" fill="url(#pv-face)" stroke="#eff8ff" stroke-width="2.4"/><path d="M34 19 53 34 53 67 34 52Z" fill="#123c63" stroke="#a9c7dd" stroke-width="1.5"/><path d="M53 34 101 22 101 55 53 67Z" fill="url(#pv-edge)" stroke="#eaf5ff" stroke-width="2"/><path d="M53 34 53 67M69 30 69 63M85 26 85 59M53 45 101 33M53 56 101 44" fill="none" stroke="#83cafa" stroke-width="1.3" opacity=".9"/><path d="M58 67 55 84M91 57 96 79" stroke="#ccd7de" stroke-width="4"/><path d="M49 84h13M91 79h12" stroke="#8195a2" stroke-width="4" stroke-linecap="round"/></svg>`,
	wind: `<svg viewBox="0 0 120 100" role="img" aria-hidden="true"><defs><linearGradient id="tower" x2="1"><stop stop-color="#d6e0e6"/><stop offset=".45" stop-color="#fff"/><stop offset="1" stop-color="#788c99"/></linearGradient><linearGradient id="blade" x2="0" y2="1"><stop stop-color="#f5fbff"/><stop offset="1" stop-color="#91a8b7"/></linearGradient></defs><ellipse cx="60" cy="88" rx="22" ry="6" fill="#172831" opacity=".42"/><path d="m56 37 8 0 5 48H51Z" fill="url(#tower)" stroke="#5e7180" stroke-width="1.5"/><path d="m54 85 8-1 9 5-22 0Z" fill="#667b87"/><circle cx="60" cy="33" r="7" fill="#dbe6eb" stroke="#596d7a" stroke-width="2"/><path d="M60 31 57 4Q58 0 61 5l3 24Z" fill="url(#blade)" stroke="#728894" stroke-width="1"/><path d="m64 35 23 14q3 3-2 3L63 39Z" fill="url(#blade)" stroke="#728894" stroke-width="1"/><path d="m56 37-20 18q-4 1-2-3l18-19Z" fill="url(#blade)" stroke="#728894" stroke-width="1"/><circle cx="60" cy="33" r="3" fill="#536977"/></svg>`,
	thermal: `<svg viewBox="0 0 120 100" role="img" aria-hidden="true"><defs><linearGradient id="th-face" x2="0" y2="1"><stop stop-color="#4aa9d0"/><stop offset="1" stop-color="#154f70"/></linearGradient><linearGradient id="th-tank" x2="1"><stop stop-color="#bacbd4"/><stop offset=".5" stop-color="#f4f8fa"/><stop offset="1" stop-color="#758b98"/></linearGradient></defs><ellipse cx="61" cy="87" rx="39" ry="7" fill="#142832" opacity=".45"/><path d="M25 51 67 40 88 51 44 64Z" fill="#283e4c" stroke="#e3edf2" stroke-width="2"/><path d="M25 18 67 8 88 19 44 31Z" fill="url(#th-face)" stroke="#fff" stroke-width="2"/><path d="M25 18 44 31 44 63 25 50Z" fill="#174c68" stroke="#b8d6e2" stroke-width="1.5"/><path d="M44 31 88 19 88 51 44 63Z" fill="url(#th-face)" stroke="#e9f7fc" stroke-width="2"/><path d="M44 31v32m15-36v32m15-36v32M44 42l44-12M44 52l44-12" fill="none" stroke="#8dd5ef" stroke-width="1.4"/><path d="M47 63 43 81m34-30 7 26" stroke="#cbd7dc" stroke-width="4"/><path d="M86 18c0-6 8-9 14-6 5 2 5 7 5 10v28c0 5-4 8-10 8s-10-3-10-8V23c0-2 0-4 1-5Z" fill="url(#th-tank)" stroke="#536b78" stroke-width="2"/><ellipse cx="95" cy="20" rx="9" ry="4" fill="#f4fafc" stroke="#718591" stroke-width="1.5"/><path d="M92 59v8h8v-9m-3-35v23" fill="none" stroke="#d7e4e9" stroke-width="2"/></svg>`,
};
function openKit(id) {
	if (!KIT_DATA[id]) return;
	S.selectedKit = id;
	renderKitPanel(id);
	$("kit-panel").hidden = false;
	$("map-layout").classList.add("kit-open");
	if (S.map) setTimeout(() => S.map.invalidateSize(), 220);
}
function closeKit() {
	S.selectedKit = null;
	$("kit-panel").hidden = true;
	$("map-layout").classList.remove("kit-open");
	setTimeout(() => {
		if (S.map) S.map.invalidateSize();
	}, 220);
}
function renderKitPanel(kitId = S.selectedKit) {
	const info = KIT_DATA[kitId];
	if (!info) return;
	$("kit-title").textContent = info.title;
	$("kit-description").textContent = info.description;
	$("kit-power").textContent = fmt(info.power()) + " W";
	$("kit-generated").textContent = fmt(info.generated()) + " Wh";
	$("kit-stored").textContent = fmt(info.stored()) + " Wh";
	$("kit-consumption").textContent = fmt(consumptionForKit(kitId)) + " W estimados";
	$("kit-storage-note").textContent = info.storageNote();
	const status = kitStatus(kitId);
	const kitName = info.title.replace("Kit ", "");
	$("kit-state-text").textContent = "Estado energético: " + status.label;
	$("kit-state-text").className = "kit-state-text " + status.className;
	$("kit-mode-title").textContent =
		"Modo operacional (" +
		kitName.charAt(0).toLocaleUpperCase("es-AR") +
		kitName.slice(1) +
		")";
	$("kit-mode-value").textContent = MN[S.kitModes[kitId]];
	$("kit-mode-value").className = "auto-mode-value mode-" + S.kitModes[kitId];
	$("kit-mode-reason").textContent = S.modeReason;
	$("kit-mode-note").textContent = S.api
		? "Modo automático informativo. Para accionar equipos reales, la API debe implementar comandos de control."
		: "Modo automático compartido por los tres kits según batería y balance de energía.";
}
function kitStatus(kitId) {
	const percent =
		kitId === "thermal"
			? Math.max(0, Math.min(100, ((S.tempC - 8) / 52) * 100))
			: S.batt;
	if (!Number.isFinite(percent))
		return { className: "state-unknown", label: "Sin lectura" };
	if (percent <= 10)
		return { className: "state-critical", label: "Crítico" };
	if (percent <= 50)
		return { className: "state-warning", label: "Reserva intermedia" };
	return { className: "state-normal", label: "Normal" };
}
function renderKitMarkerStates() {
	Object.keys(KIT_DATA).forEach((kitId) => {
		const marker = $("kit-state-" + kitId);
		if (!marker) return;
		const status = kitStatus(kitId);
		marker.className = "kit-state-dot " + status.className;
		marker.title = status.label;
		marker.setAttribute("aria-label", "Estado energético: " + status.label);
	});
}
function initEnergyMap() {
	$("kit-close").addEventListener("click", closeKit);
	if (!window.L) {
		$("real-map").hidden = true;
		$("map-fallback").hidden = false;
		return;
	}
	S.map = L.map("real-map", {
		dragging: false,
		scrollWheelZoom: false,
		doubleClickZoom: false,
		boxZoom: false,
		keyboard: false,
		touchZoom: false,
		zoomControl: false,
		minZoom: 16,
		maxZoom: 16,
		maxBounds: L.latLngBounds([-37.83, -71.12], [-37.805, -71.078]),
		maxBoundsViscosity: 1,
	}).setView(
		[-37.81882, -71.09874],
		16,
	);
	const imagery = L.tileLayer(
		"https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
		{
			maxZoom: 19,
			attribution:
				"Imagery © Esri, Maxar, Earthstar Geographics, and the GIS User Community",
		},
	).addTo(S.map);
	imagery.on("tileerror", () => {
		const f = $("map-fallback");
		f.textContent =
			"No se pudieron cargar las imágenes satelitales. Revisá tu conexión a internet.";
		f.hidden = false;
	});
	Object.entries(KIT_DATA).forEach(([id, kit]) => {
		const label = kit.title.replace("Kit ", "");
		const model = KIT_MODELS[id];
		const icon = L.divIcon({
			className: "kit-map-icon",
			iconSize: [116, 126],
			iconAnchor: [58, 104],
			html:
				"<div class='kit-3d-pin kit-3d-" +
				id +
				"' aria-label='" +
				kit.title +
				"'><span id='kit-state-" +
				id +
				"' class='kit-state-dot state-normal' role='img' aria-label='Estado energético: Normal'></span><span class='kit-model'>" +
				model +
				"</span><span class='kit-model-label'>" +
				label +
				"</span></div>",
		});
		L.marker(kit.position, {
			icon,
			title: kit.title,
			alt: kit.title,
			keyboard: true,
		})
			.addTo(S.map)
			.on("click", () => openKit(id));
	});
}

function simulate() {
	S.phase += 0.12;
	const sun = Math.max(0, Math.sin(S.phase)) * 28;
	S.solar = Math.max(0, sun + (Math.random() - 0.5) * 2);
	S.solarV = S.solar > 0 ? 3 + (S.solar / 28) * 14 : 0;
	S.windMs = Math.min(14, Math.max(0, S.windMs + (Math.random() - 0.5) * 2));
	S.windW = windPower(S.windMs);
	S.tempC = Math.min(
		85,
		Math.max(8, S.tempC + (S.solar > 5 ? 0.5 : -0.3)),
	);
	const electricGeneration =
		kitPower("pv", S.solar) + kitPower("wind", S.windW);
	applyAutomaticMode(electricGeneration, totalKitConsumption());
	S.cons = totalKitConsumption();
	const wh =
		(S.batt / 100) * S.cap + ((electricGeneration - S.cons) * 300) / 3600; // cada lectura = 5 min simulados
	// La primera lectura muestra la carga inicial completa; la simulación empieza
	// a modificarla a partir de la siguiente actualización.
	if (S.simulationStarted)
		S.batt = Math.max(0, Math.min(100, (wh / S.cap) * 100));
	else S.simulationStarted = true;
	S.source = "simulated";
	S.online = true;
}
async function fetchApi() {
	try {
		const r = await fetch(S.api.replace(/\/$/, "") + "/api/status", {
			cache: "no-store",
		});
		if (!r.ok) throw new Error(r.status);
		const j = await r.json(),
			d = j.data || {};
		S.batt = +d.battery_level;
		S.solar = +d.generation_power || 0;
		S.solarV = +d.voltage || 0;
		S.windMs = +d.wind_speed || 0;
		S.windW = d.wind_power != null ? +d.wind_power : windPower(S.windMs);
		S.tempC = +d.temperature;
		S.cons = Number.isFinite(+d.consumption_power)
			? +d.consumption_power
			: totalKitConsumption();
		applyAutomaticMode(S.solar + S.windW, S.cons);
		S.source =
			j.metadata && j.metadata.source_type === "simulated"
				? "simulated"
				: "real";
		S.apiLevel = LEVELS[j.alert_level] ? j.alert_level : null;
		S.online = !(j.status === "offline" || j.status === "error");
	} catch (e) {
		if (S.online)
			addEvent(
				"high",
				"Sin conexión con la API. Se muestran los últimos datos.",
			);
		S.online = false;
	}
}
function render() {
	const assistantBot = $("assistant-bot");
	assistantBot.classList.remove(
		"assistant-normal",
		"assistant-ahorro",
		"assistant-emergencia",
	);
	assistantBot.classList.add("assistant-" + S.autoMode);
	assistantBot.setAttribute(
		"aria-label",
		"Asistente energético. Modo actual: " + MN[S.autoMode],
	);
	const p = Math.round(S.batt),
		lv = S.api && S.apiLevel ? S.apiLevel : levelFor(S.batt),
		L = LEVELS[lv];
	const gen = kitPower("pv", S.solar) + kitPower("wind", S.windW),
		bal = gen - S.cons,
		okp = Number.isFinite(p),
		energy = (S.batt / 100) * S.cap;
	$("pct").textContent = okp ? p : "--";
	$("fill").style.width = (okp ? p : 0) + "%";
	$("barwrap").className = "battery-meter lv-" + lv;
	$("barwrap").setAttribute("aria-valuenow", okp ? p : 0);
	$("barwrap").setAttribute(
		"aria-label",
		okp
			? "Carga de batería: " + p + " por ciento"
			: "Carga de batería sin lectura",
	);
	$("bstate").textContent = okp ? L.n : "Sin lectura";
	$("bstate").className = "battery-state lv-" + lv;
	$("energyval").textContent = okp
		? fmt(energy) + " / " + fmt(S.cap) + " Wh"
		: "--";
	$("vsol").textContent = fmt(S.solar) + " W";
	$("vsolv").textContent =
		S.solarV > 0 ? fmt(S.solarV) + " V en el panel" : "Sin generación";
	$("vwind").textContent = fmt(S.windMs) + " m/s";
	$("vwindp").textContent = "≈ " + fmt(S.windW) + " W estimados";
	$("vtemp").textContent = Number.isFinite(S.tempC)
		? fmt(S.tempC) + " °C"
		: "--";
	$("ttxt").textContent =
		S.tempC < 25
			? "Agua fría"
			: S.tempC <= 60
				? "Útil para calentar"
				: "Muy caliente: revisar";
	$("vcons").textContent = fmt(S.cons) + " W";
	$("vbal").textContent = (bal > 0 ? "+" : "") + fmt(bal) + " W";
	$("btxt").textContent =
		bal >= 0 ? "La batería se carga" : "La batería se descarga";
	if (bal >= 0) $("autoval").textContent = "cargando";
	else {
		const h = ((S.batt / 100) * S.cap) / -bal,
			hh = Math.floor(h);
		$("autoval").textContent = hh + " h " + Math.round((h - hh) * 60) + " min";
	}
	$("alert").className = "alert lv-" + lv;
	$("aic").textContent = L.i;
	$("ah").textContent = L.n + " · Batería al " + p + " %";
	$("ap").textContent = L.t;
	$("agen").textContent = fmt(gen) + " W";
	const criticalLoads = LOADS.filter((l) => l.prio === "Crítica");
	const criticalW = criticalLoads.reduce((sum, l) => sum + l.w, 0);
	$("acritical").textContent = fmt(criticalW) + " W";
	$("aloads").textContent =
		criticalLoads.map((l) => l.name).join(" y ") || "Ninguna activa";
	$("atip").textContent = {
		normal:
			"Seguimiento activo: generación, consumo y reserva actualizados en cada lectura.",
		low: "Evitá las cargas prescindibles mientras se recupera la reserva.",
		medium: "Activá el modo ahorro para prolongar la autonomía.",
		high: "Reducí consumos y prepará una fuente de energía alternativa.",
		critical:
			"Emergencia automática activa. Prioridad: recargar la batería y sostener las cargas esenciales.",
	}[lv];
	if (S.lastLevel !== lv) {
		if (S.lastLevel !== null)
			addEvent(lv, "Nivel de alerta: " + L.n + " (batería " + p + " %).");
		S.lastLevel = lv;
	}
	const c = $("conn");
	if (c) {
		c.textContent = S.online ? "Conectado" : "Sin conexión";
		c.className = "chip " + (S.online ? "ok" : "bad");
	}
	const s = $("src");
	if (s) {
		s.textContent = S.source === "real" ? "Datos reales" : "Datos simulados";
		s.className = "chip " + (S.source === "real" ? "ok" : "sim");
	}
	const upd = $("upd");
	if (upd) upd.textContent = "Última lectura " + (S.last || "--");
	renderKitMarkerStates();
	$("events").innerHTML = S.events.length
		? S.events
				.map(
					(e) =>
						"<li><span class='tag' style='color:var(--" +
						TAGC[e.level] +
						")'>" +
						LEVELS[e.level].i +
						" " +
						LEVELS[e.level].n +
						"</span> " +
						e.t +
						" · " +
						e.text +
						"</li>",
				)
				.join("")
		: "<li>Sin eventos todavía.</li>";
	renderKitPanel();
	drawChart();
	renderExtraPanels();
}
function renderExtraPanels() {
	const connection = $("connection-status");
	connection.textContent = !S.api
		? "Modo local · sin API"
		: S.online
			? "API conectada"
			: "API sin conexión";
	connection.className = !S.api
		? "status-neutral"
		: S.online
			? "status-good"
			: "status-bad";
	$("source-status").textContent = S.source === "real" ? "API real" : "Simulación local";
	$("reading-time").textContent = S.last || "Esperando lectura";
	$("energy-status").textContent =
		fmt((S.batt / 100) * S.cap) + " / " + fmt(S.cap) + " Wh · " + Math.round(S.batt) + "%";
	const history = S.hist.slice(-8).reverse();
	$("history-list").innerHTML = history.length
		? history
				.map(
					(item) =>
						"<li><time>" +
						(item.t || "Inicio") +
						"</time><span>Generación " +
						fmt(item.g) +
						" W · Consumo " +
						fmt(item.c) +
						" W</span></li>",
				)
				.join("")
		: "<li>Sin lecturas todavía.</li>";
}
function drawChart() {
	const H = S.hist,
		W = 600,
		Ht = 200,
		m = { l: 42, r: 10, t: 10, b: 22 };
	if (H.length < 2) {
		$("chart").innerHTML = "";
		return;
	}
	const max = Math.max(30, ...H.map((h) => Math.max(h.g, h.c))) * 1.1;
	const x = (i) => m.l + (i * (W - m.l - m.r)) / (H.length - 1),
		y = (v) => Ht - m.b - (v / max) * (Ht - m.t - m.b);
	const pts = (k) =>
		H.map((h, i) => x(i).toFixed(1) + "," + y(h[k]).toFixed(1)).join(" ");
	let g = "";
	for (let k = 0; k <= 4; k++) {
		const v = (max * k) / 4;
		g +=
			"<line x1='" +
			m.l +
			"' x2='" +
			(W - m.r) +
			"' y1='" +
			y(v) +
			"' y2='" +
			y(v) +
			"' stroke='rgba(255,255,255,.16)'/><text x='" +
			(m.l - 6) +
			"' y='" +
			(y(v) + 4) +
			"' font-size='11' text-anchor='end' fill='var(--mut)'>" +
			Math.round(v) +
			" W</text>";
	}
	$("chart").innerHTML =
		g +
		"<polyline points='" +
		pts("g") +
		"' fill='none' stroke='var(--gold)' stroke-width='4'/><polyline points='" +
		pts("c") +
		"' fill='none' stroke='var(--ink)' stroke-width='3' stroke-dasharray='8 5'/>" +
		"<text x='" +
		m.l +
		"' y='" +
		(Ht - 4) +
		"' font-size='11' fill='var(--mut)'>más antiguo</text><text x='" +
		(W - m.r) +
		"' y='" +
		(Ht - 4) +
		"' font-size='11' text-anchor='end' fill='var(--mut)'>ahora</text>";
	const l = H[H.length - 1];
	$("chartsum").textContent =
		"Ahora: generación " + fmt(l.g) + " W, consumo " + fmt(l.c) + " W.";
}
async function tick() {
	if (S.api) await fetchApi();
	else simulate();
	S.thermalW = S.solar * 0.75;
	const simulatedHours = 5 / 60;
	S.generatedWh.pv += kitPower("pv", S.solar) * simulatedHours;
	S.generatedWh.wind += kitPower("wind", S.windW) * simulatedHours;
	S.generatedWh.thermal += kitPower("thermal", S.thermalW) * simulatedHours;
	S.last = clock();
	S.hist.push({
		g: kitPower("pv", S.solar) + kitPower("wind", S.windW),
		c: S.cons,
		t: S.last,
	});
	S.hist = S.hist.slice(-40);
	render();
}
function initColorblindMode() {
	const toggle = $("colorblind-toggle");
	let enabled = false;
	try {
		enabled = localStorage.getItem(COLORBLIND_STORAGE_KEY) === "true";
	} catch (error) {
		// La opción sigue disponible en la sesión si el almacenamiento está bloqueado.
	}
	const update = () => {
		document.body.classList.toggle("colorblind-mode", enabled);
		toggle.setAttribute("aria-pressed", String(enabled));
		toggle.innerHTML =
			'<span aria-hidden="true">◉</span> Modo daltónico: ' +
			(enabled ? "activado" : "desactivado");
	};
	toggle.addEventListener("click", () => {
		enabled = !enabled;
		update();
		try {
			localStorage.setItem(COLORBLIND_STORAGE_KEY, String(enabled));
		} catch (error) {
			// No interrumpir el cambio visual si el almacenamiento está bloqueado.
		}
	});
	update();
}
if (!RM)
	document.querySelectorAll(".tilt").forEach((c) => {
		c.addEventListener("pointermove", (e) => {
			const b = c.getBoundingClientRect(),
				x = (e.clientX - b.left) / b.width - 0.5,
				y = (e.clientY - b.top) / b.height - 0.5;
			c.style.transform =
				"perspective(800px) rotateX(" +
				-y * 5 +
				"deg) rotateY(" +
				x * 5 +
				"deg)";
		});
		c.addEventListener("pointerleave", () => (c.style.transform = ""));
	});
addEvent("normal", "Panel iniciado con datos simulados.");
initColorblindMode();
initEnergyMap();
tick();
setInterval(tick, 3000);
