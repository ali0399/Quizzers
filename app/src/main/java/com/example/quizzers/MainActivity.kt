package com.example.quizzers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizzers.ui.theme.QuizzersTheme

class MainActivity : ComponentActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizzersTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Greeting(name = "chaman bahar")
                }
            }
        }
        prefs = getSharedPreferences("QuizerPrefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("LoggedIn", false)
        if (!loggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    @Composable
    fun Greeting(name: String) {
        Column(modifier=Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            profileDetail(username = "Rauson Ali")
            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Hello $name!")
            Button(onClick = {
                startActivity(Intent(this@MainActivity,
                    GamePlay::class.java))
            }) {
                Text(text = "Start Quiz")
            }
        }
    }


    @Composable
    fun profileDetail(username:String){
        Text(text = username)
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        QuizzersTheme {
            Column() {
                profileDetail(username = "Rauson Ali")
                Spacer(modifier = Modifier.padding(8.dp))
                Greeting("Android")
            }

        }
    }
}

