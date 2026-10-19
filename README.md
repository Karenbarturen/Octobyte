# Octobyte

Backend Spring Boot para recibir datos de un ESP32 por puerto serial, guardarlos en PostgreSQL y exponerlos mediante API REST. Frontend vanilla con mapa, gráfico y panel de energía.

## Requisitos

- Java 21
- Maven 3.9+
- Docker y Docker Compose

## Estructura

```
backend/          # Backend Spring Boot
frontend/         # Frontend HTML/JS (versión actualizada desde rama master)
docker-compose.yml
```

## Cómo levantar

### 1. Base de datos

```bash
cd backend
docker compose up -d
```

PostgreSQL queda disponible en `localhost:5433` (puerto externo), base `octobyte`, usuario/contraseña `octobyte`.

### 2. Backend

```bash
cd backend
mvn clean package -DskipTests
java -jar target/octobyte-backend-1.0.0.jar
```

El backend escucha en `http://localhost:8080`.

> En Windows, el backend intentará abrir el puerto serial configurado en `esp32.serial.default-port` (por defecto `COM3`). Si no encuentra el ESP32, funcionará en modo simulado.

### 3. Frontend

```bash
cd frontend
python -m http.server 3001
```

Abrí en el navegador:

```
http://localhost:3001
```

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Info del backend (compatibilidad legacy) |
| GET | `/api/v1/status` | Último registro en formato enriquecido |
| GET | `/api/v1/records?cursor=0&limit=2` | Historial paginado |
| POST | `/api/v1/records` | Insertar registro manual |
| GET | `/api/registro-completo/ultimo` | Alias usado por el frontend master |
| GET | `/api/totales` | Alias de `/api/v1/records` |
| POST | `/api/totales` | Alias de `POST /api/v1/records` |
| GET | `/api/v1/operation-modes` | Listar modos operativos |
| POST | `/api/v1/operation-modes` | Crear modo operativo |
| PUT | `/api/v1/operation-modes/{id}/activate` | Activar modo operativo |
| GET | `/signal` | Alias legacy de `/api/v1/status` |
| GET | `/signals` | Alias legacy de `/api/v1/records?limit=100` |

## Datos del ESP32

El backend lee el puerto serial cada segundo, parsea bloques delimitados por:

```
===== MEDICIONES =====
...
======================
```

Extrae:
- `Luz (ADC)` → lumens
- `Velocidad tangencial estimada` → wind_speed
- `Temperatura` → temperature
- `Voltaje estimado del panel` → voltage

Los campos faltantes se completan automáticamente:
- `operation_mode` → modo operativo activo
- `current`, `battery_level`, `stored_energy` → aleatorios realistas
- `generation_power` → `voltage * current`
- `consumption_power` → aleatorio menor que generación
- `alert_level` → calculado por umbrales y devuelto como string (`normal`, `low`, `medium`, `high`, `critical`)

## Simulador

Si no llegan datos del ESP32 durante 60 segundos, el backend genera registros simulados cada segundo.

## Frontend

El frontend actualizado (rama master) consulta `GET /api/registro-completo/ultimo` cada 3 segundos y muestra:
- Estado de batería con alertas visuales
- Métricas de solar, viento, termosolar, consumo y balance
- Mapa de Copahue con kits energéticos
- Gráfico de generación y consumo
- Eventos y alertas
