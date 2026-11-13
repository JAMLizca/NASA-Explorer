package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.local.dao.FavoriteDao
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    fun isFavorite(id: String): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(id: String) {
        favoriteDao.deleteFavorite(id)
    }

    suspend fun deleteAllFavorites() {
        favoriteDao.deleteAllFavorites()
    }
}
