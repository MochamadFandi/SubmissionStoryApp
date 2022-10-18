package com.fanindo.submissionstoryapp.ui.newstory

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.databinding.ActivityNewStoryBinding
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.home.HomeActivity
import com.fanindo.submissionstoryapp.ui.widget.MyCustomDialog
import com.fanindo.submissionstoryapp.utils.CameraActivity
import com.fanindo.submissionstoryapp.utils.rotateBitmap
import com.fanindo.submissionstoryapp.utils.uriToFile
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var newViewModel: NewStoryViewModel
    private lateinit var dialog: DialogFragment
    private var getFile: File? = null
    private lateinit var token: String

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {

            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.error_permission), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermission()
        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        newViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(UserPreference.getInstance(dataStore), this)
            )[NewStoryViewModel::class.java]

        newViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        newViewModel.response.observe(this) { user ->
            if (user != null) dialogSubmit()
        }

        newViewModel.errorMessage.observe(this) { message ->
            dialog(message)
        }

        newViewModel.getUser().observe(this) { user ->
            token = user.token
        }
    }

    private fun setupAction() {
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            if (validation()) {
                getFile?.let {
                    newViewModel.uploadStory(
                        token,
                        it, binding.edtStory.text.toString()
                    )
                }

            }
        }
    }

    private fun validation(): Boolean {
        val story = binding.edtStory.text.toString()

        when {
            getFile == null -> {
                Toast.makeText(this, getString(R.string.error_null_photo), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            story.isEmpty() -> {
                binding.tilStory.error = resources.getString(R.string.error_null_story)
                return false
            }
        }
        return true
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.intent_chooser))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(CameraActivity.DATA_PICTURE) as File
            val isBackCamera =
                it.data?.getBooleanExtra(CameraActivity.DATA_CAMERA_TYPE, true) as Boolean
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            getFile = myFile
            binding.ivStory.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@NewStoryActivity)
            getFile = myFile
            binding.ivStory.setImageURI(selectedImg)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            dialog = MyCustomDialog()
            dialog.show(supportFragmentManager, TAG)
        } else {
            dialog.dismiss()
        }
    }

    private fun dialogSubmit() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.success_story))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            setCancelable(false)
            create()
            show()
        }
    }


    private fun dialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error))
            setMessage(message)
            setPositiveButton(getString(R.string.close)) { _, _ ->
            }
            setCancelable(true)
            create()
            show()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        private const val TAG = "NewStoryActivity"
    }
}