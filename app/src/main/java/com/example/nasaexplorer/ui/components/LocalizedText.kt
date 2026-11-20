/** Ofrece componentes y utilidades visuales reutilizables. */
package com.example.nasaexplorer.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

private val spanishToEnglish = mapOf(
    "Asistente" to "Assistant",
    "IMAGEN DEL DÍA" to "IMAGE OF THE DAY",
    "ASTEROIDES CERCANOS" to "NEAR-EARTH ASTEROIDS",
    "EXPLORACIÓN ESPACIAL" to "SPACE EXPLORATION",
    "Inicio" to "Home",
    "Asteroides" to "Asteroids",
    "DATOS NASA" to "NASA DATA",
    "Logotipo de NASA Explorer" to "NASA Explorer logo",
    "Ver información de la aplicación" to "View app information",
    "EXPLORACIÓN ASTRONÓMICA" to "ASTRONOMICAL EXPLORATION",
    "OBSERVAR · COMPRENDER · EXPLORAR" to "OBSERVE · UNDERSTAND · EXPLORE",
    "El espacio, explicado con datos." to "Space, explained through data.",
    "NASA · ACTIVIDAD ESPACIAL" to "NASA · SPACE ACTIVITY",
    "Actividad del Sol y la Tierra" to "Sun and Earth activity",
    "El universo, más cerca." to "The universe, closer.",
    "Explora el cosmos con datos de NASA." to "Explore the cosmos with NASA data.",
    "Descubre imágenes que cuentan historias." to "Discover images that tell stories.",
    "Observa la Tierra desde otra perspectiva." to "See Earth from a different perspective.",
    "Cada dato abre una ventana al espacio." to "Every fact opens a window to space.",
    "NASA · EXPLORACIÓN" to "NASA · EXPLORATION",
    "La selección diaria de NASA." to "NASA's daily selection.",
    "Seguimiento de objetos cercanos." to "Tracking near-Earth objects.",
    "Vista satelital del área del evento" to "Satellite view of the event area",
    "INFORMACIÓN DEL EVENTO" to "EVENT INFORMATION",
    "EVENTO NATURAL" to "NATURAL EVENT",
    "Imagen satelital no disponible" to "Satellite image unavailable",
    "Vista satelital de la zona asociada al evento. La imagen muestra el área y no necesariamente el fenómeno de cerca." to
        "Satellite view of the area associated with the event. The image shows the area and may not depict the phenomenon up close.",
    "Categoría" to "Category",
    "Fecha" to "Date",
    "Magnitud" to "Magnitude",
    "Identificador EONET" to "EONET identifier",
    "Ver informe completo" to "Read full report",
    "Mostrar resumen" to "Show summary",
    "Traducir al español" to "Translate to Spanish",
    "Ver original" to "View original",
    "La primera traducción descarga un modelo de idioma (aprox. 30 MB)." to
        "The first translation downloads a language model (about 30 MB).",
    "No se pudo completar la traducción." to "Translation couldn't be completed.",
    "Ver más" to "See more",
    "Ver menos" to "See less",
    "NASA DONKI · CLIMA ESPACIAL" to "NASA DONKI · SPACE WEATHER",
    "Cargando notificaciones de clima espacial…" to "Loading space weather notifications…",
    "NASA no publicó notificaciones recientes de clima espacial." to
        "NASA has not published recent space weather notifications.",
    "Notificación sin descripción." to "Notification has no description.",
    "Ver notificación NASA" to "View NASA notification",
    "NASA EONET · EVENTOS ACTIVOS" to "NASA EONET · ACTIVE EVENTS",
    "Cargando eventos naturales activos…" to "Loading active natural events…",
    "EONET no tiene eventos activos para mostrar." to "EONET has no active events to show.",
    "Mapa" to "Map",
    "Mapa de eventos EONET" to "EONET events map",
    "Eventos naturales activos" to "Active natural events",
    "Tipo de evento" to "Event type",
    "Descripción del evento" to "Event description",
    "Sobre este tipo de evento" to "About this event type",
    "Eventos de origen humano" to "Human-induced events",
    "Temperaturas extremas" to "Temperature extremes",
    "Cambios en el color del agua" to "Changes in water color",
    "Ausencia prolongada de precipitaciones que afecta la agricultura, la ganadería y la disponibilidad de alimentos y agua." to
        "Long-lasting absence of precipitation affecting agriculture, livestock, and the availability of food and water.",
    "Eventos relacionados con tormentas de polvo, contaminación del aire y otros aerosoles no volcánicos." to
        "Events related to dust storms, air pollution, and other non-volcanic aerosols.",
    "Sacudidas y desplazamientos del terreno; algunas consecuencias también pueden aparecer como inundaciones o deslizamientos." to
        "Ground shaking and displacement; some effects may also appear as floods or landslides.",
    "Inundaciones y extensión del agua más allá de los límites habituales de ríos y lagos." to
        "Flooding and water extending beyond the usual boundaries of rivers and lakes.",
    "Deslizamientos de tierra y fenómenos relacionados, como flujos de lodo y avalanchas." to
        "Landslides and related events such as mudslides and avalanches.",
    "Eventos extremos originados por actividades humanas." to "Extreme events caused by human activity.",
    "Hielo en océanos y lagos, incluido el hielo estacional o permanente y los icebergs." to
        "Ice on oceans and lakes, including seasonal or permanent ice and icebergs.",
    "Aspectos atmosféricos de tormentas, como huracanes, ciclones y tornados; sus efectos también pueden aparecer en otras categorías." to
        "Atmospheric aspects of storms, such as hurricanes, cyclones, and tornadoes; their effects may also appear in other categories.",
    "Nevadas extremas o anómalas por su momento, extensión o profundidad." to
        "Extreme or anomalous snowfall in timing, extent, or depth.",
    "Temperaturas terrestres anómalas, tanto calor como frío." to
        "Anomalous land temperatures, including heat and cold.",
    "Efectos físicos de erupciones, como roca, ceniza y lava, y columnas atmosféricas de ceniza o gases." to
        "Physical effects of eruptions, such as rock, ash, and lava, and atmospheric ash or gas plumes.",
    "Eventos que alteran el aspecto del agua, como fitoplancton, mareas rojas, algas y sedimentos." to
        "Events that alter the appearance of water, such as phytoplankton, red tides, algae, and sediments.",
    "Incendios en zonas naturales, bosques o llanuras; pueden tener origen natural o humano." to
        "Fires in natural areas, forests, or plains; they may be caused by natural or human activity.",
    "NASA EONET no publicó una descripción para este evento." to
        "NASA EONET did not provide a description for this event.",
    "La explicación de la categoría es general y no describe detalles específicos de este evento." to
        "This is a general category definition, not a description of this specific event.",
    "Ubicaciones aproximadas según geometrías publicadas por NASA EONET. Mapa: OpenStreetMap." to
        "Approximate locations based on geometries published by NASA EONET. Map: OpenStreetMap.",
    "Cargando eventos naturales…" to "Loading natural events…",
    "No hay eventos con coordenadas para mostrar." to "There are no events with coordinates to display.",
    "No se pudo cargar el mapa." to "Couldn't load the map.",
    "Ubicaciones aproximadas según geometrías publicadas por NASA EONET. Mapa: OpenStreetMap." to
        "Approximate locations based on geometries published by NASA EONET. Map: OpenStreetMap.",
    "Volver" to "Back",
    "Cerrar detalle" to "Close details",
    "Datos publicados por NASA DONKI y NASA EONET; las fuentes pueden actualizarse." to
        "Data published by NASA DONKI and NASA EONET; source data may be updated.",
    "Eyección de masa coronal" to "Coronal mass ejection",
    "Llamarada solar" to "Solar flare",
    "Tormenta geomagnética" to "Geomagnetic storm",
    "Choque interplanetario" to "Interplanetary shock",
    "Partículas energéticas solares" to "Solar energetic particles",
    "Evento de radiación" to "Radiation belt enhancement",
    "Corriente solar de alta velocidad" to "High-speed solar wind stream",
    "Conjunción planetaria" to "Planetary conjunction",
    "Notificación de clima espacial" to "Space weather notification",
    "Incendios forestales" to "Wildfires",
    "Tormentas severas" to "Severe storms",
    "Volcanes" to "Volcanoes",
    "Hielo marino y lacustre" to "Sea and lake ice",
    "Inundaciones" to "Floods",
    "Terremotos" to "Earthquakes",
    "Deslizamientos de tierra" to "Landslides",
    "Sequía" to "Drought",
    "Polvo y neblina" to "Dust and haze",
    "Nieve" to "Snow",
    "Evento natural" to "Natural event",
    "Reintentar" to "Retry",
    "Consulta imágenes astronómicas y objetos cercanos a la Tierra a partir de información publicada por NASA." to
        "Explore astronomical images and near-Earth objects using information published by NASA.",
    "Explorar" to "Explore",
    "Selecciona un módulo para consultar la información." to "Select a module to explore the data.",
    "APOD · ASTRONOMY PICTURE OF THE DAY" to "APOD · ASTRONOMY PICTURE OF THE DAY",
    "Imagen astronómica del día" to "Astronomy Picture of the Day",
    "Imagen, explicación y créditos publicados por NASA." to "Image, explanation, and credits published by NASA.",
    "NEOWS · OBJETOS CERCANOS A LA TIERRA" to "NEOWS · NEAR-EARTH OBJECTS",
    "Asteroides cercanos" to "Near-Earth asteroids",
    "Aproximaciones, dimensiones y clasificación." to "Close approaches, dimensions, and classification.",
    "Tu colección" to "Your collection",
    "Contenido que guardaste para volver a consultar." to "Content you saved to revisit.",
    "ELEMENTOS GUARDADOS" to "SAVED ITEMS",
    "Favoritos" to "Favorites",
    "Tus imágenes y objetos astronómicos guardados." to "Your saved images and astronomical objects.",
    "Proyecto independiente. Los datos astronómicos pertenecen a sus respectivas fuentes." to
        "Independent project. Astronomical data belongs to its respective sources.",
    "EL UNIVERSO DE HOY" to "TODAY'S UNIVERSE",
    "Astronomy Picture of the Day" to "Astronomy Picture of the Day",
    "Contenido multimedia" to "Multimedia content",
    "Tipo no especificado" to "Type not specified",
    "Fecha no disponible" to "Date unavailable",
    "Sin título" to "Untitled",
    "No hay explicación disponible." to "No explanation available.",
    "Quitar de favoritos" to "Remove from favorites",
    "Agregar a favoritos" to "Add to favorites",
    "La historia detrás de la imagen" to "The story behind the image",
    "CRÉDITOS" to "CREDITS",
    "Créditos" to "Credits",
    "DETALLE ASTRONÓMICO" to "ASTRONOMY DETAILS",
    "Volver a APOD" to "Back to APOD",
    "Astronomía del día" to "Astronomy of the day",
    "tipo no especificado" to "type not specified",
    "Abrir contenido multimedia" to "Open multimedia content",
    "Contenido en video" to "Video content",
    "Conectando con NASA…" to "Connecting to NASA…",
    "Preparando el descubrimiento de hoy" to "Preparing today's discovery",
    "No pudimos cargar APOD" to "Couldn't load APOD",
    "Intentar nuevamente" to "Try again",
    "Explorar historia" to "Explore story",
    "Guardado" to "Saved",
    "Guardar" to "Save",
    "Imagen astronómica seleccionada por NASA para esta fecha." to
        "Astronomical image selected by NASA for this date.",
    "Consultando objetos cercanos con NASA…" to "Loading near-Earth objects from NASA…",
    "Periodo de consulta" to "Query period",
    "DESDE" to "FROM",
    "HASTA" to "TO",
    "NeoWs admite periodos de hasta 7 días por consulta." to "NeoWs supports query periods of up to 7 days.",
    "Consultar periodo" to "Load selected dates",
    "OBJETOS" to "OBJECTS",
    "PELIGROSOS" to "HAZARDOUS",
    "MÁX. DIÁMETRO" to "MAX DIAMETER",
    "Buscar por nombre" to "Search by name",
    "Ej. Apophis" to "e.g. Apophis",
    "Potencialmente peligrosos" to "Potentially hazardous",
    "Nombre" to "Name",
    "Diámetro" to "Diameter",
    "Velocidad" to "Speed",
    "Distancia" to "Distance",
    "Mayor diámetro" to "Largest diameter",
    "Mayor velocidad" to "Highest speed",
    "Menor distancia" to "Closest distance",
    "No hay asteroides para estos filtros." to "No asteroids match these filters.",
    "Prueba con otro nombre o desactiva el filtro." to "Try another name or turn off the filter.",
    "NASA · NEOWS" to "NASA · NEOWS",
    "Objetos cercanos a la Tierra" to "Near-Earth objects",
    "Aproximaciones y datos orbitales publicados por NASA." to
        "Close approaches and orbital data published by NASA.",
    "Asteroide sin nombre" to "Unnamed asteroid",
    "DIÁMETRO" to "DIAMETER",
    "VELOCIDAD" to "SPEED",
    "DISTANCIA DE APROXIMACIÓN" to "CLOSE APPROACH DISTANCE",
    "FECHA NO DISPONIBLE" to "DATE UNAVAILABLE",
    "No potencialmente peligroso" to "Not potentially hazardous",
    "Clasificación no disponible" to "Classification unavailable",
    "Seleccionar" to "Select",
    "Cancelar" to "Cancel",
    "TU UNIVERSO PERSONAL" to "YOUR PERSONAL UNIVERSE",
    "Tu biblioteca astronómica" to "Your astronomy library",
    "ELEMENTOS" to "ITEMS",
    "IMÁGENES" to "IMAGES",
    "ASTEROIDES" to "ASTEROIDS",
    "GUARDADOS PARA EXPLORAR" to "SAVED FOR EXPLORATION",
    "DETALLE GUARDADO" to "SAVED ITEM DETAILS",
    "Volver a favoritos" to "Back to favorites",
    "No hay información adicional guardada." to "No additional information was saved.",
    "Datos del objeto" to "Object information",
    "Eliminar todos" to "Remove all",
    "Tu colección empieza aquí" to "Your collection starts here",
    "Guarda imágenes astronómicas y objetos cercanos para encontrarlos fácilmente." to
        "Save astronomical images and near-Earth objects to find them easily.",
    "descubrimiento guardado" to "discovery saved",
    "descubrimientos guardados" to "discoveries saved",
    "ASTEROIDE CERCANO" to "NEAR-EARTH ASTEROID",
    "GUARDADO" to "SAVED",
    "APARIENCIA" to "APPEARANCE",
    "IDIOMA" to "LANGUAGE",
    "Idioma de la aplicación" to "App language",
    "Elige el idioma de todos los menús y controles." to "Choose the language for all menus and controls.",
    "Claro" to "Light",
    "Superficies luminosas durante el día" to "Bright surfaces for daytime use",
    "Oscuro" to "Dark",
    "Una experiencia cómoda en la noche" to "Comfortable viewing at night",
    "Seguir el sistema" to "Follow system",
    "Usa la preferencia del dispositivo" to "Use your device preference",
    "Acerca de NASA Explorer" to "About NASA Explorer",
    "Una herramienta para explorar" to "A tool for exploration",
    "Explora imágenes astronómicas, asteroides cercanos y eventos de nuestro planeta con datos publicados por NASA." to
        "Explore astronomical images, near-Earth asteroids, and events on our planet using data published by NASA.",
    "Fuentes de información" to "Information sources",
    "Imágenes y explicaciones astronómicas" to "Astronomical images and explanations",
    "Información de objetos cercanos a la Tierra" to "Near-Earth object information",
    "Eventos naturales y actividad espacial" to "Natural events and space activity",
    "Imágenes satelitales de la Tierra" to "Satellite imagery of Earth",
    "Observa · comprende · explora" to "Observe · understand · explore",
    "Versión " to "Version ",
    " · Créditos y fuentes" to " · Credits and sources",
    "Créditos y fuentes" to "Credits and sources",
    "NASA Explorer" to "NASA Explorer",
    "VERSIÓN" to "VERSION",
    "PROPÓSITO" to "PURPOSE",
    "Explorar y comprender información astronómica pública con sus unidades y contexto." to
        "Explore and understand public astronomical information with its units and context.",
    "NASA proporciona los datos astronómicos. Esta aplicación es un proyecto independiente y no representa una afiliación oficial con NASA." to
        "NASA provides the astronomical data. This is an independent project and is not officially affiliated with NASA.",
    "Cerrar" to "Close",
    "Identificador NASA" to "NASA ID",
    "DETALLE DEL OBJETO" to "OBJECT DETAILS",
    "NASA · NeoWs" to "NASA · NeoWs",
    "Volver a asteroides" to "Back to asteroids",
    "NASA · Potencialmente peligroso" to "NASA · Potentially hazardous",
    "NASA · No potencialmente peligroso" to "NASA · Not potentially hazardous",
    "NASA · No disponible" to "NASA · Unavailable",
    "Datos estimados" to "Estimated data",
    "Aproximación registrada" to "Recorded close approach",
    "Ilustración conceptual · no es una fotografía" to "Conceptual illustration · not a photograph",
    "Ilustración conceptual de " to "Conceptual illustration of ",
    "Fotografía real no disponible" to "Real photograph unavailable",
    "NASA no tiene una imagen identificada para este asteroide." to
        "NASA has no identified image for this asteroid.",
    "No se pudo consultar la biblioteca de imágenes de NASA." to
        "Couldn't search NASA's image library.",
    "Solo se muestran fotografías reales verificadas; no se usan ilustraciones." to
        "Only verified real photographs are shown; illustrations are not used.",
    "Solo se muestra una fotografía identificada en la biblioteca de NASA. Si no hay una coincidencia fiable, no se usa una imagen ilustrativa." to
        "Only a photograph identified in NASA's library is shown. No illustration is used when there is no reliable match.",
    "Reintentar" to "Retry",
    "Fotografía de NASA: " to "NASA photograph: ",
    "Las medidas y la clasificación se basan en los datos publicados por NASA. La ilustración no representa la forma real del objeto." to
        "Measurements and classification are based on data published by NASA. This illustration does not represent the object's actual shape.",
    "Diámetro mínimo" to "Minimum diameter",
    "Diámetro máximo" to "Maximum diameter",
    "Velocidad relativa" to "Relative velocity",
    "Distancia de aproximación" to "Close approach distance",
    "Fecha de aproximación" to "Close approach date",
    "Potencialmente peligroso" to "Potentially hazardous",
    "Sí, según NASA" to "Yes, according to NASA",
    "No, según NASA" to "No, according to NASA",
    "Asistente NASA" to "NASA Assistant",
    "Abrir asistente NASA" to "Open NASA assistant",
    "Explicaciones basadas en datos" to "Explanations based on data",
    "Explicaciones con contexto NASA" to "Explanations grounded in NASA context",
    "PREGUNTAS SUGERIDAS" to "SUGGESTED QUESTIONS",
    "TU PREGUNTA" to "YOUR QUESTION",
    "RESPUESTA" to "ANSWER",
    "Historial de preguntas" to "Question history",
    "Volver al historial" to "Back to history",
    "Volver al chat" to "Back to chat",
    "Borrar todo" to "Delete all",
    "Cancelar" to "Cancel",
    "¿Borrar todo el historial?" to "Delete all history?",
    "Se eliminarán todas las preguntas y respuestas guardadas en este dispositivo." to
        "All questions and answers saved on this device will be deleted.",
    "Eliminar pregunta del historial" to "Delete question from history",
    "No se pudo eliminar esta pregunta del historial." to
        "Couldn't delete this question from history.",
    "No se pudo borrar el historial." to "Couldn't delete the history.",
    "Tus preguntas respondidas aparecerán aquí." to "Your answered questions will appear here.",
    "La respuesta llegó, pero no se pudo guardar en el historial." to
        "The answer arrived, but it couldn't be saved to history.",
    "Asistente Inteligente NASA" to "NASA AI Assistant",
    "Haz preguntas contextuales sobre datos astronómicos, la foto del día o asteroides." to
        "Ask contextual questions about astronomical data, the picture of the day, or asteroids.",
    "¿Qué es APOD y cómo se selecciona la imagen?" to "What is APOD and how is the image selected?",
    "¿Cómo clasifica la NASA un asteroide potencialmente peligroso?" to
        "How does NASA classify a potentially hazardous asteroid?",
    "Explicar APOD" to "Explain APOD",
    "Asteroides peligrosos" to "Hazardous asteroids",
    "Pregunta al asistente" to "Ask the assistant",
    "Ej: Explicar qué es un asteroide..." to "e.g. Explain what an asteroid is...",
    "Consultando con la Inteligencia Artificial..." to "Asking the AI assistant…",
    "Respuesta del Asistente" to "Assistant response",
    "Escribe una duda o selecciona uno de los temas sugeridos arriba." to
        "Type a question or choose one of the suggested topics above.",
    "Explicaciones para explorar el cosmos" to "Explanations to explore the cosmos",
    "¿Qué te gustaría descubrir?" to "What would you like to discover?",
    "Puedo ayudarte a entender la información de NASA que estás viendo." to
        "I can help you understand the NASA information you're viewing.",
    "Pregunta sobre astronomía o conceptos espaciales. Si no hay datos de un objeto seleccionado, te lo indicaré." to
        "Ask about astronomy or space concepts. If no selected-object data is available, I'll let you know.",
    "PRUEBA PREGUNTANDO" to "TRY ASKING",
    "Explícame esta imagen" to "Explain this image",
    "¿Qué términos astronómicos aparecen?" to "What astronomical terms appear here?",
    "Explícame estos datos" to "Explain these measurements",
    "¿Qué significa potencialmente peligroso?" to "What does potentially hazardous mean?",
    "¿Qué es un año luz?" to "What is a light-year?",
    "¿Cómo se estudian los asteroides?" to "How are asteroids studied?",
    "Preparando una explicación…" to "Preparing an explanation…",
    "Revisando el contexto disponible" to "Reviewing the available context",
    "EXPLICACIÓN" to "EXPLANATION",
    "Cerrar asistente" to "Close assistant",
    "Escribe tu pregunta…" to "Type your question…",
    "Enviar pregunta" to "Send question",
    "Enviar" to "Send",
    "Respuestas orientativas basadas en el contexto. No predice impactos." to
        "Explanatory answers based on context. No impact predictions.",
    "Ajustes" to "Settings",
    "Personaliza tu experiencia de exploración." to "Customize your exploration experience.",
    "Acerca de" to "About",
    "Propósito" to "Purpose",
    "FUENTES DE DATOS" to "DATA SOURCES",
    "Escribe una pregunta para el asistente." to "Enter a question for the assistant.",
    "No hay contexto de NASA para explicar." to "There is no NASA context to explain.",
    "No se pudo conectar con el backend del asistente." to "Couldn't connect to the assistant backend.",
    "Error al consultar el asistente" to "Error while contacting the assistant.",
    "El backend no tiene GEMINI_API_KEY configurada." to "The backend has no GEMINI_API_KEY configured.",
    "Gemini tardó demasiado en responder." to "Gemini took too long to respond.",
    "No fue posible conectar con Gemini." to "Couldn't connect to Gemini.",
    "Gemini no pudo procesar la consulta." to "Gemini couldn't process the request.",
    "Gemini devolvió una respuesta inválida." to "Gemini returned an invalid response.",
    "Gemini devolvió una respuesta vacía." to "Gemini returned an empty response.",
    "No pudimos cargar los asteroides" to "Couldn't load asteroids",
    "Límite de solicitudes alcanzado" to "Request limit reached",
    "Servicio NASA no disponible" to "NASA service unavailable",
    "NASA no aceptó la clave API" to "NASA rejected the API key",
    "NASA respondió con un error:" to "NASA returned an error:",
    "No se pudieron cargar los asteroides." to "Couldn't load the asteroids.",
    "La API key de NASA no puede estar vacía." to "The NASA API key cannot be empty.",
    "Error de conexión. Verifica tu internet." to "Connection error. Check your internet access.",
    "La fecha inicial no es válida." to "The start date is invalid.",
    "La fecha final no es válida." to "The end date is invalid.",
    "Las fechas deben tener el formato AAAA-MM-DD." to "Dates must use the YYYY-MM-DD format.",
    "La fecha final debe ser igual o posterior a la fecha inicial." to
        "The end date must be the same as or later than the start date.",
    "NeoWs permite consultar un máximo de 7 días por solicitud." to
        "NeoWs allows a maximum of 7 days per request.",
    "La fecha final no puede estar en el futuro." to "The end date cannot be in the future.",
    "NASA no devolvió información de APOD." to "NASA returned no APOD information.",
    "La respuesta de NASA no contiene los datos básicos de APOD." to
        "NASA's response is missing basic APOD data.",
    "No se pudo conectar con NASA. Revisa tu conexión." to "Couldn't connect to NASA. Check your connection."
)

