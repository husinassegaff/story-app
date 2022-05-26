package com.example.intermediatesubmission_1.story

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.intermediatesubmission_1.data.StoryRepository
import com.example.intermediatesubmission_1.di.Injection
import com.example.intermediatesubmission_1.model.UserModel
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.response.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository): ViewModel() {


    fun story(token: String): LiveData<PagingData<ListStoryItem>> =  storyRepository.getStory(token).cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}

class ViewModelFactory(private val pref: UserPreference, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(pref, Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

