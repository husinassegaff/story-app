package com.example.intermediatesubmission_1.login

import android.util.Log
import androidx.lifecycle.*
import com.example.intermediatesubmission_1.api.ApiConfig
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _loginCheck = MutableLiveData<Boolean>()
    val loginCheck: LiveData<Boolean> = _loginCheck

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    fun loginUser(email: String, password: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.body()?.error == true){
                    _loginCheck.value = false
                    Log.e(TAG, "Error Login User : ${response.body()!!.message}")
                }

                else if (response.isSuccessful) {
                    val dataLogin = response.body()
                    if (dataLogin != null && !dataLogin.error){
                        val name = dataLogin.loginResult.name
                        val userId = dataLogin.loginResult.userId
                        val token = dataLogin.loginResult.token

                        login(name, userId, token)
                        _loginCheck.value = true
                    } else {
                        Log.e(TAG, "Error Login User : ${dataLogin?.message}")
                    }
                }

                else if (response.code() == 401) _loginCheck.value = false




                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }
        })
    }

    fun login(name: String, userId: String, token: String) {
        viewModelScope.launch {
            pref.login(name, userId, token)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}