/** Representa la entidad persistida y sus campos en la base de datos local. */
package com.example.nasaexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
/** Representa un registro almacenado en la base de datos local. */
data class FavoriteEntity(

    @PrimaryKey
    val id: String,

    val title: String,

    val description: String,

    val imageUrl: String,

    val date: String,

    val type: String
)
