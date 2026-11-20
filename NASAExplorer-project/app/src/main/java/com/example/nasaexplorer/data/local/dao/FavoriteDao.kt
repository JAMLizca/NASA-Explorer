/** Agrupa las operaciones de consulta y escritura sobre los datos almacenados localmente. */
package com.example.nasaexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/** Define el contrato que deben cumplir las implementaciones relacionadas. */
@Dao
interface FavoriteDao {

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Query("SELECT * FROM favorites ORDER BY date DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Query("DELETE FROM favorites WHERE id = :favoriteId")
    suspend fun deleteFavorite(favoriteId: String)

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :favoriteId)")
    fun isFavorite(favoriteId: String): Flow<Boolean>

    /** Ejecuta la consulta o modificación necesaria sobre Room. */
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
