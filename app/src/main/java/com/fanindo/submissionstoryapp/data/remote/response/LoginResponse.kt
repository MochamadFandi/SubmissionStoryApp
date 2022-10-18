package com.fanindo.submissionstoryapp.data.remote.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: User
)

data class User(
    val userId: String,
    val name: String,
    val token: String,
)

data class LoginRequest(
    val email: String,
    val password: String
)