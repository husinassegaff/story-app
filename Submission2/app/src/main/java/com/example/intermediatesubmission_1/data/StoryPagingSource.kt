package com.example.intermediatesubmission_1.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.intermediatesubmission_1.api.ApiService
import com.example.intermediatesubmission_1.response.ListStoryItem

class StoryPagingSource(private val token: String, private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory("Bearer $token", page, params.loadSize)

            LoadResult.Page(
                data = responseData.body()?.listStory as MutableList<ListStoryItem>,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if ((responseData.body()!!.listStory as MutableList<ListStoryItem>).isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}