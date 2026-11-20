# NASA Explorer

Aplicación Android nativa en Kotlin y Jetpack Compose para consultar contenido astronómico publicado por NASA, explorar objetos cercanos a la Tierra, guardar elementos localmente y solicitar explicaciones contextuales a un backend OpenRouter opcional.

## Descripción académica

### Planteamiento del problema

La información astronómica pública se encuentra distribuida entre diferentes servicios y presenta vocabulario y magnitudes que pueden resultar difíciles de interpretar para personas no especialistas. Se requiere una aplicación móvil que reúna información oficial, haga explícitas sus unidades y permita conservar contenido de interés sin sustituir ni alterar los datos de origen.

### Objetivo general

Desarrollar una aplicación Android que facilite la consulta y comprensión de publicaciones astronómicas de NASA mediante una interfaz accesible, persistencia local y un asistente contextual desacoplado de la aplicación.

### Objetivos específicos

- Consultar y presentar APOD y el feed de objetos cercanos a la Tierra (NeoWs).
- Permitir filtrar, ordenar y revisar estadísticas calculadas exclusivamente a partir de las respuestas de NeoWs.
- Persistir favoritos y preferencias de apariencia en el dispositivo.
- Aplicar separación MVVM y Repository Pattern para aislar presentación, servicios y almacenamiento.
- Proporcionar explicaciones opcionales basadas en el contexto recibido desde NASA, manteniendo las credenciales de OpenRouter fuera del APK.

### Justificación

La aplicación ofrece una experiencia móvil unificada para explorar contenido astronómico primario. La separación de capas facilita el mantenimiento y las pruebas; Room conserva favoritos sin conexión y DataStore mantiene preferencias. El asistente se ejecuta en un servicio separado para no exponer credenciales de OpenRouter en el cliente. La interfaz indica cuando los datos no están disponibles en lugar de sustituirlos por valores supuestos.

## Tecnologías

- Kotlin, Android Gradle Plugin, Gradle Kotlin DSL y Jetpack Compose Material 3.
- ML Kit Translation para traducción local bajo demanda de informes DONKI.
- Navigation Compose, Lifecycle ViewModel y Kotlin Coroutines/StateFlow.
- Retrofit/Gson para los servicios REST y Coil Compose para imágenes.
- Leaflet y mosaicos de OpenStreetMap dentro de WebView para ubicar eventos EONET sin requerir una clave de mapas.
- Room/KSP para favoritos y Preferences DataStore para el tema.
- FastAPI, Pydantic y HTTPX para el backend; OpenRouter se invoca desde el servidor.

Las versiones y plugins Android están declarados en `gradle/libs.versions.toml`, `build.gradle.kts` y `app/build.gradle.kts`. Android Gradle Plugin, Kotlin y Gradle requieren las versiones de SDK/JDK compatibles definidas por Android Studio y el wrapper del proyecto.

## Arquitectura

La capa `presentation` contiene pantallas Compose, ViewModels y navegación. Los ViewModels exponen estados observables mediante StateFlow. La capa `data` contiene DTO, interfaces Retrofit, DAO, entidades, preferencias y repositorios. `domain/model` contiene las funciones puras de filtrado, ordenamiento y estadísticas de NeoWs. `ui/theme` concentra colores y tipografía. La paleta parte del azul marino y azul NASA del logotipo, con cian y rojo reservados para acentos; los fondos, superficies y tonos de texto se ajustan por separado para mantener contraste en ambos temas.

El asistente sigue el flujo Android → `AssistantViewModel` → `AssistantRepository` → FastAPI → OpenRouter. El cliente envía únicamente la pregunta y el contexto mostrado por la aplicación; la API key de OpenRouter se lee del entorno del servidor.

El idioma de la interfaz puede cambiarse entre español e inglés desde Ajustes y se conserva mediante DataStore. El asistente recibe el idioma seleccionado para responder en el mismo idioma. Los títulos, explicaciones y créditos recibidos de NASA se conservan en su idioma de publicación para no modificar el contenido de origen.

