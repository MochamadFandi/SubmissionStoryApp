package com.fanindo.submissionstoryapp.di

import android.content.Context
import com.fanindo.submissionstoryapp.data.StoryRepository
import com.fanindo.submissionstoryapp.data.local.room.StoryDatabase
import com.fanindo.submissionstoryapp.data.remote.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}