package com.fanindo.submissionstoryapp.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fanindo.submissionstoryapp.data.local.entity.Story
import com.fanindo.submissionstoryapp.databinding.ActivityDetailStoryBinding
import com.fanindo.submissionstoryapp.ui.home.HomeActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
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

        val detailTourism = intent.getParcelableExtra<Story>(EXTRA_DATA)
        showDetailStory(detailTourism)

    }

    private fun showDetailStory(detailStory: Story?) {
        detailStory?.let {
            binding.tvDate.text = detailStory.createdAt.withDateFormat()
            binding.tvTitle.text = detailStory.name
            binding.tvDesc.text = detailStory.description
            Glide.with(this).load(detailStory.photoUrl).into(binding.ivStory)
        }

    }

    private fun String.withDateFormat(): String {
        val format = SimpleDateFormat("yyyy-mm-dd", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}