package com.fanindo.submissionstoryapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class StoryResponse(
    val error:String,
    val message: String,
    val listStory: List<StoryItem>
)

@Parcelize
data class StoryItem(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) : Parcelable




