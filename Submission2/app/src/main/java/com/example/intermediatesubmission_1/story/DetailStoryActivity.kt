package com.example.intermediatesubmission_1.story

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.intermediatesubmission_1.databinding.ActivityDetailStoryBinding
import com.example.intermediatesubmission_1.response.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
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
    }

    private fun setupAction() {
        val story = intent.getParcelableExtra<ListStoryItem>("story") as ListStoryItem

        Glide.with(applicationContext)
            .load(story.photoUrl)
            .apply(RequestOptions().override(360, 150))
            .into(binding.imgItemStory)
        binding.nameStory.text = story.name
        binding.descStory.text = story.description
    }

}