package com.fanindo.submissionstoryapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fanindo.submissionstoryapp.data.StoryRepository
import com.fanindo.submissionstoryapp.data.remote.response.StoryResponse
import com.fanindo.submissionstoryapp.model.UserModel
import com.fanindo.submissionstoryapp.model.UserPreference
import kotlinx.coroutines.flow.Flow


class MapsViewModel(
    private val preference: UserPreference,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getAllStory(token: String): Flow<Result<StoryResponse>> =
        storyRepository.getAllMarker(token)

//    private val _listStory = MutableLiveData<List<StoryItem>>()
//    val listStory: LiveData<List<StoryItem>> = _listStory
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> = _errorMessage
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading


    fun getUser(): LiveData<UserModel> {
        return preference.getUser().asLiveData()
    }

//    fun getStory(token: String) {
//        _isLoading.value = true
//        val call = ApiConfig.getApiService().getAllStories("Bearer $token")
//        call.enqueue(object : Callback<StoryResponse> {
//            override fun onResponse(
//                call: Call<StoryResponse>,
//                response: Response<StoryResponse>
//            ) {
//                _isLoading.value = false
//                if (response.isSuccessful) {
//                    _listStory.value = response.body()?.listStory
//                } else {
//                    val jsonObject: JSONObject?
//                    try {
//                        jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
//                        _errorMessage.value = jsonObject?.getString("message") ?: "Undefined Error"
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
//                _isLoading.value = false
//                _errorMessage.value = t.message
//            }
//
//        })
//
//    }
}