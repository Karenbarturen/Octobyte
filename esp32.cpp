#include <WiFi.h>
#include <HTTPClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>

const char* ssid = "ESP32_AP_MAIN";
const char* password = "copahue-esp32-ap-main";
const char* serverUrl = "http://IP_DE_TU_PC:8080/api/v1/records";

// -------- PINES --------
const int pinLuz = 33;
const int pinAnemometroAD = 34;
const int pinTemperatura = 4;
const int pinPanel = 35;

// -------- ANEMÓMETRO --------
const float DIAMETRO_M = 0.10f;
const float CIRCUNFERENCIA_M =
    3.14159265f * DIAMETRO_M;

// Ajustar después de comprobar cuántos ciclos
// detecta el sensor por cada vuelta completa.
const float PULSOS_POR_VUELTA = 1.0f;

const int UMBRAL_ALTO = 40;
const int UMBRAL_BAJO = 10;
const unsigned long DURACION_MINIMA_MS = 3;

// Promedio móvil de los últimos 5 segundos
const int SEGUNDOS_VENTANA = 5;
unsigned long pulsosPorSegundo[SEGUNDOS_VENTANA] = {0};

int indiceSegundo = 0;
int segundosRegistrados = 0;

bool estadoAltoViento = false;
unsigned long inicioPulsoMs = 0;
unsigned long pulsosActuales = 0;

float velocidadTangencial = 0.0f;

// -------- TEMPERATURA --------
OneWire oneWire(pinTemperatura);
DallasTemperature sensorTemperatura(&oneWire);

float temperatura = DEVICE_DISCONNECTED_C;
bool conversionPendiente = false;
unsigned long inicioConversion = 0;

// -------- MEDICIONES --------
int lecturaLuz = 0;
int lecturaViento = 0;
int lecturaPanel = 0;

// -------- TEMPORIZADORES --------
unsigned long ultimaLecturaUs = 0;
unsigned long ultimaImpresionMs = 0;

const unsigned long INTERVALO_LECTURA_US = 1000;
const unsigned long INTERVALO_IMPRESION_MS = 1000;

const float UMBRAL_TEMPERATURA_ALERTA_C = 60.0f;
const float DESCARGA_BATERIA_POR_CICLO = 0.01f;

float voltajePanel = 0.0f;
float consumptionPower = 50.0f;
float batteryLevel = 100.0f;

void enviarDatosServidor() {
  consumptionPower += (random(0, 21) - 10) / 10.0f;
  if (consumptionPower < 35.0f) {
    consumptionPower = 35.0f;
  } else if (consumptionPower > 55.0f) {
    consumptionPower = 55.0f;
  }

  batteryLevel -= DESCARGA_BATERIA_POR_CICLO;
  if (batteryLevel < 20.0f) {
    batteryLevel = 20.0f;
  }

  float generationPower = voltajePanel * 0.5f;
  int alertLevel =
      temperatura != DEVICE_DISCONNECTED_C &&
      temperatura > UMBRAL_TEMPERATURA_ALERTA_C ? 1 : 0;

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Error: Wi-Fi desconectado; no se enviaron los datos");
    return;
  }

  HTTPClient http;
  if (!http.begin(serverUrl)) {
    Serial.println("Error: no se pudo iniciar la petición HTTP");
    return;
  }

  http.addHeader("Content-Type", "application/json");

  String payload = "{\"generation_power\":" + String(generationPower, 2) +
      ",\"consumption_power\":" + String(consumptionPower, 1) +
      ",\"battery_level\":" + String(batteryLevel, 1) +
      ",\"alert_level\":" + String(alertLevel) + "}";

  int httpCode = http.POST(payload);
  if (httpCode > 0) {
    Serial.print("Respuesta del servidor: HTTP ");
    Serial.println(httpCode);
  } else {
    Serial.print("Error al enviar datos: ");
    Serial.println(http.errorToString(httpCode));
  }

  http.end();
}

void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  Serial.print("Conectando a Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConectado a Wi-Fi");

  analogReadResolution(12);

  pinMode(pinLuz, INPUT);
  pinMode(pinAnemometroAD, INPUT);
  pinMode(pinPanel, INPUT);

  randomSeed(analogRead(pinLuz));
  consumptionPower = random(350, 551) / 10.0f;
  batteryLevel = random(850, 1001) / 10.0f;

  sensorTemperatura.begin();
  sensorTemperatura.setWaitForConversion(false);

  sensorTemperatura.requestTemperatures();
  inicioConversion = millis();
  conversionPendiente = true;

  ultimaLecturaUs = micros();
  ultimaImpresionMs = millis();

  Serial.println("Sistema de monitoreo iniciado");
  Serial.println("Velocidad actualizada cada segundo");
}

