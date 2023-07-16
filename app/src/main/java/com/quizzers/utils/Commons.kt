package com.quizzers.utils

import com.google.gson.Gson
import com.quizzers.network.models.ErrorResponseModel
import okhttp3.ResponseBody

fun String?.isValidEmail() = !isNullOrBlank() && matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))

fun ResponseBody.getErrorMessage(): String {
    return Gson().fromJson(
        this.charStream(),
        ErrorResponseModel::class.java
    ).detail
}