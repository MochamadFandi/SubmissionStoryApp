package com.fanindo.submissionstoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fanindo.submissionstoryapp.data.remote.api.ApiConfig
import com.fanindo.submissionstoryapp.data.remote.response.LoginRequest
import com.fanindo.submissionstoryapp.data.remote.response.LoginResponse
import com.fanindo.submissionstoryapp.data.remote.response.User
import com.fanindo.submissionstoryapp.model.UserModel
import com.fanindo.submissionstoryapp.model.UserPreference
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val preference: UserPreference) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            preference.saveUser(user)
        }
    }

    fun postLogin(email: String, pass: String) {
        _isLoading.value = true
        val request = LoginRequest(
            email,
            pass
        )

        val client = ApiConfig.getApiService().sendLogin(request)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.value = response.body()?.loginResult
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
            }

        })
    }


}