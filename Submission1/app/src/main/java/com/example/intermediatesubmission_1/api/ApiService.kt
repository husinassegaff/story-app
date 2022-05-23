package com.example.intermediatesubmission_1.api

import com.example.intermediatesubmission_1.response.LoginResponse
import com.example.intermediatesubmission_1.response.RegisterResponse
import com.example.intermediatesubmission_1.response.StoryResponse
import com.example.intermediatesubmission_1.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun getUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token : String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @GET("stories")
    fun getStory(
        @Header("Authorization") token : String,
    ): Call<StoryResponse>


}