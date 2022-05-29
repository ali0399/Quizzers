package com.example.quizzers.repository

sealed class SafeResponse<T>(val data: T? = null, val errorMsg: String? = null) {
    class Loading<T> : SafeResponse<T>()

    class Success<T>(data: T? = null) : SafeResponse<T>(data = data)

    class Error<T>(errorMsg: String) : SafeResponse<T>(errorMsg = errorMsg)
}