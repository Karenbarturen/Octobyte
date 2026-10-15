/* LÓGICA: todo lo que se mueve en la página, en JavaScript puro.
   Las 5 ideas clave:
   1. S           -> el objeto con todos los datos actuales (batería, viento, modo...).
   2. simulate()  -> inventa datos nuevos mientras no hay hardware.
   3. fetchApi()  -> pide datos reales a la API cuando pegás una URL.
   4. render()    -> escribe los datos de S en la pantalla.
   5. setInterval(tick, 3000) -> repite todo cada 3 segundos. */
"use strict";
const $ = id => document.getElementById(id);
const RM = matchMedia("(prefers-reduced-motion: reduce)").matches;
const LEVELS = {
  normal:{n:"Normal",i:"✔",t:"Sin acción necesaria."},
  low:{n:"Bajo",i:"ℹ",t:"Revisar generación y evitar cargas prescindibles."},
  medium:{n:"Medio",i:"⚠",t:"Pasar a modo ahorro."},
  high:{n:"Alto",i:"▲",t:"Modo ahorro activo. Preparar fuente alternativa."},
  critical:{n:"Crítico",i:"✖",t:"Modo emergencia: solo cargas críticas."}
};
const TAGC={normal:"tn",low:"tl",medium:"tm",high:"th",critical:"tc"};
// Umbrales de batería (%). Documentarlos en docs/
const levelFor=p=>p>50?"normal":p>30?"low":p>20?"medium":p>10?"high":"critical";
const LOADS=[
  {id:"com",name:"Comunicaciones",prio:"Crítica",w:4},{id:"sen",name:"Sensores del nodo",prio:"Crítica",w:2},
  {id:"agu",name:"Bomba de agua",prio:"Importante",w:10},{id:"luz",name:"Iluminación",prio:"Prescindible",w:6},
  {id:"cal",name:"Calefacción",prio:"Prescindible",w:20}];
const MODES={normal:["com","sen","agu","luz","cal"],ahorro:["com","sen","agu"],emergencia:["com","sen"]};
const MN={normal:"Normal",ahorro:"Ahorro",emergencia:"Emergencia"};
const KITS=[{n:"Kit eólico",t:"Eléctrica"},{n:"Kit fotovoltaico",t:"Eléctrica"},{n:"Kit termosolar",t:"Térmica"}];
const S={batt:78,cap:60,mode:"normal",solar:0,solarV:0,windMs:4,windW:0,tempC:22,cons:0,hist:[],events:[],
  api:"",online:true,source:"simulated",cut:false,phase:.6,lastLevel:null,last:null,apiLevel:null};
const fmt=n=>Number(n).toFixed(1);
const clock=()=>new Date().toLocaleTimeString("es-AR",{hour:"2-digit",minute:"2-digit",second:"2-digit"});
const consFor=m=>LOADS.filter(l=>MODES[m].includes(l.id)).reduce((a,l)=>a+l.w,0);
// Potencia eólica estimada (kit chico): 0,5·ρ·A·Cp·v³ con A≈0,05 m² y Cp≈0,3
const windPower=v=>Math.min(15,0.009*v*v*v);
function addEvent(level,text){S.events.unshift({level,text,t:clock()});S.events=S.events.slice(0,40);}

