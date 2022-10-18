package com.fanindo.submissionstoryapp.ui.newstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fanindo.submissionstoryapp.data.remote.api.ApiConfig
import com.fanindo.submissionstoryapp.data.remote.response.UploadResponse
import com.fanindo.submissionstoryapp.model.UserModel
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NewStoryViewModel(private val preference: UserPreference) : ViewModel() {

    private val _response = MutableLiveData<UploadResponse>()
    val response: LiveData<UploadResponse> = _response

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getUser(): LiveData<UserModel> {
        return preference.getUser().asLiveData()
    }

    fun uploadStory(token: String, file: File, desc: String) {
        _isLoading.value = true
        val compressedFile = reduceFileImage(file)
        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val call =
            ApiConfig.getApiService().uploadStory("Bearer $token", imageMultipart, description)
        call.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _response.value = response.body()
                } else {
                    val jsonObject: JSONObject?
                    try {
                        jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = jsonObject?.getString("message") ?: "Undefined Error"
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
            }

        })
    }


}