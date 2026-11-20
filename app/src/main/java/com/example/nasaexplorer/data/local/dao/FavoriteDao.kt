/** Define las operaciones de acceso a los datos locales de esta entidad. */
package com.example.nasaexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
/** Define el contrato de favorite dao. */
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY date DESC")
    /** Efectúa la consulta o modificación local asociada a get all favorites. */
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE id = :favoriteId")
    suspend fun deleteFavorite(favoriteId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :favoriteId)")
    /** Efectúa la consulta o modificación local asociada a is favorite. */
    fun isFavorite(favoriteId: String): Flow<Boolean>

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
