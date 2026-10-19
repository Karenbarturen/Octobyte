import time
import random
import uuid
from datetime import datetime, timedelta, timezone

import requests

URL = "http://localhost:8080/api/registro-completo"
ARGENTINA_TIMEZONE = timezone(timedelta(hours=-3))

print("==================================================")
print(" Simulador de ESP32 - Grupo 3 (Energía Autónoma)")
print("==================================================")
print(f"Enviando datos a /records & /status cada 10 segundos...\n")

while True:
    record_id = str(uuid.uuid4())   
    timestamp = datetime.now(ARGENTINA_TIMEZONE).isoformat(timespec="seconds")
    voltage = round(random.uniform(11.8, 14.2), 2)
    current = round(random.uniform(1.2, 5.8), 2)
    generation_power = round(random.uniform(900.0, 1450.0), 2)
    consumption_power = round(random.uniform(600.0, 950.0), 2)
    stored_energy = random.randint(4200, 5800)
    battery_level = round(random.uniform(70.0, 98.5), 2)

    payload = {
        "schema_version": "1.0.0",
        "record_id": record_id,
        "record_type": "reading",
        "node_id": "energy_node_01",
        "group": 3,
        "subsystem": "Sistema energético autónomo",
        "timestamp": timestamp,
        "status": "active",
        "alert_level": "normal",
        "data": {
            "voltage": voltage,
            "current": current,
            "generation_power": generation_power,
            "consumption_power": consumption_power,
            "stored_energy": stored_energy,
            "battery_level": battery_level,
            "operation_mode": "automatic"
        },
        "metadata": {
            "environment": "test",
            "source_type": "simulated",
            "alert_evaluated": False,
            "clock_synced": False,
            "units": {
                "voltage": "V",
                "current": "A",
                "generation_power": "W",
                "consumption_power": "W",
                "stored_energy": "Wh",
                "battery_level": "%",
                "operation_mode": "string"
            },
            "quality": {
                "voltage": "valid",
                "current": "valid",
                "generation_power": "valid",
                "consumption_power": "valid",
                "stored_energy": "valid",
                "battery_level": "valid",
                "operation_mode": "valid"
            }
        }
    }

    try:
        print(f"[ENVIANDO] record_id={record_id} | timestamp={timestamp}")
        response = requests.post(URL, json=payload, timeout=5)
        request_status = f"HTTP {response.status_code} {response.reason}"

        if response.status_code in (200, 201):
            print(f"[RESPUESTA] record_id={record_id} | estado=ÉXITO ({request_status})\n")
        else:
            print(f"[RESPUESTA] record_id={record_id} | estado=ERROR ({request_status}): {response.text}\n")
    except requests.exceptions.RequestException as error:
        print(f"[RESPUESTA] record_id={record_id} | estado=ERROR de conexión: {error}\n")

    # Pausa de 15 segundos
    time.sleep(15)