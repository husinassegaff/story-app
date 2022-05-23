package com.example.intermediatesubmission_1.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intermediatesubmission_1.api.ApiConfig
import com.example.intermediatesubmission_1.model.UserModel
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreference) : ViewModel() {

    private val _registerCheck = MutableLiveData<Boolean>()
    val registerCheck: LiveData<Boolean> = _registerCheck

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(name,email, password)
        client.enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful){
                    val dataRegister = response.body()
                    if (dataRegister != null){
                        if (!dataRegister.error){
                            _registerCheck.value = true
                        } else {
                            Log.e(TAG, "Error Create User ${dataRegister.message}")
                        }
                    }
                } else {
                    _registerCheck.value = false
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}