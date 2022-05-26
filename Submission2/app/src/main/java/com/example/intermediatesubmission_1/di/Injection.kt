package com.example.intermediatesubmission_1.di

import android.content.Context
import com.example.intermediatesubmission_1.api.ApiConfig
import com.example.intermediatesubmission_1.data.StoryRepository
import com.example.intermediatesubmission_1.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}