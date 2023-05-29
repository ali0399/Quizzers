package com.example.quizzers.network.models

import com.google.gson.annotations.SerializedName

data class ProfileDetailResponseModel(
    val email: String = "",
    val first_name: String = "",
    val id: String = "",
    val last_name: String = "",
    val position: Int = 0,
    val tdb_token: String? = null,
    val total_score: Int = 0,
    @SerializedName("username")
    val userName: String = "",
    @SerializedName("userprofile")
    val userProfile: Userprofile? = null,
    @SerializedName("userstreak")
    val userStreak: UserStreak? = null
)

data class UserStreak(
    @SerializedName("current_streak")
    val currentStreak: Int = 0,
    @SerializedName("longest_streak")
    val longestStreak: Int = 0
)

data class Userprofile(
    val display_picture: String,
)