## Módulos funcionales

- **Inicio:** portada visual con accesos a APOD y NeoWs, favoritos y feeds independientes de clima espacial (DONKI) y eventos naturales activos (EONET). El encabezado principal rota frases predefinidas cada cinco segundos, sin solicitudes a servicios de IA. DONKI muestra una notificación inicial y permite expandir el resto; su detalle presenta primero las secciones clave, ofrece el informe completo y traducción opcional al español en el dispositivo mediante ML Kit Translation (el modelo se descarga al solicitarla por primera vez). EONET presenta sus eventos en un carrusel deslizable. Su opción **Mapa** carga hasta 100 eventos activos como marcadores seleccionables. Al seleccionar un evento se muestran su tipo/categoría, fecha, magnitud y la descripción individual publicada por EONET si está disponible; si no hay descripción, se presenta una definición general de la categoría basada en NASA EONET, claramente diferenciada de los datos propios del evento. Para polígonos, la posición es el centro aproximado de las coordenadas, no una ubicación exacta. El mapa usa Leaflet desde un CDN y mosaicos de OpenStreetMap, por lo que requiere conexión a Internet y puede depender de la disponibilidad de esos servicios. Al tocar una tarjeta EONET se abre un detalle emergente con imagen satelital ampliada, categoría, fecha y magnitud; la imagen muestra el área asociada y no necesariamente el fenómeno de cerca. Al tocar una tarjeta DONKI se abre su detalle dentro de la app, no la respuesta JSON del endpoint.
- **APOD:** consume la colección vigente `science.nasa.gov/wp-json/wp/v2/apod-basic`, limpia el HTML explicativo, muestra crédito y ofrece un detalle dedicado para la historia y el contenido multimedia.
- **Asteroides:** consume el feed `api.nasa.gov/neo/rest/v1/feed`, limita cada consulta a siete días, permite seleccionar fechas, buscar y ordenar. El detalle busca fotografías en la biblioteca NASA Images y solo muestra resultados cuyo título identifica el asteroide y que no están marcados como ilustración, render o material gráfico. Como la mayoría de los objetos de NeoWs no tiene una fotografía individual, la app informa cuando no encuentra una coincidencia fiable; no sustituye la fotografía por una ilustración.
- **Favoritos:** agrega y elimina registros únicos de APOD y asteroides mediante Room.
- **Ajustes:** tema claro, oscuro o del sistema, e idioma español/inglés persistidos con DataStore; sección «Acerca de» con versión, propósito, fuentes NASA y aclaración de afiliación.
- **Asistente:** se abre desde el botón flotante global en Inicio, APOD, asteroides y sus detalles; usa los datos APOD/NeoWs de la publicación u objeto abierto. En Inicio responde preguntas generales sin exigir seleccionar un objeto, y solo aclara que faltan datos si la pregunta requiere información de un objeto concreto no disponible. Al enviar una pregunta limpia el campo para la siguiente; las respuestas exitosas se guardan en un historial local persistente de hasta 50 conversaciones, desde donde se pueden borrar entradas individuales o todo el historial. No estima ni predice impactos.

## Persistencia

Room usa la base `nasa_explorer_database`, tabla `favorites`, con `id` como clave primaria, tipo, título, descripción, URL de imagen y fecha. Los identificadores APOD se basan en la fecha; los de NeoWs, en el identificador NASA. DataStore Preferences guarda `theme_mode` (`LIGHT`, `DARK` o `SYSTEM`).

## Endpoints

