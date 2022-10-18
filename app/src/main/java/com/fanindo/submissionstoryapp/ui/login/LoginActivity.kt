package com.fanindo.submissionstoryapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.databinding.ActivityLoginBinding
import com.fanindo.submissionstoryapp.model.UserModel
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.home.HomeActivity
import com.fanindo.submissionstoryapp.ui.widget.MyCustomDialog

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dialog: DialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.user.observe(this) { user ->
            loginViewModel.saveUser(UserModel(user.name, user.userId, user.token, true))
            dialogSubmit()
        }

        loginViewModel.errorMessage.observe(this) { message ->
            dialog(message)
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

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            if (validation()) {
                loginViewModel.postLogin(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
            }
        }
    }

    private fun playAnimation() {
        val logo = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1F).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvDescLogin, View.ALPHA, 1F).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1F).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1F).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1F).setDuration(500)
        val passEdit = ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1F).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1F).setDuration(500)

        val emailSet = AnimatorSet().apply {
            playTogether(email, emailEdit)
        }

        val passSet = AnimatorSet().apply {
            playTogether(pass, passEdit)
        }

        AnimatorSet().apply {
            playSequentially(logo, title, desc, emailSet, passSet, login)
            start()
        }
    }

    private fun validation(): Boolean {
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.tilEmail.error = resources.getString(R.string.error_null_email)
                return false
            }
            pass.isEmpty() -> {
                binding.tilPassword.error = resources.getString(R.string.error_null_pass)
                return false
            }
        }
        return true
    }

    private fun dialogSubmit() {
        AlertDialog.Builder(this).apply {
            setMessage("Anda Berhasil Login")
            setPositiveButton("Next") { _, _ ->
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
            setTitle("Error")
            setMessage(message)
            setPositiveButton("Close") { _, _ ->
            }
            setCancelable(true)
            create()
            show()
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

    companion object {
        private const val TAG = "LoginActivity"
    }


}