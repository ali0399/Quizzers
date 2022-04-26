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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.quizzers.databinding.ActivityHomeBinding
import com.example.quizzers.databinding.EditUsernameDialogBinding
import com.example.quizzers.network.models.UsernameUpdateModel
import com.example.quizzers.network.retrofit.QuizzerProfileApi
import com.example.quizzers.network.retrofit.RetrofitHelper
import com.example.quizzers.repository.ProfileRepository
import com.example.quizzers.viewModels.ProfileViewModel
import com.example.quizzers.viewModels.ProfileViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URISyntaxException


// Request code for selecting a PDF document.
const val PICK_PDF_FILE = 2

class HomeActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityHomeBinding
    private var userFirstName = "FirstName"
    private var userLastName = "LastName"
    private val TAG = "HomeActivity"
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: start")
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
            if (it.id != "") {
                userFirstName = it.first_name
                userLastName = it.last_name
                binding.usernameTv.text = getString(R.string.Username, it.first_name, it.last_name)
                it.userprofile.let {
                    Glide.with(this).load(it?.display_picture).into(binding.profileIv)
                }


                binding.scoreTv.text = it.total_score.toString()
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

        binding.startQuizBtn.setOnClickListener {
            startActivity(Intent(this,
                GamePlay::class.java))
            finish()
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
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(),
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
                    cursor = context.getContentResolver()
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
}