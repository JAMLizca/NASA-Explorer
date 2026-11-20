/** Orquesta las fuentes de datos y ofrece una interfaz coherente para la presentación. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.local.dao.FavoriteDao
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/** Centraliza el acceso y manejo de datos para favorite. */
class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    /** Proporciona la operación de datos asociada a is favorite. */
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