function simulate(){
  S.phase+=.12;
  const sun=Math.max(0,Math.sin(S.phase))*28;
  S.solar=S.cut?0:Math.max(0,sun+(Math.random()-.5)*2);
  S.solarV=S.solar>0?3+S.solar/28*14:0;
  S.windMs=S.cut?0:Math.min(14,Math.max(0,S.windMs+(Math.random()-.5)*2));
  S.windW=windPower(S.windMs);
  S.tempC=Math.min(85,Math.max(8,S.tempC+(S.solar>5?.5:-.3)));
  S.cons=consFor(S.mode);
  const wh=S.batt/100*S.cap+(S.solar+S.windW-S.cons)*300/3600; // cada lectura = 5 min simulados
  S.batt=Math.max(0,Math.min(100,wh/S.cap*100));
  const lv=levelFor(S.batt);
  if((lv==="medium"||lv==="high")&&S.mode==="normal")setMode("ahorro",true);
  if(lv==="critical"&&S.mode!=="emergencia")setMode("emergencia",true);
  S.source="simulated";S.online=true;
}
async function fetchApi(){
  try{
    const r=await fetch(S.api.replace(/\/$/,"")+"/api/status",{cache:"no-store"});
    if(!r.ok)throw new Error(r.status);
    const j=await r.json(),d=j.data||{};
    S.batt=+d.battery_percent;S.solar=+d.solar_power_w||0;S.solarV=+d.solar_voltage_v||0;
    S.windMs=+d.wind_speed_ms||0;S.windW=d.wind_power_w!=null?+d.wind_power_w:windPower(S.windMs);
    S.tempC=+d.water_temp_c;S.cons=+d.consumption_w||0;
    if(MODES[d.mode])S.mode=d.mode;
    S.source=d.source==="simulated"?"simulated":"real";
    S.apiLevel=LEVELS[j.alert_level]?j.alert_level:null;
    S.online=!(j.status==="offline"||j.status==="error");
  }catch(e){
    if(S.online)addEvent("high","Sin conexión con la API. Se muestran los últimos datos.");
    S.online=false;
  }
}
function setMode(m,auto){
  if(S.mode===m)return;
  S.mode=m;addEvent(auto?"medium":"normal",(auto?"Cambio automático":"Cambio manual")+" a modo "+MN[m]+".");
  if(S.api&&!auto)fetch(S.api.replace(/\/$/,"")+"/api/mode",{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({mode:m})})
    .catch(()=>addEvent("high","No se pudo enviar el modo a la API."));
}
function render(){
  const p=Math.round(S.batt),lv=(S.api&&S.apiLevel)?S.apiLevel:levelFor(S.batt),L=LEVELS[lv];
  const bal=S.solar+S.windW-S.cons,okp=Number.isFinite(p);
  $("pct").textContent=okp?p:"--";$("fill").style.width=(okp?p:0)+"%";
  $("barwrap").setAttribute("aria-label","Carga de batería: "+p+" por ciento");
  $("vsol").textContent=fmt(S.solar)+" W";$("vsolv").textContent=S.solarV>0?fmt(S.solarV)+" V en el panel":"Sin generación";
  $("vwind").textContent=fmt(S.windMs)+" m/s";$("vwindp").textContent="≈ "+fmt(S.windW)+" W estimados";
  $("vtemp").textContent=Number.isFinite(S.tempC)?fmt(S.tempC)+" °C":"--";
  $("ttxt").textContent=S.tempC<25?"Agua fría":S.tempC<=60?"Útil para calentar":"Muy caliente: revisar";
  $("vcons").textContent=fmt(S.cons)+" W";
  $("vbal").textContent=(bal>0?"+":"")+fmt(bal)+" W";$("btxt").textContent=bal>=0?"La batería se carga":"La batería se descarga";
  if(bal>=0)$("autoval").textContent="cargando";
  else{const h=(S.batt/100*S.cap)/(-bal),hh=Math.floor(h);$("autoval").textContent=hh+" h "+Math.round((h-hh)*60)+" min";}
  $("alert").className="alert lv-"+lv;$("aic").textContent=L.i;$("ah").textContent=L.n+" · Batería al "+p+" %";$("ap").textContent=L.t;
  if(S.lastLevel!==lv){if(S.lastLevel!==null)addEvent(lv,"Nivel de alerta: "+L.n+" (batería "+p+" %).");S.lastLevel=lv;}
  const c=$("conn");c.textContent=S.online?"Conectado":"Sin conexión";c.className="chip "+(S.online?"ok":"bad");
  const s=$("src");s.textContent=S.source==="real"?"Datos reales":"Datos simulados";s.className="chip "+(S.source==="real"?"ok":"sim");
  $("upd").textContent="Última lectura "+(S.last||"--");
  document.querySelectorAll("input[name=mode]").forEach(i=>i.checked=i.value===S.mode);
  $("modenote").textContent="Modo actual: "+MN[S.mode]+" · consumo previsto "+consFor(S.mode)+" W.";
  $("loads").innerHTML=LOADS.map(l=>{const on=MODES[S.mode].includes(l.id);
    return "<tr><td>"+l.name+"</td><td>"+l.prio+"</td><td>"+l.w+"</td><td class='"+(on?"on":"off")+"'>"+(on?"✔ Encendida":"✖ Apagada")+"</td></tr>";}).join("");
  $("events").innerHTML=S.events.length?S.events.map(e=>"<li><span class='tag' style='color:var(--"+TAGC[e.level]+")'>"+LEVELS[e.level].i+" "+LEVELS[e.level].n+"</span> "+e.t+" · "+e.text+"</li>").join(""):"<li>Sin eventos todavía.</li>";
  drawChart();
}
function drawChart(){
  const H=S.hist,W=600,Ht=200,m={l:42,r:10,t:10,b:22};
  if(H.length<2){$("chart").innerHTML="";return;}
  const max=Math.max(30,...H.map(h=>Math.max(h.g,h.c)))*1.1;
  const x=i=>m.l+i*(W-m.l-m.r)/(H.length-1),y=v=>Ht-m.b-v/max*(Ht-m.t-m.b);
  const pts=k=>H.map((h,i)=>x(i).toFixed(1)+","+y(h[k]).toFixed(1)).join(" ");
  let g="";
  for(let k=0;k<=4;k++){const v=max*k/4;g+="<line x1='"+m.l+"' x2='"+(W-m.r)+"' y1='"+y(v)+"' y2='"+y(v)+"' stroke='rgba(255,255,255,.16)'/><text x='"+(m.l-6)+"' y='"+(y(v)+4)+"' font-size='11' text-anchor='end' fill='#b0c3cd'>"+Math.round(v)+" W</text>";}
  $("chart").innerHTML=g+"<polyline points='"+pts("g")+"' fill='none' stroke='#ffc94d' stroke-width='4'/><polyline points='"+pts("c")+"' fill='none' stroke='#eef5f8' stroke-width='3' stroke-dasharray='8 5'/>"+
    "<text x='"+m.l+"' y='"+(Ht-4)+"' font-size='11' fill='#b0c3cd'>más antiguo</text><text x='"+(W-m.r)+"' y='"+(Ht-4)+"' font-size='11' text-anchor='end' fill='#b0c3cd'>ahora</text>";
  const l=H[H.length-1];$("chartsum").textContent="Ahora: generación "+fmt(l.g)+" W, consumo "+fmt(l.c)+" W.";
}
async function tick(){
  if(S.api)await fetchApi();else simulate();
  S.last=clock();S.hist.push({g:S.solar+S.windW,c:S.cons});S.hist=S.hist.slice(-40);render();
}
document.querySelectorAll("input[name=mode]").forEach(i=>i.addEventListener("change",()=>{setMode(i.value,false);render();}));
$("apply").onclick=()=>{S.api=$("api").value.trim();S.cap=Math.max(1,+$("cap").value||60);
  addEvent("normal",S.api?"Conectando con "+S.api:"Usando datos simulados.");S.hist=[];tick();};
