package com.example.quizzers

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizzers.databinding.ActivityLoginBinding
import com.example.quizzers.network.RetrofitHelper
import com.example.quizzers.network.models.CreateUserRequestModel
import com.example.quizzers.network.models.LoginRequestModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.repository.SafeResponse
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var loggedIn = false
    private var isNewUser = false
    private lateinit var prefs: SharedPreferences
    private var buttonText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usernameEt.editText?.imeOptions = ACTION_DOWN
//        binding.usernameEt.editText?.imeOptions = EditorInfo.IME_ACTION_NEXT

        binding.passwordEt.editText?.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.passwordEt.editText?.imeOptions = EditorInfo.IME_ACTION_DONE

        setupUI(binding.root)

        prefs = getSharedPreferences("QuizerPrefs", MODE_PRIVATE)
        var emailId = ""
        var password = ""

        val profileService: QuizzerProfileApi =
            RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
        val profileRepository = ProfileRepository(profileService)
        val createUserRequest = CreateUserRequestModel(
            "ali.ateeq26@gmail.com",
            "ali.ateeq26@gmail.com",
            "password@12"
        )
        val loginRequestBody = LoginRequestModel("rahman.ateeq26@gmail.com", "password@12")
        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(profileRepository)
        ).get(ProfileViewModel::class.java)

        binding.loginBtn.setOnClickListener {
            login()
        }


        binding.switchLoginView.setOnClickListener {
            if (isNewUser) {  //currentView:NewUser| change view for login
                isNewUser = false
                with(binding.loginBtn) {
                    buttonText = "Login"
                    text = buttonText
                    setOnClickListener {
                        login()
                    }
                }
                binding.usernameEt.editText!!.text.clear()
                binding.passwordEt.editText!!.text.clear()
                binding.usernameEt.hint = "Username"
                binding.switchLoginView.text = resources.getText(R.string.switch_to_new_user)
            } else {//currentView:Login| change view for new User
                isNewUser = true
                with(binding.loginBtn) {
                    buttonText = "Create Account"
                    text = buttonText
                    setOnClickListener {
                        createUser()
                    }
                }
                binding.usernameEt.editText!!.text.clear()
                binding.passwordEt.editText!!.text.clear()
                binding.usernameEt.hint = "Email Id"
                binding.switchLoginView.text = resources.getText(R.string.switch_to_login)
            }
        }

    }

    private fun createUser() {
        var emailId = binding.usernameEt.editText?.text.toString()
        var password = binding.passwordEt.editText?.text.toString()

        if (emailId.trim() == "" || password.trim() == "") {
            Toast.makeText(this, "Fields cannot be blank.", Toast.LENGTH_LONG)
                .show()
            return
        }
        val createUserRequestBody = CreateUserRequestModel(emailId, emailId, password)
//
//        createUserRequestBody.username = emailId
//        createUserRequestBody.email = emailId
//        createUserRequestBody.password = password

        Log.d(TAG, "createUser: username = $emailId")

//        profileViewModel.createUser(createUserRequest)
        profileViewModel.createUser(createUserRequestBody)

        profileViewModel.createUserResponse.observe(this, Observer {
            Log.d(TAG, "createUser response: $it")
            when (it) {
                is SafeResponse.Loading -> {
                    disableButtons()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginBtn.text = ""
                }
                is SafeResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data != null) {
                        with(prefs.edit()) {
                            putString("Token", "Token " + it.data.token).apply()
                            putBoolean("LoggedIn", true).apply()
                        }
                        Log.d(TAG, "createUser: start Main Activity")
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
                is SafeResponse.Error -> {
                    enableButtons()
                    binding.loginBtn.text = buttonText
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "CreateUser Error:${it.errorMsg}", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun login() {
        var emailId = binding.usernameEt.editText?.text.toString().trim()
        var password = binding.passwordEt.editText?.text.toString().trim()

        if (emailId == "" || password == "") {
            Toast.makeText(this, "Fields cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }
        val loginRequestBody = LoginRequestModel("rahman.ateeq26@gmail.com", "password@12")

        loginRequestBody.username = emailId
        loginRequestBody.password = password

        Log.d(TAG, "login: username = $emailId")

//        profileViewModel.createUser(createUserRequest)
        profileViewModel.login(loginRequestBody)

        profileViewModel.loginResponse.observe(this, Observer {
            Log.d(TAG, "login: response= ${it.errorMsg}")
            when (it) {
                is SafeResponse.Loading -> {
                    disableButtons()
                    binding.progressBar.visibility = View.VISIBLE
                }
                is SafeResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data != null) {
                        with(prefs.edit()) {
                            putString("Token", "Token " + it.data.token).apply()
                            putBoolean("LoggedIn", true).apply()
                        }
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
                is SafeResponse.Error -> {
                    enableButtons()
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Login Error: ${it.errorMsg}", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun enableButtons() {
        with(binding) {
            loginBtn.isEnabled = true
            switchLoginView.isEnabled = true
        }
    }

    private fun disableButtons() {
        with(binding) {
            loginBtn.isEnabled = false
            switchLoginView.isEnabled = false
        }
    }

    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideSoftKeyboard(this@LoginActivity)
                v.performClick()
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }
}