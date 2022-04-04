package com.example.quizzers.network.models

data class CreateUserRequestModel(
    var username: String,
    var email: String,
    var password: String
)