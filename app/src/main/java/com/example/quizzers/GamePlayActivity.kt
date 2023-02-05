package com.example.quizzers

import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.databinding.ActivityGamePlayBinding
import com.example.quizzers.network.models.CreateScoreRequestModel
import com.example.quizzers.network.models.Result
import com.example.quizzers.network.models.TbdResponseModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory
import com.google.gson.Gson

const val TOTAL_QUESTIONS = 10

class GamePlayActivity : AppCompatActivity() {
    val TAG = "GamePlay"
    private lateinit var prefs: SharedPreferences

    //    private val mQuizViewModel: QuizViewModel by viewModels()
    private lateinit var profileViewModel: ProfileViewModel
    private var mScore = 0
    private val timeToAnswer = 16000L
    private var correctAttempts = 0
    private val timeLeft = ""
    private val questionToShow = ""
    private val optionsToShow = listOf<String>()
    lateinit var binding: ActivityGamePlayBinding
    lateinit var questions: List<Result>
    var qNbr = 0
    val timer = object : CountDownTimer(timeToAnswer, 1000L) {

        override fun onTick(millisUntilFinished: Long) {
//            Log.d(TAG, "onTick: start Qnbr = $qNbr")
            binding.timerTv.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            Log.d(TAG, "onFinish: start Qnbr= $qNbr")
            binding.timerTv.text = "0"
            if (++qNbr < TOTAL_QUESTIONS) {
                binding.option1Tv.setBackgroundResource(R.drawable.option_bg)
                binding.option2Tv.setBackgroundResource(R.drawable.option_bg)
                binding.option3Tv.setBackgroundResource(R.drawable.option_bg)
                binding.option4Tv.setBackgroundResource(R.drawable.option_bg)
                showQuestion(qNbr, questions[qNbr].question,
                    setupOptions(questions[qNbr].correct_answer,
                        questions[qNbr].incorrect_answers))
                this.start()
            } else {

                AlertDialog.Builder(this@GamePlayActivity)
                    .setTitle("Score")
                    .setMessage("$mScore / 10")
                    .setPositiveButton("OK") { dialog, which ->
                        binding.scoreUploadProgress.visibility = View.VISIBLE
                        val profileService: QuizzerProfileApi =
                            RetrofitHelper.getProfileInstance()
                                .create(QuizzerProfileApi::class.java)
                        //upload score
                        val profileRepository = ProfileRepository(profileService)
                        profileViewModel = ViewModelProvider(this@GamePlayActivity,
                            ProfileViewModelFactory(profileRepository)).get(ProfileViewModel::class.java)
                        val scoreRequest = CreateScoreRequestModel(10, correctAttempts, mScore)
                        val token = prefs.getString("Token", "")!!
                        profileViewModel.createScore(token, scoreRequest)
                        profileViewModel.createScoreResponse.observe(this@GamePlayActivity,
                            Observer {
                                Log.d(TAG, "onFinish: create score : $it")
                                if (it != null)
                                    dialog.cancel()
                            })

                    }
                    .setOnCancelListener {
                        startActivity(Intent(this@GamePlayActivity, HomeActivity::class.java))
                        finish()
                    }
                    .setCancelable(false)
                    .show()
//                Toast.makeText(this@GamePlayActivity,"Quiz finish. Score= $mScore / 10",
//                    Toast.LENGTH_SHORT).show()
                this.cancel()

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("QuizerPrefs", MODE_PRIVATE)

//        val quizService = RetrofitHelper.getQuizInstance().create(QuizzerApi::class.java)
//        val repository = QuizzerRepository(quizService)
//        val quizzerViewModel =
//            ViewModelProvider(this, ViewModelFactory(repository)).get(QuizViewModel::class.java)

        val st = intent.getStringExtra(QUIZ_DATA)
        Log.d(TAG, "onCreate: intent data= $st")
        val quizData = Gson().fromJson(st, TbdResponseModel::class.java)
        runQuiz(quizData)
    }

    fun runQuiz(responseModel: TbdResponseModel) {
        questions = responseModel.results!!
        val optionsToShow =
            setupOptions(questions[qNbr].correct_answer, questions[qNbr].incorrect_answers)
        showQuestion(qNbr, questions[qNbr].question, optionsToShow)
        binding.qNbrTv.text = "1"
        timer.start()

    }

    private fun setClickListeners(btnIndex: Int, optionIndex: Int, correctOptionTv: TextView) {
        Log.d(TAG, "setClickListeners: $btnIndex start")

        val optionTv = when (btnIndex) {
            1 -> binding.option1Tv
            2 -> binding.option2Tv
            3 -> binding.option3Tv
            4 -> binding.option4Tv
            else -> binding.option4Tv
        }
        optionTv.isClickable = true
        if (optionIndex == 0) {
            optionTv.setOnClickListener {
//                Toast.makeText(this, "CORRECT ANSWER!", Toast.LENGTH_SHORT).show()
                correctAttempts++
                optionTv.setBackgroundResource(R.drawable.option_correct_bg)
                updateStats(true)
                timer.cancel()
                disableOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    //Do something after 100ms
                    timer.onFinish()
                }, 2000)
            }
        } else {
            optionTv.setOnClickListener {
//                Toast.makeText(this, "!!!WRONG ANSWER!!!", Toast.LENGTH_SHORT).show()
                disableOptions()
                optionTv.setBackgroundResource(R.drawable.option_wrong_bg)
                Log.d(TAG, "setClickListeners: correct = ${correctOptionTv.text}")
                correctOptionTv.let { it.setBackgroundResource(R.drawable.option_correct_bg) }
                updateStats(false)
                timer.cancel()
                optionTv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_anim))
                Handler(Looper.getMainLooper()).postDelayed({
                    //Do something after 100ms
                    timer.onFinish()
                }, 2000)
            }
        }
    }

    private fun disableOptions() {
        with(binding) {
            option1Tv.isClickable = false
            option2Tv.isClickable = false
            option3Tv.isClickable = false
            option4Tv.isClickable = false
        }
    }

    private fun checkAns(index: Int, optionsToShow: List<List<String>>) {
        Log.d(TAG, "checkAns: start | options = $optionsToShow")
        if (optionsToShow[index][1] == "1") {
//            Toast.makeText(this, "CORRECT ANSWER!", Toast.LENGTH_SHORT).show()
            updateStats(true)
        }
    }

    private fun updateStats(ansIsCorrect: Boolean) {
        if (ansIsCorrect) {
            mScore += 1
        }
    }


    fun showQuestion(qNbr: Int, question: String, options: List<List<String>>) {

        Log.d(TAG, "showQuestion: qNbr = $qNbr")

        binding.qNbrTv.text = (qNbr + 1).toString()
        val optionIndex = mutableListOf<Int>(0, 1, 2, 3)
        Log.d(TAG, "showQuestion: option index before shuffle $optionIndex")
        optionIndex.shuffle()
        Log.d(TAG, "showQuestion: option index after shuffle $optionIndex")
        if (Build.VERSION.SDK_INT >= 24) {
            binding.questionTv.text = Html.fromHtml(question, Html.FROM_HTML_MODE_LEGACY)
        } /*else {
            binding.questionTv.text = Html.fromHtml(question);
            for (i in 0..options.size - 1) {
                when (i) {
                    0 -> binding.option1Tv.text = Html.fromHtml(options[i][0])
                    1 -> binding.option2Tv.text = Html.fromHtml(options[i][0])
                    2 -> binding.option3Tv.text = Html.fromHtml(options[i][0])
                    3 -> binding.option4Tv.text = Html.fromHtml(options[i][0])
                }
            }
        }*/


    }

    fun setupOptions(correctOption: String, wrongOptions: List<String>): List<List<String>> {

        val options = mutableListOf<List<String>>(listOf(correctOption, "1"))
        for (wrongOption in wrongOptions) {
            options.add(listOf(wrongOption, "0"))
        }
        Log.d(TAG, "before shuffle: $options")
//        options.shuffle()
//        Log.d(TAG, "after shuffle: ${options}")
        val optionIndex = mutableListOf<Int>(0, 1, 2, 3)
        Log.d(TAG, "showQuestion: option index before shuffle $optionIndex")
        optionIndex.shuffle()
        var correctOptionTv: TextView
        val correctOptionIndex = optionIndex.indexOf(0)
        correctOptionTv = when (correctOptionIndex) {
            0 -> binding.option1Tv
            1 -> binding.option2Tv
            2 -> binding.option3Tv
            3 -> binding.option4Tv
            else -> binding.option4Tv
        }
        Log.d(TAG, "showQuestion: option index after shuffle $optionIndex")
        if (Build.VERSION.SDK_INT >= 24) {
            binding.option1Tv.text =
                Html.fromHtml(options[optionIndex[0]][0], Html.FROM_HTML_MODE_LEGACY)
            setClickListeners(1, optionIndex[0], correctOptionTv)

            binding.option2Tv.text =
                Html.fromHtml(options[optionIndex[1]][0], Html.FROM_HTML_MODE_LEGACY)
            setClickListeners(2, optionIndex[1], correctOptionTv)

            binding.option3Tv.text =
                Html.fromHtml(options[optionIndex[2]][0], Html.FROM_HTML_MODE_LEGACY)
            setClickListeners(3, optionIndex[2], correctOptionTv)

            binding.option4Tv.text =
                Html.fromHtml(options[optionIndex[3]][0], Html.FROM_HTML_MODE_LEGACY)
            setClickListeners(4, optionIndex[3], correctOptionTv)
        } /*else {
            binding.questionTv.text = Html.fromHtml(question);
            for (i in 0..options.size - 1) {
                when (i) {
                    0 -> binding.option1Tv.text = Html.fromHtml(options[i][0])
                    1 -> binding.option2Tv.text = Html.fromHtml(options[i][0])
                    2 -> binding.option3Tv.text = Html.fromHtml(options[i][0])
                    3 -> binding.option4Tv.text = Html.fromHtml(options[i][0])
                }
            }
        }*/

        return options
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@GamePlayActivity)
            .setTitle("Exit")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                finish()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}