void loop() {
  unsigned long ahoraUs = micros();
  unsigned long ahoraMs = millis();

  // 1. LECTURAS DE SENSORES: objetivo cada 1 ms
  if ((unsigned long)(ahoraUs - ultimaLecturaUs)
      >= INTERVALO_LECTURA_US) {

    ultimaLecturaUs = ahoraUs;

    lecturaLuz = analogRead(pinLuz);
    lecturaViento = analogRead(pinAnemometroAD);
    lecturaPanel = analogRead(pinPanel);

    // 2. DETECCIÓN DE CICLOS DEL ANEMÓMETRO
    // Estado alto: ADC > 40
    // Estado bajo: ADC < 10
    // La histéresis evita cambios por pequeñas variaciones.

    if (!estadoAltoViento && lecturaViento > UMBRAL_ALTO) {
      estadoAltoViento = true;
      inicioPulsoMs = ahoraMs;
    }
    else if (estadoAltoViento && lecturaViento < UMBRAL_BAJO) {
      unsigned long duracionPulso =
          ahoraMs - inicioPulsoMs;

      if (duracionPulso >= DURACION_MINIMA_MS) {
        pulsosActuales++;
      }

      estadoAltoViento = false;
    }
  }

  // 3. TEMPERATURA SIN BLOQUEAR EL PROGRAMA
  if (conversionPendiente &&
      (unsigned long)(ahoraMs - inicioConversion) >= 750) {

    temperatura = sensorTemperatura.getTempCByIndex(0);
    conversionPendiente = false;
  }

  if (!conversionPendiente &&
      (unsigned long)(ahoraMs - inicioConversion) >= 1000) {

    sensorTemperatura.requestTemperatures();
    inicioConversion = ahoraMs;
    conversionPendiente = true;
  }

  // 4. ACTUALIZAR LA VELOCIDAD CADA SEGUNDO
  if ((unsigned long)(ahoraMs - ultimaImpresionMs)
      >= INTERVALO_IMPRESION_MS) {

    ultimaImpresionMs = ahoraMs;

    // Guardar los pulsos del segundo recién terminado
    pulsosPorSegundo[indiceSegundo] = pulsosActuales;
    pulsosActuales = 0;

    // Avanzar al siguiente segundo de la ventana
    indiceSegundo = (indiceSegundo + 1) % SEGUNDOS_VENTANA;

    // Preparar el espacio que se reutilizará
    pulsosPorSegundo[indiceSegundo] = 0;

    if (segundosRegistrados < SEGUNDOS_VENTANA) {
      segundosRegistrados++;
    }

    // Sumar los pulsos de la ventana disponible
    unsigned long totalPulsos = 0;

    for (int i = 0; i < SEGUNDOS_VENTANA; i++) {
      totalPulsos += pulsosPorSegundo[i];
    }

    float tiempoVentana = (float)segundosRegistrados;

    float vueltasPorSegundo =
        (totalPulsos / PULSOS_POR_VUELTA) / tiempoVentana;

    velocidadTangencial =
        CIRCUNFERENCIA_M * vueltasPorSegundo;

    // 5. MOSTRAR TODAS LAS MEDICIONES
    Serial.println("\n===== MEDICIONES =====");

    Serial.print("Luz (ADC): ");
    Serial.println(lecturaLuz);

    Serial.print("Anemometro (ADC): ");
    Serial.println(lecturaViento);

    Serial.print("Ciclos en la ventana: ");
    Serial.println(totalPulsos);

    Serial.print("Velocidad tangencial estimada: ");
    Serial.print(velocidadTangencial, 2);
    Serial.println(" m/s");

    Serial.print("Temperatura: ");

    if (temperatura == DEVICE_DISCONNECTED_C) {
      Serial.println("Error: sensor desconectado");
    } else {
      Serial.print(temperatura, 2);
      Serial.println(" C");
    }

    Serial.print("Panel solar (ADC): ");
    Serial.println(lecturaPanel);

    float voltajeGPIO = lecturaPanel * 3.3f / 4095.0f;
    voltajePanel = voltajeGPIO * 2.0f;

    Serial.print("Voltaje estimado del panel: ");
    Serial.print(voltajePanel, 2);
    Serial.println(" V");

    Serial.println("======================");

    enviarDatosServidor();
  }
}