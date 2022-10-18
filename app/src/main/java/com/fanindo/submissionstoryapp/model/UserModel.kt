package com.fanindo.submissionstoryapp.model

data class UserModel(
    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
)
