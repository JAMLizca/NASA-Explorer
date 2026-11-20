/** Representa la entidad persistida y sus campos en la base de datos local. */
package com.example.nasaexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistant_history")
/** Representa un registro almacenado en la base de datos local. */
data class AssistantHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val answer: String,
    val createdAt: Long
)
