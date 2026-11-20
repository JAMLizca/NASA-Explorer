/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
package com.example.nasaexplorer

import com.example.nasaexplorer.ui.components.translateToEnglish
import org.junit.Assert.assertEquals
import org.junit.Test

/** Agrupa las pruebas relacionadas con esta funcionalidad. */
class LocalizedTextTest {
    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun translatesStaticInterfaceLabels() {
        assertEquals("Home", translateToEnglish("Inicio"))
        assertEquals("Near-Earth asteroids", translateToEnglish("Asteroides cercanos"))
        assertEquals("See more", translateToEnglish("Ver más"))
        assertEquals("See less", translateToEnglish("Ver menos"))
        assertEquals("Category", translateToEnglish("Categoría"))
        assertEquals("EONET identifier", translateToEnglish("Identificador EONET"))
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun translatesDynamicResultCounts() {
        assertEquals("8 objects found", translateToEnglish("8 objetos encontrados"))
        assertEquals("1 discovery saved", translateToEnglish("  1 descubrimiento guardado  "))
        assertEquals("3 discoveries saved", translateToEnglish("  3 descubrimientos guardados  "))
    }

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun translatesNasaRateLimitMessage() {
        val message = "NASA limitó temporalmente las consultas. " +
            "Espera un momento y vuelve a intentar. " +
            "DEMO_KEY tiene una cuota reducida; configura tu propia clave NASA en local.properties."

        assertEquals(
            "NASA has temporarily limited requests. " +
                "Wait a moment and try again. " +
                "DEMO_KEY has a limited quota; configure your own NASA key in local.properties.",
            translateToEnglish(message)
        )
    }
}
