/** Prueba el comportamiento de la funcionalidad correspondiente y verifica sus resultados esperados. */
package com.example.nasaexplorer

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nasaexplorer.data.local.AppDatabase
import com.example.nasaexplorer.data.repository.AssistantHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Agrupa las pruebas relacionadas con esta funcionalidad. */
@RunWith(AndroidJUnit4::class)
class AssistantHistoryRepositoryTest {

    /** Verifica el resultado esperado de este escenario. */
    @Test
    fun deletesOneEntryAndCanClearAllEntries() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        try {
            val repository = AssistantHistoryRepository(database.assistantHistoryDao())
            repository.save("Pregunta uno", "Respuesta uno")
            repository.save("Pregunta dos", "Respuesta dos")

            val savedEntries = repository.history.first()
            assertEquals(2, savedEntries.size)

            repository.delete(savedEntries.first().id)
            assertEquals(1, repository.history.first().size)

            repository.deleteAll()
            assertEquals(0, repository.history.first().size)
        } finally {
            database.close()
        }
    }
}
