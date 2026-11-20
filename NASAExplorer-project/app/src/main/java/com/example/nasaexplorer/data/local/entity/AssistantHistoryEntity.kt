/** Representa un registro persistido en la base de datos Room. */
package com.example.nasaexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Representa un registro almacenado en la base de datos local. */
@Entity(tableName = "assistant_history")
data class AssistantHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val answer: String,
    val createdAt: Long
)
