package com.example.intermediatesubmission_1.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.intermediatesubmission_1.api.ApiConfig
import com.example.intermediatesubmission_1.response.ListStoryItem
import com.example.intermediatesubmission_1.response.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory : LiveData<List<ListStoryItem>> = _listStory

    fun getStory(token: String) {
        val bearer = "Bearer $token"
        val client = ApiConfig.getApiService().getStoryMaps(bearer, 1)
        client.enqueue(object: Callback<StoryResponse> {

            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory as List<ListStoryItem>
                    Log.d(TAG, "response Success")
                } else {
                    Log.e(TAG, "response onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "response onFailure: ${t.message}")
            }
        })
    }

    companion object {
        const val TAG = "MapsViewModel"
    }
}