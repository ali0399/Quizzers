package com.example.quizzers

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

class MainActivity2 : ComponentActivity() {
    private val a = 11000L
    private val b = 1000L
    private val c = mutableStateOf(a)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timer = object : CountDownTimer(a, b) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("MYLOG", "text updated programmatically")
                c.value = millisUntilFinished
            }

            override fun onFinish() {
                c.value = 0
            }
        }
        timer.start()
        setContent {
            Log.d("timer ui", "onCreate: start")
            CountDown() }
    }

    @Composable
    fun CountDown() {
        val milliseconds by c
        val text1 = (milliseconds / 1000).toString()
        Text(text1)
    }
}