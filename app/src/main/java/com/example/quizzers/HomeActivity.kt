package com.example.quizzers

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.databinding.ActivityHomeBinding
import com.example.quizzers.network.models.CreateScoreRequestModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityHomeBinding
    private val TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileService =
            RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
        val profileRepository = ProfileRepository(profileService)
        profileViewModel = ViewModelProvider(this,
            ProfileViewModelFactory(profileRepository)).get(ProfileViewModel::class.java)

        prefs = getSharedPreferences("QuizerPrefs", MODE_PRIVATE)

        val loggedIn = prefs.getBoolean("LoggedIn", false)
        if (!loggedIn) {
            Log.d(TAG, "onCreate: not logged in")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Log.d(TAG, "onCreate: logged in")
        val token = prefs.getString("Token", "")!!
        profileViewModel.getProfileDetail(token)
        profileViewModel.profileDetails.observe(this, Observer {
            binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
            binding.scoreTv.text = it.total_score.toString()

        })
        binding.startQuizBtn.setOnClickListener {
            startActivity(Intent(this,
                GamePlay::class.java))
        }
    }
}