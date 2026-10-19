# Energía autónoma para Copahue

Prototipo web para visualizar y monitorear kits de generación renovable en Copahue. Incluye una batería compartida, un mapa satelital interactivo, datos energéticos, modos operacionales automáticos y alertas.

## Funciones

- Indicador de carga, energía disponible y autonomía estimada de la batería.
- Mapa satelital de Copahue con tres kits seleccionables: fotovoltaico, eólico y termosolar.
- Panel de monitoreo por kit con generación, consumo estimado, energía acumulada y reserva.
- Cambio automático del modo operacional según los umbrales de batería configurados:
  - **Normal:** desde el umbral de ahorro hasta el 100 %.
  - **Ahorro:** por debajo del umbral de ahorro y por encima del umbral de emergencia.
  - **Emergencia:** en el umbral de emergencia o por debajo.
- Aviso visual y registro cuando cambia el modo operacional.
- Gráfico de generación y consumo, historial de lecturas y estado energético de los kits.
- Configuración de capacidad de batería y umbrales de ahorro y emergencia.

Los valores iniciales son 60 Wh de capacidad, 40 % para ahorro y 10 % para emergencia. Los parámetros y el historial se conservan durante la sesión actual; al recargar la página vuelven a sus valores iniciales.

## Requisitos
- Un navegador moderno.
- Conexión a internet para cargar Leaflet, las fuentes externas y las imágenes satelitales de Esri.
- Python 3 para iniciar un servidor local, o una extensión equivalente como Live Server en Visual Studio Code.

No hace falta instalar dependencias con npm ni compilar el proyecto.

## Ejecutar localmente
1. Descargá o cloná este repositorio.
2. Abrí una terminal en la carpeta del proyecto.
3. Iniciá un servidor web local:

   ```bash
   py -m http.server 8080
   ```

   Si `py` no está disponible, probá:

   ```bash
   python -m http.server 8080
   ```

4. Abrí [http://localhost:8080](http://localhost:8080) en el navegador.
5. Para detener el servidor, volvé a la terminal y presioná `Ctrl+C`.

También se puede abrir la carpeta en Visual Studio Code e iniciar un servidor con la extensión Live Server.

## Estructura
Octobyte/
├── index.html          # Estructura y contenido de la página
├── styles.css          # Estilos y diseño adaptable
├── app.js              # Simulación, mapa, gráficos y lógica de los modos
├── img/
│   └── copahue.jpg     # Imagen de portada
└── README.md


## Ajustar las posiciones de los kits

Las coordenadas de demostración están en `KIT_DATA`, dentro de `app.js`, en formato `[latitud, longitud]`:

```js
position: [-37.81755, -71.1009]
```

Reemplazá cada par por las coordenadas reales de instalación. El mapa está fijo en una zona de Copahue y no permite alejarse ni desplazarse.

## Tecnologías

- HTML, CSS y JavaScript sin framework.
- [Leaflet](https://leafletjs.com/) para el mapa.
- Imágenes satelitales de Esri.
