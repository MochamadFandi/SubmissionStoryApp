package com.fanindo.submissionstoryapp.ui.signup

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
import com.fanindo.submissionstoryapp.databinding.ActivitySignupBinding
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.welcome.MainActivity
import com.fanindo.submissionstoryapp.ui.widget.MyCustomDialog

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var dialog: DialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

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
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[SignupViewModel::class.java]

        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        signupViewModel.signUpSuccess.observe(this) {
            dialogSubmit()
        }

        signupViewModel.errorMessage.observe(this) { errorMessage ->
            dialog(errorMessage)
        }

    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            if (validation()) {
                signupViewModel.signUp(
                    binding.edtName.text.toString(),
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
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1F).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1F).setDuration(500)
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

        val nameSet = AnimatorSet().apply {
            playTogether(name, nameEdit)
        }

        AnimatorSet().apply {
            playSequentially(logo, title, desc, nameSet, emailSet, passSet, login)
            start()
        }

    }

    private fun validation(): Boolean {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.tilName.error = resources.getString(R.string.error_null_name)
                return false
            }
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
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun dialog(message: String?) {
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
        private const val TAG = "SignUpActivity"
    }

}