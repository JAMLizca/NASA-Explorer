package com.example.nasaexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY date DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE id = :favoriteId")
    suspend fun deleteFavorite(favoriteId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :favoriteId)")
    fun isFavorite(favoriteId: String): Flow<Boolean>

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
