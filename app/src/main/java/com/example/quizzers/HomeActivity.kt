package com.example.quizzers

import android.animation.ValueAnimator
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.quizzers.repository.Response
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
import java.net.URISyntaxException


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

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: start")
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navDrawerView
        toolbar = binding.toolbar

        navigationView.bringToFront()

        val toggle = ActionBarDrawerToggle(this,
            drawerLayout,
            toolbar,
            R.string.navDrawerOpen,
            R.string.navDrawerClose)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_leaderboard -> {
                    Toast.makeText(this, "LeaderBoard", Toast.LENGTH_SHORT).show()
                    showLeaderboard()
                }
                R.id.nav_logout -> {
                    logout()
                    Toast.makeText(this, "nav_logout", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_about -> {
                    showCredits(findViewById<ConstraintLayout>(R.id.scoreContainer))
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        categoryJson = Gson().fromJson(CATEGORY_JSON_STRING, CategoryObjectList::class.java)


        val profileService =
            RetrofitHelper.getProfileInstance().create(QuizzerProfileApi::class.java)
        val profileRepository = ProfileRepository(profileService)
        profileViewModel = ViewModelProvider(this,
            ProfileViewModelFactory(profileRepository)).get(ProfileViewModel::class.java)

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
            if (it != null && it.id != "") {
                userFirstName = if (it.first_name == ("")) "Quiz" else it.first_name
                userLastName = if (it.last_name == "") "Master" else it.last_name
                binding.usernameTv.text = if (getString(R.string.Username,
                        it.first_name,
                        it.last_name) == " "
                ) "Quiz Master" else getString(R.string.Username, it.first_name, it.last_name)
                it.userprofile?.let {
                    try {
                        Log.d(TAG, "onCreate: null userProfile: ${it}")
                        Glide.with(this).load(it.display_picture)
                            .placeholder(R.drawable.ic_baseline_person_24).into(binding.profileIv)
                    } catch (e: Exception) {
                        Log.d(TAG, "onCreate: userProfile Pic execption: $e")
                    }

                }

                ValueAnimator.ofInt(0, it.total_score).apply {
                    duration = 1500L
                    addUpdateListener { updatedAnimation ->
                        // You can use the animated value in a property that uses the
                        // same type as the animation. In this case, you can use the
                        // float value in the translationX property.
                        binding.scoreTv.text = updatedAnimation.animatedValue.toString()
                    }
                    start()
                }

                drawerLayout.findViewById<MaterialTextView>(R.id.headerUsernameTV).text =
                    it.email.toString()


            } else {
                Log.d(TAG, "onCreate: Error fetching profile details")
                Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show()
                userFirstName = "???"
                userLastName = "???"
                binding.apply {
                    usernameTv.text = getString(R.string.Username, userFirstName, userLastName)
                    scoreTv.text = "???"
                    profileIv.setImageResource(R.drawable.ic_connection_error)
                    startQuizBtn.isEnabled = false
                }

            }
        })
        val quizService = RetrofitHelper.getQuizInstance().create(QuizzerApi::class.java)
        val repository = QuizzerRepository(quizService)
        quizViewModel =
            ViewModelProvider(this, ViewModelFactory(repository)).get(QuizViewModel::class.java)

        binding.startQuizBtn.setOnClickListener {
            showCategoryDialog()

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
        binding.uploadPicBtn.setOnClickListener {
            pickFile()
        }
        //category spinner
//        val cats = resources.getStringArray(R.array.categories)
//        with(binding.catSpinner) {
//            onItemSelectedListener = this@HomeActivity
//            setPopupBackgroundDrawable(getDrawable(R.drawable.btn_round_bg))
//        }
//        val ad = ArrayAdapter<String>(this,
//            R.layout.category_spinner_collapse, cats)
//
//        ad.setDropDownViewResource(R.layout.spinner_item_layout)
//        binding.catSpinner.adapter = ada

        getPermissions()
    }


    private fun getPermissions() {
        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQ_CODE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_REQ_CODE -> Toast.makeText(this,
                "Thank You for the Permission",
                Toast.LENGTH_SHORT).show()
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
            LayoutInflater.from(this))

        val dialog = dialogBuilder.setView(catgBinding.root)
            .setNegativeButton("Cancel") { dia, _ ->
                dia.dismiss()
            }
            .setTitle("Choose Category")
            .show()

        val adapter = CategoryListAdapter { position ->
            Log.d(TAG, "showCategoryDialog: $position")
            binding.progressBar.visibility = View.VISIBLE
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
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    if (it.data != null) {
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this,
                            GamePlay::class.java).putExtra(QUIZ_DATA, Gson().toJson(it.data)))
                        finish()
                    }

                }
                is Response.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this,
                        "Network Error: ${it.errorMsg}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showLeaderboard() {
        profileViewModel.getLeaderboard(token)
        profileViewModel.leaderboardResponse.observe(this, Observer {
            val lbBinding: LeaderboardDialogLayoutBinding =
                LeaderboardDialogLayoutBinding.inflate(
                    LayoutInflater.from(this))
//                val adapter = LeaderboardRVAdapter()
//                adapter.list = it
            val adapter = LeaderboardListAdapter()
            adapter.submitList(it)
            lbBinding.recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            lbBinding.recyclerView.adapter = adapter

            val dialog = AlertDialog.Builder(this)
                .setView(lbBinding.root)
                .setNegativeButton("Close") { dialog, which -> dialog.dismiss() }

//            val dialog = Dialog(this)
//            dialog.setContentView(lbBinding.root)
//            val rv=dialog.findViewById<RecyclerView>(R.id.recyclerView)
//            rv.adapter=adapter
//            dialog.set("Close") { dialog, which -> dialog.dismiss() }


            Log.d(TAG, "showLeaderboard: ${it[0].id}")
            dialog.show()
        })
    }

    private fun showCredits(view: ConstraintLayout) {
        val popUpClass = PopUpClass()
        popUpClass.showPopupWindow(view)
    }

    private fun updateUsername(firstName: String, lastName: String) {
        val updateRequest = UsernameUpdateModel(firstName, lastName)
        profileViewModel.updateUsername(token, updateRequest)
        profileViewModel.usernameUpdateResponse.observe(this, Observer {
            binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
        })
    }

    fun pickFile(pickerInitialUri: Uri = Uri.parse("")) {
        val intent = Intent()
            .setType("image/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_PDF_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            val selectedFile = intent?.data //The uri with the location of the file
            val path = getRealPathFromURI(this, selectedFile)
            val file = File(path)
            binding.profileIv.setImageURI(selectedFile)
            Log.d(TAG, "onActivityResult: $selectedFile")
            Log.d(TAG, "onActivityResult: fileSystemPath = $path")
            val part = MultipartBody.Part.createFormData("display_picture", path,
                file.asRequestBody("image/*".toMediaTypeOrNull()))
            profileViewModel.uploadPhoto(token, part)
        }

    }

    @Throws(URISyntaxException::class)
    fun getRealPathFromURI(context: Context, uri: Uri?): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (uri != null) {
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.applicationContext,
                    uri)
            ) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id))
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(
                        split[1]
                    )
                }
            }
            if ("content".equals(uri!!.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index: Int =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: java.lang.Exception) {
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
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

}