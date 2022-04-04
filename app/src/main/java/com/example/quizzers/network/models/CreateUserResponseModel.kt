package com.example.quizzers.network.models

data class CreateUserResponseModel(
    val user: User,
    val token: String
)