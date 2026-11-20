/** Define las operaciones de acceso a los datos locales de esta entidad. */
package com.example.nasaexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nasaexplorer.data.local.entity.AssistantHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
/** Define el contrato de assistant history dao. */
interface AssistantHistoryDao {

    @Insert
    suspend fun insert(entry: AssistantHistoryEntity)

    @Query("SELECT * FROM assistant_history ORDER BY createdAt DESC, id DESC")
    /** Efectúa la consulta o modificación local asociada a observe all. */
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
    suspend fun keepLatest50()

    /** Elimina una conversación del historial. */
    @Query("DELETE FROM assistant_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Elimina todas las conversaciones guardadas. */
    @Query("DELETE FROM assistant_history")
    suspend fun deleteAll()
}
