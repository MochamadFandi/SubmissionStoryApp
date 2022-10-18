package com.fanindo.submissionstoryapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fanindo.submissionstoryapp.di.Injection
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.home.HomeViewModel
import com.fanindo.submissionstoryapp.ui.login.LoginViewModel
import com.fanindo.submissionstoryapp.ui.map.MapsViewModel
import com.fanindo.submissionstoryapp.ui.newstory.NewStoryViewModel
import com.fanindo.submissionstoryapp.ui.signup.SignupViewModel

class ViewModelFactory(private val pref: UserPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel() as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(pref, Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(NewStoryViewModel::class.java) -> {
                NewStoryViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref, Injection.provideRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

}