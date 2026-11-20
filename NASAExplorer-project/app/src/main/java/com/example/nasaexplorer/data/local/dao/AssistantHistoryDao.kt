/** Agrupa las operaciones de consulta y escritura sobre los datos almacenados localmente. */
package com.example.nasaexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import kotlinx.coroutines.flow.Flow

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
@Dao
interface AssistantHistoryDao {

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Insert
    suspend fun insert(entry: AssistantHistoryEntity)

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Query("SELECT * FROM assistant_history ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<AssistantHistoryEntity>>

    /** Conserva solo las 50 conversaciones más recientes. */
    @Query(
        """
        DELETE FROM assistant_history
        WHERE id NOT IN (
            SELECT id FROM assistant_history
            ORDER BY createdAt DESC, id DESC
            LIMIT 50
        )
        """
    )
    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    suspend fun keepLatest50()

    /** Elimina una conversación del historial. */
    suspend fun deleteById(id: Long)
    @Query("DELETE FROM assistant_history WHERE id = :id")

    /** Elimina todas las conversaciones guardadas. */
    suspend fun deleteAll()
    @Query("DELETE FROM assistant_history")
}
