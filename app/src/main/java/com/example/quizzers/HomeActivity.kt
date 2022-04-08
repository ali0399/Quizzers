package com.example.quizzers

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.databinding.ActivityHomeBinding
import com.example.quizzers.databinding.EditUsernameDialogBinding
import com.example.quizzers.network.models.UsernameUpdateModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityHomeBinding
    private var userFirstName = "FirstName"
    private var userLastName = "LastName"
    private val TAG = "HomeActivity"
    private lateinit var token: String
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
        token = prefs.getString("Token", "")!!

        val loggedIn = prefs.getBoolean("LoggedIn", false)
        if (!loggedIn) {
            Log.d(TAG, "onCreate: not logged in")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Log.d(TAG, "onCreate: logged in")
        profileViewModel.getProfileDetail(token)
        profileViewModel.profileDetails.observe(this, Observer {
            userFirstName = it.first_name
            userLastName = it.last_name
            binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
            binding.scoreTv.text = it.total_score.toString()
        })

        binding.startQuizBtn.setOnClickListener {
            startActivity(Intent(this,
                GamePlay::class.java))
        }

        binding.editUsernameBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogBinding = EditUsernameDialogBinding.inflate(layoutInflater)
            dialogBinding.firstNameEt.setText(userFirstName)
            dialogBinding.lastNameEt.setText(userLastName)
            dialog.setView(dialogBinding.root)
                .setTitle("Enter New Username")
                .setPositiveButton("Edit", DialogInterface.OnClickListener { dialog, which ->
                    updateUsername(dialogBinding.firstNameEt.text.toString(),
                        dialogBinding.lastNameEt.text.toString())
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            dialog.show()
        }
    }

    private fun updateUsername(firstName: String, lastName: String) {
        val updateRequest = UsernameUpdateModel(firstName, lastName)
        profileViewModel.updateUsername(token, updateRequest)
        profileViewModel.usernameUpdateResponse.observe(this, Observer {
            binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
        })
    }
}