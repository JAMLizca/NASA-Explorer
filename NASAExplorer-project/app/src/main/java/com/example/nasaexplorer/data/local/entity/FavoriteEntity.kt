/** Representa un registro persistido en la base de datos Room. */
package com.example.nasaexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Representa un registro almacenado en la base de datos local. */
@Entity(tableName = "favorites")
data class FavoriteEntity(

    @PrimaryKey
    val id: String,

    val title: String,

    val description: String,

    val imageUrl: String,

    val date: String,

    val type: String
)
