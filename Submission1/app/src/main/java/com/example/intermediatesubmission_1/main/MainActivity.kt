package com.example.intermediatesubmission_1.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.ViewModelFactory
import com.example.intermediatesubmission_1.databinding.ActivityMainBinding
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.story.StoryActivity
import com.example.intermediatesubmission_1.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this
        ) { user ->
            if (user.isLogin) {
                binding.helloText.text = getString(R.string.hello_text, user.name)
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener{
            mainViewModel.logout()
        }
        binding.btnHome.setOnClickListener{
            startActivity(Intent(this, StoryActivity::class.java))
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 60000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.helloText, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.mainText, View.ALPHA, 1f).setDuration(500)
        val home = ObjectAnimator.ofFloat(binding.btnHome, View.ALPHA, 1f).setDuration(500)
        val logout = ObjectAnimator.ofFloat(binding.btnLogout, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, message, home, logout)
            startDelay = 500
        }.start()
    }
}