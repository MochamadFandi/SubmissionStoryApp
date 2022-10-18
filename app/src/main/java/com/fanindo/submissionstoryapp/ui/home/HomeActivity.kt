package com.fanindo.submissionstoryapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.fanindo.submissionstoryapp.R
import com.fanindo.submissionstoryapp.adapter.LoadingStateAdapter
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.databinding.ActivityHomeBinding
import com.fanindo.submissionstoryapp.model.UserPreference
import com.fanindo.submissionstoryapp.ui.ViewModelFactory
import com.fanindo.submissionstoryapp.ui.map.MapsActivity
import com.fanindo.submissionstoryapp.ui.newstory.NewStoryActivity
import com.fanindo.submissionstoryapp.ui.welcome.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var handler: Handler
    private var doubleBackToExitPressedOnce = false
    private var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
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
        handler = Handler(Looper.myLooper()!!)
    }

    private fun setupViewModel() {
        homeViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(UserPreference.getInstance(dataStore), this)
            )[HomeViewModel::class.java]

        homeViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                binding.tvName.text = getString(R.string.halo_name, user.name)
                token = user.token
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        homeViewModel.getAllStory(token).observe(this) { stories ->
            if (stories == null) {
                binding.tvEmptyStory.visibility = View.VISIBLE
            } else {
                binding.tvEmptyStory.visibility = View.INVISIBLE
                setStoryData(stories)
            }
        }
    }

    private fun setupAction() {
        binding.ivLogout.setOnClickListener {
            homeViewModel.logout()
        }

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, NewStoryActivity::class.java))
        }

        binding.tvViewMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun setStoryData(stories: PagingData<Story>) {

        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.submitData(lifecycle, stories)

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.double_back), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}