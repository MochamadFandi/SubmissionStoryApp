package com.fanindo.submissionstoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.data.local.room.StoryDatabase
import com.fanindo.submissionstoryapp.data.remote.api.ApiService
import com.fanindo.submissionstoryapp.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getAllStories(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }

        ).liveData
    }

    fun getAllMarker(token: String): Flow<Result<StoryResponse>> = flow {
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.getAllStories(bearerToken, size = 5, page = 5)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

}