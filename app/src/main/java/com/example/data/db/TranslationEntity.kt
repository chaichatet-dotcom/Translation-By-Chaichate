package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceText: String,
    val targetText: String,
    val sourceLang: String,
    val targetLang: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val tone: String = "Natural", // Direct, Friendly, Professional, Casual, Romantic, Funny
    val mode: String = "AI Natural" // Direct vs AI Natural
)
