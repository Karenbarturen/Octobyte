# Octobyte

Sistema de energía autónoma para Copahue, desarrollado para Navathon III por el Grupo 3. La aplicación recibe mediciones de un nodo energético, las conserva en PostgreSQL y las muestra en un panel web con información de batería, generación, consumo y alertas.

## Arquitectura y stack

- **API (`api/`)**: servicio REST construido con Java 21 y Spring Boot. Valida las mediciones recibidas, las guarda y expone la lectura más reciente y el historial. Maven Wrapper (`api/mvnw` y `api/mvnw.cmd`) permite ejecutar el proyecto sin instalar Maven globalmente.
- **Base de datos**: PostgreSQL almacena las lecturas energéticas. La conexión se configura en `api/src/main/resources/application.properties`.
- **Panel web**: `index.html`, `styles.css` y `app.js` implementan el dashboard sin un paso de compilación. El mapa usa Leaflet. El JavaScript consulta el estado de la API cada tres segundos y actualiza las métricas y visualizaciones.
- **Firmware ESP32 (`src/main.cpp`)**: programa en C++ para microcontrolador ESP32 con PlatformIO. Lee sensores reales de luz, anemómetro, temperatura (Dallas DS18B20) y panel solar, procesa las mediciones de forma no bloqueante y las envía mediante peticiones HTTP POST a la API en la red local.
- **Simulador (`simulador_esp32.py`)**: genera mediciones de prueba y las envía a la API cada quince segundos mediante `POST /api/v1/records`. Es una herramienta independiente; `start.sh` y `start.bat` no lo instalan ni lo ejecutan.

## Detalles técnicos

### Zona horaria

Las fechas de las lecturas se guardan y se devuelven en la zona `America/Argentina/Buenos_Aires`, con el desplazamiento `-03:00`. Esto mantiene las marcas de tiempo alineadas con la hora local de Copahue y evita discrepancias entre los dispositivos que generan mediciones y las que muestra el panel. Al ingerir una lectura, el servidor asigna la fecha actual en esa zona; al responder, convierte el timestamp al mismo huso horario.

### Contrato de API y roles

La API separa la escritura de las consultas:

- **Ingesta — `POST /api/v1/records`**: recibe una lectura con sus campos principales y el objeto `data` del contrato. La lectura validada se guarda en PostgreSQL. El simulador y el ESP32 usan este endpoint.
- **Estado actual — `GET /api/v1/status`**: devuelve el registro más reciente del nodo `energy_node_01`, con `data` y `alert_level` en el contrato de respuesta. El dashboard consume este endpoint.
- **Historial — `GET /api/v1/records`**: devuelve una colección paginada de lecturas, en orden descendente por `id` (`ORDER BY id DESC` a través de Spring Data). Así, los registros más nuevos aparecen primero. Acepta `limit` (por defecto `100`, máximo `1000`) y `cursor` para continuar la paginación.

La lectura de estado también selecciona el registro del nodo ordenando por `id` descendente, por lo que presenta la última medición registrada.

## Puesta en marcha

### Requisitos

- Java 21 disponible en `PATH`.
- PostgreSQL ejecutándose y una base de datos llamada `octobyte`.
- Credenciales de conexión válidas en `api/src/main/resources/application.properties` (por defecto, el usuario configurado es `postgres`). No guardes credenciales reales en el repositorio.
- Para servir el dashboard localmente, un servidor web estático, por ejemplo Live Server en VS Code.
- Para compilar el firmware del microcontrolador, VS Code con la extensión PlatformIO IDE (`platformio.ini` configurado para `esp32dev`).

Si todavía no existe la base, créala en PostgreSQL con:

```sql
CREATE DATABASE octobyte;