| Servicio | Método y ruta | Uso |
|---|---|---|
| APOD básico de NASA | `GET https://science.nasa.gov/wp-json/wp/v2/apod-basic` | Publicación astronómica actual |
| NASA NeoWs | `GET https://api.nasa.gov/neo/rest/v1/feed?start_date=...&end_date=...&api_key=...` | Objetos cercanos a la Tierra, rango máximo de siete días |
| NASA Images | `GET https://images-api.nasa.gov/search?q=asteroid%20{nombre}&media_type=image` | Búsqueda de fotografías con coincidencia explícita en el título |
| NASA DONKI | `GET https://api.nasa.gov/DONKI/notifications?startDate=...&endDate=...&type=all&api_key=...` | Notificaciones de clima espacial de los últimos siete días |
| NASA EONET v3 | `GET https://eonet.gsfc.nasa.gov/api/v3/events?status=open&limit=5` (Inicio) o `limit=100` (mapa) | Eventos naturales activos; no requiere clave NASA |
| NASA GIBS | `GET https://gibs.earthdata.nasa.gov/wms/epsg4326/best/wms.cgi?...` | Mosaico MODIS True Color para la ubicación y fecha de cada evento EONET |
| Backend local | `POST /api/assistant/explain` | Explicación contextual mediante OpenRouter |
| Estado del backend | `GET /health` | Comprobación de disponibilidad |

APOD no envía una clave en este endpoint WordPress. NeoWs y DONKI usan `NASA_API_KEY`; crea una clave propia en el sitio de NASA para evitar las restricciones de `DEMO_KEY`. DONKI y EONET se cargan de forma independiente, por lo que un límite de cuota o error de una fuente no oculta los datos de la otra. Si NASA responde HTTP 429, la pantalla muestra el error y permite reintentar. EONET no requiere clave.

## Instalación en Ubuntu / Android Studio

1. Instala Android Studio y el SDK correspondiente a `compileSdk` del módulo, y asegúrate de tener disponible el JDK requerido por el wrapper Gradle.
2. Abre esta carpeta en Android Studio y espera la sincronización de Gradle.
3. Copia `local.properties.example` a `local.properties` (este último está ignorado por Git). Añade la ruta del SDK, por ejemplo `sdk.dir=/home/usuario/Android/Sdk`, y configura `NASA_API_KEY=tu_clave_nasa` con una clave propia obtenida en `api.nasa.gov`; no uses `DEMO_KEY` para uso continuado porque su cuota compartida se agota rápidamente. `AI_BACKEND_URL=http://10.0.2.2:8000/` permite al emulador Android comunicarse con un backend que se ejecuta en el host.
4. Después de guardar `local.properties`, vuelve a compilar e instalar la app para que la clave se incorpore a `BuildConfig`: ejecuta `./gradlew :app:assembleDebug` o sincroniza y ejecuta la configuración `app` desde Android Studio.
5. Para correr las pruebas JVM, ejecuta `./gradlew :app:testDebugUnitTest`.

`BuildConfig.NASA_API_KEY` es conveniente para configuración de desarrollo, pero **no protege secretos**: cualquier valor incluido en BuildConfig puede extraerse del APK. No reutilices esa técnica para credenciales privadas. El repositorio incluye un `local.properties.example` sin claves reales; nunca publiques `local.properties`.

### Backend FastAPI y OpenRouter en Ubuntu