internal fun translateToEnglish(text: String): String {
    spanishToEnglish[text]?.let { return it }

    var translated = text
    val substitutions = listOf(
        "No disponible" to "Unavailable",
        "metros" to "meters",
        "NASA ID " to "NASA ID ",
        "IDENTIFICADOR NASA · " to "NASA ID · ",
        "Abrir " to "Open ",
        "Magnitud: " to "Magnitude: ",
        " eventos · toca un marcador para ver los detalles" to " events · tap a marker to see details",
        "Imagen: " to "Image: ",
        "Fecha: " to "Date: ",
        "Diámetro mínimo:" to "Minimum diameter:",
        "Diámetro máximo:" to "Maximum diameter:",
        "Velocidad:" to "Speed:",
        "Distancia:" to "Distance:",
        "Clasificación NASA:" to "NASA classification:",
        "NASA · Potencialmente peligroso" to "NASA · Potentially hazardous",
        "NASA · No potencialmente peligroso" to "NASA · Not potentially hazardous",
        "NASA · Clasificación no disponible" to "NASA · Classification unavailable",
        "DISTANCIA DE APROXIMACIÓN · " to "CLOSE APPROACH DISTANCE · ",
        "Versión " to "Version ",
        " · Créditos y fuentes" to " · Credits and sources",
        " y Near Earth Object Web Service" to " and Near-Earth Object Web Service",
        "NASA respondió con el código HTTP " to "NASA returned HTTP code ",
        "NASA respondió con un error: " to "NASA returned an error: ",
        "NASA limitó temporalmente las consultas." to "NASA has temporarily limited requests.",
        "NASA limitó temporalmente las solicitudes de DONKI. Configura una clave NASA propia si usas DEMO_KEY." to
            "NASA temporarily limited DONKI requests. Configure your own NASA key if using DEMO_KEY.",
        "No hay conexión con DONKI." to "Couldn't connect to DONKI.",
        "No hay conexión con EONET." to "Couldn't connect to EONET.",
        "La clave NASA_API_KEY no está configurada." to "NASA_API_KEY is not configured.",
        "Revisa NASA_API_KEY en local.properties." to "Check NASA_API_KEY in local.properties.",
        "DONKI respondió con HTTP " to "DONKI returned HTTP ",
        "EONET respondió con HTTP " to "EONET returned HTTP ",
        "DONKI devolvió una respuesta inválida." to "DONKI returned an invalid response.",
        "EONET devolvió una respuesta inválida." to "EONET returned an invalid response.",
        "Espera un momento y vuelve a intentar." to "Wait a moment and try again.",
        "DEMO_KEY tiene una cuota reducida; configura tu propia clave NASA en local.properties." to
            "DEMO_KEY has a limited quota; configure your own NASA key in local.properties.",
        "Revisa que NASA_API_KEY en local.properties sea válida y vuelve a compilar la app." to
            "Check that NASA_API_KEY in local.properties is valid, then rebuild the app.",
        "Comprueba que las fechas sean válidas y que el periodo no supere los 7 días." to
            "Check that the dates are valid and the period does not exceed 7 days.",
        "Intenta nuevamente más tarde." to "Try again later."
    )
    substitutions.forEach { (source, target) ->
        translated = translated.replace(source, target)
    }

    if (translated.matches(Regex("\\s*\\d+ objetos encontrados\\s*"))) {
        translated = translated.replace("objetos encontrados", "objects found")
    }
    val savedCount = Regex("\\s*(\\d+) descubrimientos? guardados?\\s*").matchEntire(translated)
    if (savedCount != null) {
        val count = savedCount.groupValues[1]
        translated = "$count ${if (count == "1") "discovery saved" else "discoveries saved"}"
    }
    return translated
}

@Composable
/** Dibuja en Compose la interfaz asociada a localized string */
fun localizedString(text: String): String {
    val language = LocalConfiguration.current.locales[0]?.language
    return if (language == "en") translateToEnglish(text) else text
}

@Composable
/** Dibuja en Compose la interfaz asociada a text */
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    MaterialText(
        text = localizedString(text),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}
