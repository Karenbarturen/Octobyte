# Octobyte

Sistema de energía autónoma para Copahue, desarrollado para Navathon III por el Grupo 3. La aplicación recibe mediciones de un nodo energético, las conserva en PostgreSQL y las muestra en un panel web con información de batería, generación, consumo y alertas.

## Arquitectura y stack

- **API (`api/`)**: servicio REST construido con Java 21 y Spring Boot. Valida las mediciones recibidas, las guarda y expone la lectura más reciente y el historial. Maven Wrapper (`api/mvnw` y `api/mvnw.cmd`) permite ejecutar el proyecto sin instalar Maven globalmente.
- **Base de datos**: PostgreSQL almacena las lecturas energéticas. La conexión se configura en `api/src/main/resources/application.properties`.
- **Panel web**: `index.html`, `styles.css` y `app.js` implementan el dashboard sin un paso de compilación. El mapa usa Leaflet. El JavaScript consulta el estado de la API cada tres segundos y actualiza las métricas y visualizaciones.
- **Simulador (`simulador_esp32.py`)**: genera mediciones de prueba y las envía a la API cada quince segundos mediante `POST /api/v1/records`. Es una herramienta independiente; `start.sh` y `start.bat` no lo instalan ni lo ejecutan.

## Detalles técnicos

### Zona horaria

Las fechas de las lecturas se guardan y se devuelven en la zona `America/Argentina/Buenos_Aires`, con el desplazamiento `-03:00`. Esto mantiene las marcas de tiempo alineadas con la hora local de Copahue y evita discrepancias entre los dispositivos que generan mediciones y las que muestra el panel. Al ingerir una lectura, el servidor asigna la fecha actual en esa zona; al responder, convierte el timestamp al mismo huso horario.

### Contrato de API y roles

La API separa la escritura de las consultas:

- **Ingesta — `POST /api/v1/records`**: recibe una lectura con sus campos principales y el objeto `data` del contrato. La lectura validada se guarda en PostgreSQL. El simulador usa este endpoint.
- **Estado actual — `GET /api/v1/status`**: devuelve el registro más reciente del nodo `energy_node_01`, con `data` y `alert_level` en el contrato de respuesta. El dashboard consume este endpoint.
- **Historial — `GET /api/v1/records`**: devuelve una colección paginada de lecturas, en orden descendente por `id` (`ORDER BY id DESC` a través de Spring Data). Así, los registros más nuevos aparecen primero. Acepta `limit` (por defecto `100`, máximo `1000`) y `cursor` para continuar la paginación.

La lectura de estado también selecciona el registro del nodo ordenando por `id` descendente, por lo que presenta la última medición registrada.

## Puesta en marcha

### Requisitos

- Java 21 disponible en `PATH`.
- PostgreSQL ejecutándose y una base de datos llamada `octobyte`.
- Credenciales de conexión válidas en `api/src/main/resources/application.properties` (por defecto, el usuario configurado es `postgres`). No guardes credenciales reales en el repositorio.
- Para servir el dashboard localmente, un servidor web estático, por ejemplo Live Server en VS Code.

Si todavía no existe la base, créala en PostgreSQL con:

```sql
CREATE DATABASE octobyte;
```

Configura en `application.properties` un usuario y contraseña con permiso para conectarse a esa base.

### Iniciar la API

Desde la raíz del proyecto:

- **Windows CMD**: ejecuta `start.bat`.
- **Linux, macOS o Git Bash**: ejecuta `./start.sh`. Si el archivo no conserva el permiso de ejecución al descargarlo, usa `chmod +x start.sh` y vuelve a ejecutarlo.

Los scripts abren `index.html` en el navegador predeterminado y luego entran en `api/` para ejecutar el Maven Wrapper con `spring-boot:run`. LRevisa todo el proyecto y reemplaza cualquier mención de 'Grupo 6' por 'Grupo 3' (incluyendo el README.md, comentarios y documentación), ya que el equipo oficial es el Grupo 3.a API queda disponible en `http://localhost:8080`. La base de datos debe estar disponible antes de arrancar los scripts.

### Conectar el dashboard

1. Inicia PostgreSQL y verifica la configuración de conexión de `api/src/main/resources/application.properties`.
2. Ejecuta el script correspondiente a tu sistema. Este abre el dashboard automáticamente antes de iniciar Spring Boot.
3. El dashboard consulta `http://localhost:8080/api/v1/status`. La configuración CORS de la API permite las solicitudes a `/api/**` desde otros orígenes.

El estado no estará disponible hasta que se registre una primera lectura. Para mostrar mediciones de prueba, ejecuta `simulador_esp32.py` por separado en un entorno Python con la dependencia `requests` instalada. El simulador no es necesario para iniciar la API ni el dashboard.
