package com.fanindo.submissionstoryapp.data.remote.api


import com.fanindo.submissionstoryapp.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @POST("login")
    fun sendLogin(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun sendSignup(@Body request: SignupRequest): Call<SignupResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String, @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): StoryResponse

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<UploadResponse>
}