package com.example.quizzers

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.network.models.Result
import com.example.quizzers.network.retrofit.QuizzerApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.QuizzerRepository
import com.example.quizzers.ui.theme.QuizzersTheme
import com.example.quizzers.viewModels.QuizViewModel
import com.example.quizzers.viewModels.ViewModelFactory
import kotlinx.coroutines.delay

class GamePlay : ComponentActivity() {
    //    lateinit var mQuizViewModel: QuizViewModel
    private val mQuizViewModel: QuizViewModel by viewModels()
    private val mScore = 0
    private val TIME_TO_ANSWER = 10000L
    private val timeLeft = mutableStateOf(TIME_TO_ANSWER)
    private val questionToShow = mutableStateOf("Question Loading...")
    var dummy = Result("category1",
        "type1",
        "difficult1",
        "Question Lorem Ipsum aloo pyaj",
        "correct option",
        listOf("wrong1", "wrong2", "wrong3"))
    private val optionsToShow = mutableStateOf(dummy)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizService = RetrofitHelper.getInstance().create(QuizzerApi::class.java)
        val repository = QuizzerRepository(quizService)
        val quizzerViewModel =
            ViewModelProvider(this, ViewModelFactory(repository)).get(QuizViewModel::class.java)
        val questions: List<Result>? = quizzerViewModel.mQuiz.value?.results
        quizzerViewModel.getQuiz()
        var i = 0
        val timer = object : CountDownTimer(10000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("MYLOG", "text updated programmatically i= $i")
                timeLeft.value = millisUntilFinished
                if(questions!=null) {
                    optionsToShow.value = questions[i++]
                }

            }

            override fun onFinish() {
                timeLeft.value = 0
            }
        }
        timer.start()


        setContent {
            QuizzersTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
//                    //                    val quiz = quizzerViewModel.mQuiz.value?.results
////                    val questions: List<Result>? = quizzerViewModel.mQuiz.value?.results
//                    Log.d("GamePlay", "onCreate: set content")
////                    if (questions != null) {
////                        for (question in questions) {
//                    if (questions != null) {
////            for (question in questions) {
////                optionsToShow.value  = question
////            }
//                        Log.d("setContent", "set question ")
//                        optionsToShow.value = questions[0]
//                    }
                    Quizzing()
//                        }
//                    }
//                    //                    Quizzing(quiz = quizzerViewModel.mQuiz.value?.results!![0])
//                    quizzerViewModel.getQuiz()
                }
            }
        }

    }

    class Timer : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            //todo
            //here you can have your logic to set text to edittext
        }

        override fun onFinish() {
            //todo
        }
    }

    @Composable
    fun Quizzing() {
        val milliseconds by timeLeft
        val timeText = (milliseconds / 1000).toString()
        val quizToShow by optionsToShow

        Column(Modifier.fillMaxWidth()) {
            Text(text = timeText)
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(text = quizToShow.question,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            val options = quizToShow.incorrect_answers + quizToShow.correct_answer
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Card {
                val options1 = 0..3
                val options2 = 3..4

                Column() {
                    for (i in options1) {
                        Text(text = options[i],
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    Toast
                                        .makeText(this@GamePlay,
                                            "item $i clicked",
                                            Toast.LENGTH_SHORT)
                                        .show()
                                },
                            style = TextStyle(fontSize = 25.sp))
                    }
                    Spacer(modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview2() {
        var dummy = Result("category",
            "type",
            "difficult",
            "Question Lorem Ipsum aloo pyaj",
            "correct option",
            listOf("wrong1", "wrong2", "wrong3"))
        QuizzersTheme {
            Quizzing()
        }
    }
}
