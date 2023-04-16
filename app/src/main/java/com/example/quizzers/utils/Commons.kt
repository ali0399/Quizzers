package com.example.quizzers.utils

fun String?.isValidEmail() = !isNullOrBlank() && matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
