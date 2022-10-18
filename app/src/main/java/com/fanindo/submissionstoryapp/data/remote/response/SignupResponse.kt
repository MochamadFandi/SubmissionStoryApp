package com.fanindo.submissionstoryapp.data.remote.response


data class SignupResponse(
    val error: Boolean,
    val message: String,
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)