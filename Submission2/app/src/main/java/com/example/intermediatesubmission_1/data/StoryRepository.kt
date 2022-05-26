package com.example.intermediatesubmission_1.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.intermediatesubmission_1.api.ApiService
import com.example.intermediatesubmission_1.database.StoryDatabase
import com.example.intermediatesubmission_1.response.ListStoryItem


class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        val bearer = "Bearer $token"
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(bearer, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}