$("cut").onclick=e=>{S.cut=!S.cut;e.target.setAttribute("aria-pressed",S.cut);
  e.target.textContent=S.cut?"Restablecer generación":"Simular corte de generación";
  addEvent(S.cut?"high":"normal",S.cut?"Generación cortada (simulación).":"Generación restablecida.");};
$("reset").onclick=()=>{S.batt=78;S.mode="normal";S.hist=[];S.events=[];S.cut=false;S.lastLevel=null;S.tempC=22;
  $("cut").textContent="Simular corte de generación";$("cut").setAttribute("aria-pressed","false");tick();};
$("kits").innerHTML=KITS.map(k=>"<tr><td>"+k.n+"</td><td>"+k.t+"</td><td><select aria-label='En uso por, "+k.n+"'><option>Libre</option>"+
  [1,2,3,4,5,6].map(g=>"<option>Grupo "+g+"</option>").join("")+"</select></td></tr>").join("");
if(!RM)document.querySelectorAll(".tilt").forEach(c=>{
  c.addEventListener("pointermove",e=>{const b=c.getBoundingClientRect(),x=(e.clientX-b.left)/b.width-.5,y=(e.clientY-b.top)/b.height-.5;
    c.style.transform="perspective(800px) rotateX("+(-y*5)+"deg) rotateY("+(x*5)+"deg)";});
  c.addEventListener("pointerleave",()=>c.style.transform="");});
addEvent("normal","Panel iniciado con datos simulados.");
tick();setInterval(tick,3000);
