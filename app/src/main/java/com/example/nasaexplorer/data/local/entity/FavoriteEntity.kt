package com.example.nasaexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
