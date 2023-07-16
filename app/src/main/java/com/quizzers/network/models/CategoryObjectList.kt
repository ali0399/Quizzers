package com.quizzers.network.models

import androidx.annotation.Keep

@Keep
data class CategoryObjectList(
    val trivia_categories: List<TriviaCategory>,
)