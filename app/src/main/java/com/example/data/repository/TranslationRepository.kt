package com.example.data.repository

import com.example.data.db.TranslationDao
import com.example.data.db.TranslationEntity
import kotlinx.coroutines.flow.Flow

class TranslationRepository(private val dao: TranslationDao) {
    val allTranslations: Flow<List<TranslationEntity>> = dao.getAllTranslations()
    val favoriteTranslations: Flow<List<TranslationEntity>> = dao.getFavoriteTranslations()

    suspend fun insertTranslation(translation: TranslationEntity): Long {
        return dao.insertTranslation(translation)
    }

    suspend fun toggleFavorite(id: Int, isFav: Boolean) {
        dao.updateFavorite(id, isFav)
    }

    suspend fun deleteTranslation(id: Int) {
        dao.deleteTranslation(id)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}
