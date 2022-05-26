package com.example.intermediatesubmission_1.api

import com.example.intermediatesubmission_1.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
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
    suspend fun getStory(
        @Header("Authorization") token : String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): Response<StoryResponse>

    @GET("stories")
    fun getStoryMaps(
        @Header("Authorization") token : String,
        @Query("location") location: Int? = null
    ): Call<StoryResponse>


}