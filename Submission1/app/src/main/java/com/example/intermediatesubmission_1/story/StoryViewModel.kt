package com.example.intermediatesubmission_1.story

import android.util.Log
import androidx.lifecycle.*
import com.example.intermediatesubmission_1.api.ApiConfig
import com.example.intermediatesubmission_1.model.UserModel
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.response.ListStoryItem
import com.example.intermediatesubmission_1.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val pref: UserPreference): ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun listStories(token: String){
        _isLoading.value = true
        val bearer = "Bearer $token"

        val client = ApiConfig.getApiService().getStory(bearer)
        client.enqueue(object: Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory as List<ListStoryItem>
                } else {
                    Log.e(TAG,"response not successfully: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}