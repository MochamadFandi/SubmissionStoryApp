package com.fanindo.submissionstoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fanindo.submissionstoryapp.data.StoryRepository
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.model.UserModel
import com.fanindo.submissionstoryapp.model.UserPreference
import kotlinx.coroutines.launch

class HomeViewModel(
    private val preference: UserPreference,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getAllStory(token: String): LiveData<PagingData<Story>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)

    //    private val _listStory = MutableLiveData<List<StoryItem>>()
//    val listStory: LiveData<List<StoryItem>> = _listStory
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> = _errorMessage
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//
    fun getUser(): LiveData<UserModel> {
        return preference.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
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