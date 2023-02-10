package com.example.quizzers

import android.animation.ValueAnimator
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quizzers.databinding.ActivityHomeBinding
import com.example.quizzers.databinding.CategoryDialogLayoutBinding
import com.example.quizzers.databinding.EditUsernameDialogBinding
import com.example.quizzers.databinding.LeaderboardDialogLayoutBinding
import com.example.quizzers.network.CATEGORY_JSON_STRING
import com.example.quizzers.network.catgList
import com.example.quizzers.network.models.CategoryObjectList
import com.example.quizzers.network.models.UsernameUpdateModel
import com.example.quizzers.network.retrofit.QuizzerApi
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.repository.QuizzerRepository
import com.example.quizzers.repository.SafeResponse
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory
import com.example.quizzers.viewModels.QuizViewModel
import com.example.quizzers.viewModels.ViewModelFactory
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


// Request code for selecting a PDF document.

const val PICK_PDF_FILE = 2
const val QUIZ_DATA = "QuizData"
const val LOGGED_IN = "LoggedIn"
const val TOKEN = "Token"
const val PERMISSION_REQ_CODE = 123
private const val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityHomeBinding
    private var userFirstName = "FirstName"
    private var userLastName = "LastName"
    private lateinit var token: String
    private lateinit var categoryJson: CategoryObjectList

    //Variables
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    var toolbar: Toolbar? = null
    var menu: Menu? = null
    var drawerState = false

    //ActivityResultContracts
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileIv.setImageResource(R.drawable.ic_baseline_cloud_upload_24)
                uploadPic(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: start")
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navDrawerView
//        toolbar = binding.toolbar

        navigationView.bringToFront()

        val hamMenu = binding.menuBtn

        hamMenu.setOnClickListener {
            // If the navigation drawer is not open then open it, if its already open then close it.
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(
                GravityCompat.START
            ) else drawerLayout.closeDrawer(
                GravityCompat.END
            )
        }

        val adapter = CategoryListAdapter { position ->
            Log.d(TAG, "showCategoryDialog: $position")
            binding.catgRV.isClickable = false

            if (position == 0)
                quizViewModel.quizOptions.value?.set("category", "0")   //mixed bag
            else
                quizViewModel.quizOptions.value?.set("category", "${position + 8}")

            getQuestions()
        }

        binding.catgRV.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        adapter.submitList(catgList)
        binding.catgRV.adapter = adapter

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_leaderboard -> {
//                    Toast.makeText(this, "LeaderBoard", Toast.LENGTH_SHORT).show()
                    showLeaderboard()
                }
                R.id.nav_logout -> {
                    logout()
//                    Toast.makeText(this, "nav_logout", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_about -> {
                    Log.d(TAG, "onCreate: nav_about")
                    showCredits(findViewById(R.id.scoreContainer))
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        categoryJson = Gson().fromJson(CATEGORY_JSON_STRING, CategoryObjectList::class.java)


        val profileService =
            RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
        val profileRepository = ProfileRepository(profileService)
        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(profileRepository)
        ).get(ProfileViewModel::class.java)

        prefs = getSharedPreferences("QuizerPrefs", MODE_PRIVATE)
        token = prefs.getString(TOKEN, "")!!

        val loggedIn = prefs.getBoolean(LOGGED_IN, false)
        if (!loggedIn) {
            Log.d(TAG, "onCreate: not logged in")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Log.d(TAG, "onCreate: logged in")
        profileViewModel.getProfileDetail(token)
        profileViewModel.profileDetails.observe(this, Observer {
            when (it) {
                is SafeResponse.Success -> {
                    enableButtons()
                    it.data.run {
                        if ((this != null) && (this.id != "")) {
                            binding.homeShimmer.stopShimmer()
                            binding.homeShimmer.visibility = View.GONE
                            binding.bgLayer.root.visibility = View.GONE
                            binding.homeContainer.visibility = View.VISIBLE
                            binding.errorLayer.root.visibility = View.GONE
                            drawerLayout.findViewById<MaterialTextView>(R.id.headerUsernameTV).text =
                                this.email
                            userFirstName = if (this.first_name == ("")) "Quiz" else this.first_name
                            userLastName = if (this.last_name == "") "Master" else this.last_name
                            binding.usernameTv.text = if (getString(
                                    R.string.Username,
                                    this.first_name,
                                    this.last_name
                                ) == " "
                            ) "Quiz Master" else getString(
                                R.string.Username,
                                this.first_name,
                                this.last_name
                            )
                            this.userprofile?.let {
                                try {
                                    Log.d(TAG, "onCreate: null userProfile: $it")
                                    Glide.with(this@HomeActivity).load(it.display_picture)
                                        .placeholder(R.drawable.ic_baseline_person_24)
                                        .error(R.drawable.ic_connection_error)
                                        .into(binding.profileIv)
                                } catch (e: Exception) {
                                    Log.d(TAG, "onCreate: userProfile Pic execption: $e")
                                }
                            }

                            ValueAnimator.ofInt(0, this.total_score).apply {
                                duration = 1500L
                                addUpdateListener { updatedAnimation ->
                                    // You can use the animated value in a property that uses the
                                    // same type as the animation. In this case, you can use the
                                    // float value in the translationX property.
                                    binding.scoreTv.text = updatedAnimation.animatedValue.toString()
                                    binding.scoreTv.setOnClickListener {
                                        showLeaderboard()
                                    }
                                }
                                start()
                            }
                        }
                    }
                }
                is SafeResponse.Error -> {
                    if (it.errorMsg == "Authorisation error") {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    enableButtons()
                    Log.d(TAG, "onCreate: Error fetching profile details")
                    binding.homeShimmer.stopShimmer()
                    binding.homeShimmer.visibility = View.GONE
                    binding.bgLayer.root.visibility = View.GONE
                    binding.homeContainer.visibility = View.GONE
                    binding.errorLayer.root.visibility = View.VISIBLE

                    binding.errorLayer.button.setOnClickListener {
                        profileViewModel.getProfileDetail(token)
                    }
                    Toast.makeText(this, "Error: ${it.errorMsg}", Toast.LENGTH_SHORT).show()
                    userFirstName = "???"
                    userLastName = "???"
                    binding.apply {
                        usernameTv.text = getString(R.string.Username, userFirstName, userLastName)
                        scoreTv.text = "???"
                        profileIv.setImageResource(R.drawable.ic_connection_error)
//                        startQuizBtn.isEnabled = false
                    }

                }
                is SafeResponse.Loading -> {
                    disableButtons()
                    binding.homeShimmer.startShimmer()
                    binding.homeShimmer.visibility = View.VISIBLE
                    binding.bgLayer.root.visibility = View.VISIBLE
                    binding.homeContainer.visibility = View.GONE
                    binding.errorLayer.root.visibility = View.GONE

                }
            }

        })
        val quizService = RetrofitHelper.getQuizInstance().create(QuizzerApi::class.java)
        val repository = QuizzerRepository(quizService)
        quizViewModel =
            ViewModelProvider(this, ViewModelFactory(repository)).get(QuizViewModel::class.java)

//        binding.startQuizBtn.setOnClickListener {
//            showCategoryDialog()
//
//        }

        binding.editUsernameBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogBinding = EditUsernameDialogBinding.inflate(layoutInflater)
            dialogBinding.firstNameEt.setText(userFirstName)
            dialogBinding.lastNameEt.setText(userLastName)
            dialog.setView(dialogBinding.root)
                .setTitle("Enter New Username")
                .setPositiveButton("Edit", DialogInterface.OnClickListener { dialog, which ->
                    updateUsername(
                        dialogBinding.firstNameEt.text.toString(),
                        dialogBinding.lastNameEt.text.toString()
                    )
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            dialog.show()
        }
        binding.uploadPicBtn.setOnClickListener {
            getPermissions()
        }
    }



    private fun getPermissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQ_CODE
            )
        else pickFile()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_REQ_CODE -> {
                Toast.makeText(
                    this,
                    "Thank You for the Permission",
                    Toast.LENGTH_SHORT
                ).show()
                pickFile()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun logout() {
        profileViewModel.logout(token)
        profileViewModel.logoutResponseCd.observe(this, Observer {
            if (it != "0") {
                prefs.edit().apply {
                    putBoolean(LOGGED_IN, false)
                    putString(TOKEN, "")
                }.apply()
                Log.d(TAG, "logout: code= $it")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else Toast.makeText(this, "Error logging out: $it", Toast.LENGTH_SHORT).show()
        })

    }

    private fun showCategoryDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        val catgBinding: CategoryDialogLayoutBinding = CategoryDialogLayoutBinding.inflate(
            LayoutInflater.from(this)
        )

        val dialog = dialogBuilder.setView(catgBinding.root)
            .setNegativeButton("Cancel") { dia, _ ->
                dia.dismiss()
            }
            .setTitle("Choose Category")
            .show()

        val adapter = CategoryListAdapter { position ->
            Log.d(TAG, "showCategoryDialog: $position")
            catgBinding.catgRV.isClickable = false
            dialog.cancel()

            if (position == 0)
                quizViewModel.quizOptions.value?.set("category", "0")   //mixed bag
            else
                quizViewModel.quizOptions.value?.set("category", "${position + 8}")

            getQuestions()
        }

        catgBinding.catgRV.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        adapter.submitList(catgList)
        catgBinding.catgRV.adapter = adapter
    }

    private fun getQuestions() {
        quizViewModel.getQuiz()

        quizViewModel.quiz.observe(this, Observer {
            when (it) {
                is SafeResponse.Loading -> {
                    disableButtons()
                    binding.lottieLoading.visibility = View.VISIBLE
                    binding.lottieLoading.setOnClickListener {
                        return@setOnClickListener
                    }

                }
                is SafeResponse.Success -> {
                    if (it.data != null) {
                        binding.lottieLoading.visibility = View.GONE
                        startActivity(
                            Intent(this, GamePlayActivity::class.java)
                                .putExtra(QUIZ_DATA, Gson().toJson(it.data))
                        )
                        finish()
                    }

                }
                is SafeResponse.Error -> {
                    enableButtons()
                    binding.lottieLoading.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Network Error: ${it.errorMsg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun showLeaderboard() {
        val lbBinding: LeaderboardDialogLayoutBinding =
            LeaderboardDialogLayoutBinding.inflate(
                LayoutInflater.from(this)
            )
//                val adapter = LeaderboardRVAdapter()
//                adapter.list = it
        val adapter = LeaderboardListAdapter()
        adapter.submitList(null)
        lbBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lbBinding.recyclerView.adapter = adapter
        val dialog = AlertDialog.Builder(this)
            .setView(lbBinding.root)
            .setNegativeButton("Close") { dialog, which -> dialog.dismiss() }

        dialog.show()
        profileViewModel.getLeaderboard(token)
        profileViewModel.leaderboardResponse.observe(this, Observer {
            if (it !== null) {
                //observer is called more than once (find why) so only update the list in observer
                Log.d(TAG, "showLeaderboard: start: ${it[0]}")
                adapter.submitList(it)
            }
        })
    }

    private fun showCredits(view: CardView) {
        Log.d(TAG, "showCredits: start")
        val popUpClass = PopUpClass()
        popUpClass.showPopupWindow(view)
    }

    private fun updateUsername(firstName: String, lastName: String) {
        if (firstName != "" || lastName != "") {
            val updateRequest = UsernameUpdateModel(firstName, lastName)
            profileViewModel.updateUsername(token, updateRequest)
            profileViewModel.usernameUpdateResponse.observe(this, Observer {
                binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
            })
        } else {
            Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickFile() {
        selectImageFromGalleryResult.launch("image/*")
    }


    private fun uploadPic(fileUri: Uri) {
        val parcelFileDescriptor = this.contentResolver.openFileDescriptor(fileUri, "r", null)
        parcelFileDescriptor?.let {
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(fileUri))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            Log.d(TAG, "uploadPic: path= ${file.path}")

            val part = MultipartBody.Part.createFormData(
                "display_picture", file.path,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            profileViewModel.uploadPhoto(token, part)
            profileViewModel.uploadResponse.observe(this, Observer {
                when (it) {
                    is SafeResponse.Loading -> {
                        disableButtons()
                        binding.profileIv.setImageResource(R.drawable.ic_baseline_cloud_upload_24)
                    }
                    is SafeResponse.Success -> {
                        enableButtons()
                        binding.profileIv.setImageURI(fileUri)
                    }
                    is SafeResponse.Error -> {
                        enableButtons()
                        binding.profileIv.setImageResource(R.drawable.ic_connection_error)
                    }
                }
            })
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        setCategory(position)
        Log.d(TAG, "onItemSelected:  ${quizViewModel.quizOptions.value}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        quizViewModel.quizOptions.value?.set("category", "0")
        Log.d(TAG, "onNothingSelected:  ${quizViewModel.quizOptions.value}")
    }

    private fun setCategory(position: Int) {
        if (position == 0)
            quizViewModel.quizOptions.value?.set("category", "0")   //mixed bag
        else
            quizViewModel.quizOptions.value?.set("category", "${position + 8}")
    }

    fun ContentResolver.getFileName(fileUri: Uri): String {

        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    private fun enableButtons() {
        with(binding) {
            editUsernameBtn.isEnabled = true
//            startQuizBtn.isEnabled = true
            uploadPicBtn.isEnabled = true
        }
    }

    private fun disableButtons() {
        with(binding) {
            editUsernameBtn.isEnabled = false
//            startQuizBtn.isEnabled = false
            uploadPicBtn.isEnabled = false
        }
    }

}