/** Orquesta las fuentes de datos y presenta resultados coherentes a la capa de interfaz. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.local.dao.AssistantHistoryDao
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import kotlinx.coroutines.flow.Flow

/** Centraliza el acceso a datos y la resolución de fuentes. */
class AssistantHistoryRepository(
    private val dao: AssistantHistoryDao
) {
    val history: Flow<List<AssistantHistoryEntity>> = dao.observeAll()

    /** Guarda la respuesta y elimina entradas antiguas si se supera el límite. */
    suspend fun save(question: String, answer: String) {
        dao.insert(
            AssistantHistoryEntity(
                question = question,
                answer = answer,
                createdAt = System.currentTimeMillis()
            )
        )
        dao.keepLatest50()
    }

    /** Elimina una conversación guardada. */
    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    /** Elimina todo el historial guardado. */
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
