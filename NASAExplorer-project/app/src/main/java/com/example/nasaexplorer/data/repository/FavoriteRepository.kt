/** Orquesta las fuentes de datos y presenta resultados coherentes a la capa de interfaz. */
package com.example.nasaexplorer.data.repository

import com.example.nasaexplorer.data.local.dao.FavoriteDao
import com.example.nasaexplorer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/** Centraliza el acceso a datos y la resolución de fuentes. */
class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    /** Proporciona la operación de datos requerida por la interfaz. */
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    /** Proporciona la operación de datos requerida por la interfaz. */
    fun isFavorite(id: String): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    /** Proporciona la operación de datos requerida por la interfaz. */
    suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    /** Proporciona la operación de datos requerida por la interfaz. */
    suspend fun deleteFavorite(id: String) {
        favoriteDao.deleteFavorite(id)
    }

    /** Proporciona la operación de datos requerida por la interfaz. */
    suspend fun deleteAllFavorites() {
        favoriteDao.deleteAllFavorites()
    }
}
