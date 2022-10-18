package com.fanindo.submissionstoryapp.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fanindo.submissionstoryapp.data.remote.api.ApiConfig
import com.fanindo.submissionstoryapp.data.remote.response.SignupRequest
import com.fanindo.submissionstoryapp.data.remote.response.SignupResponse
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _signUpSuccess = MutableLiveData<String>()
    val signUpSuccess: LiveData<String> = _signUpSuccess


    fun signUp(name: String, email: String, pass: String) {
        _isLoading.value = true
        val request = SignupRequest(name, email, pass)

        val call = ApiConfig.getApiService().sendSignup(request)
        call.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _signUpSuccess.value = response.body()?.message
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

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                _isLoading.value = true
                _errorMessage.value = t.message
            }
        })
    }
}