1. Crea una clave API en [OpenRouter](https://openrouter.ai/keys) y guárdala exclusivamente en el entorno del servidor.
2. En la raíz del proyecto:

   ```bash
   python3 -m venv .venv
   source .venv/bin/activate
   pip install -r backend/requirements.txt
   cp backend/.env.example backend/.env
   ```

3. Sustituye el valor de ejemplo de `OPENROUTER_API_KEY` en `backend/.env`; el archivo está ignorado por Git. El modelo predeterminado (`openrouter/free`) selecciona automáticamente un modelo gratuito disponible y puede cambiarse con `OPENROUTER_MODEL`.
4. Desde la raíz, ejecuta:

   ```bash
   uvicorn main:app --app-dir backend --host 0.0.0.0 --port 8000
   ```

5. Comprueba `http://127.0.0.1:8000/health`. Para el emulador Android usa `AI_BACKEND_URL=http://10.0.2.2:8000/`. En un dispositivo físico, configura la IP local del host y asegúrate de que ambos estén en una red confiable.

El tráfico HTTP sin cifrar se habilita únicamente para la variante `debug` para facilitar la conexión al backend local. La variante `release` lo desactiva; publícala solo con un backend accesible mediante HTTPS. Protege el servidor con límites de uso y controles de acceso antes de exponerlo a Internet.

### Despliegue gratuito en Render

El repositorio incluye `render.yaml` para crear el backend como un Web Service Python gratuito. Para desplegarlo:

1. Asegúrate de que `render.yaml` y la carpeta `backend/` estén subidos a la rama de GitHub que vas a conectar. No subas `backend/.env`; la clave debe configurarse como variable secreta en Render.
2. En [Render](https://dashboard.render.com/), selecciona **New → Blueprint**, conecta el repositorio y aplica la configuración detectada en `render.yaml`.
3. En **Environment**, configura `OPENROUTER_API_KEY` con la clave creada en OpenRouter. Configura `OPENROUTER_MODEL` como `openrouter/free` para que OpenRouter seleccione automáticamente un modelo gratuito disponible.
4. Espera a que termine el despliegue. Prueba `https://<nombre-del-servicio>.onrender.com/health`; debe responder `{"status":"ok"}`.
5. En `local.properties`, configura `AI_BACKEND_URL=https://<nombre-del-servicio>.onrender.com/`, compila e instala nuevamente la aplicación. Mantén la URL HTTPS; la versión `release` no permite HTTP sin cifrar.

El nivel gratuito de Render suspende el servicio después de 15 minutos sin solicitudes. La primera llamada posterior puede tardar cerca de un minuto mientras el backend vuelve a iniciarse. El router gratuito de OpenRouter selecciona entre modelos gratuitos disponibles, sujetos a límites de solicitudes; la disponibilidad y cuotas pueden cambiar. Consulta sus [precios y límites](https://openrouter.ai/pricing). El endpoint de explicación es público; antes de compartir ampliamente la aplicación, agrega controles de abuso y límites de solicitudes para proteger la cuota.

## Casos de prueba

Las pruebas JVM en `app/src/test` verifican filtrado por nombre y clasificación, ordenamiento por mediciones, estadísticas con campos ausentes, aceptación/rechazo de respuestas APOD y que NeoWs no reciba rangos de fecha superiores al límite. Los DTO de DONKI y EONET se prueban para garantizar la lectura de las propiedades que se muestran en Inicio. Los flujos de error de interfaz incluyen acciones de reintento; Room y DataStore son persistencia local observable.

## Guion de demostración (5–10 minutos)

1. Presentar el propósito y arquitectura por capas (1 min).
2. Mostrar APOD, explicación HTML limpia, imagen o contenido no fotográfico y detalle (1–2 min).
3. Consultar NeoWs, cambiar fechas, buscar un nombre, filtrar objetos potencialmente peligrosos y ordenar por una magnitud (2–3 min).
4. Guardar y quitar APOD/asteroide de favoritos; cerrar y reabrir para comprobar persistencia (1 min).
5. Cambiar tema, reiniciar y demostrar que la preferencia se conserva (1 min).
6. Si se configuró el backend, formular una pregunta contextual y mostrar la nota de procedencia; aclarar que no se predicen impactos (1–2 min).

## Preguntas para la sustentación

- **¿Por qué se usa Repository Pattern?** Aísla las fuentes remotas y locales de la UI y permite probar reglas sin depender de Compose.
- **¿Cómo se evita duplicar favoritos?** Room declara `id` como clave primaria y usa inserción con reemplazo.
- **¿Cómo se protege la clave del proveedor de IA?** La credencial vive en el entorno del backend; el APK no la recibe ni la contiene.
- **¿Qué significan los datos de peligrosidad?** Se presenta literalmente la clasificación recibida de NASA; no es una predicción de impacto.
- **¿Cómo se limita NeoWs?** Se valida el rango localmente y se permite como máximo una ventana de siete días.
- **¿Qué hace la app si falta una medición?** Presenta “No disponible” o excluye el dato de cálculos, en lugar de inventar un valor astronómico.
