/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
package com.example.nasaexplorer

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
/** Agrupa las pruebas relacionadas con esta funcionalidad. */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.nasaexplorer", appContext.packageName)
    }
}