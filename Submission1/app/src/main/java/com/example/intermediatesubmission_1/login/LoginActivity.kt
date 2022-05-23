package com.example.intermediatesubmission_1.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.ViewModelFactory
import com.example.intermediatesubmission_1.databinding.ActivityLoginBinding
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.story.StoryActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        showLoading(false)
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
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
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    checkInput(email, password)
                    binding.emailLayout.error = getString(R.string.email_error)
                }
                email.isNotEmpty() && !email.contains("@") -> {
                    checkInput(email, password)
                    binding.emailLayout.error = getString(R.string.email_not_valid)
                }
                password.isEmpty() -> {
                    checkInput(email, password)
                    binding.passwordEditTextLayout.error = getString(R.string.password_empty)
                }
                password.length < 6 -> {
                    checkInput(email, password)
                    binding.passwordEditTextLayout.error = getString(R.string.password_minimum)
                }
                else -> {
                    loginViewModel.loginUser(email, password)

                    loginViewModel.loginCheck.observe(this) { loginCheck ->
                        if (loginCheck) {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert_title))
                                setMessage(getString(R.string.alert_msg_login))
                                setPositiveButton(getString(R.string.alert_btn)) { _, _ ->
                                    val intent = Intent(context, StoryActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()

                                }
                                create()
                                show()
                            }
                        }
                        else if(!loginCheck) {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert_title_negative))
                                setMessage(getString(R.string.alert_msg_login_negative))
                                setNegativeButton(getString(R.string.alert_btn_negative)) {_, _ ->
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }

                    }

                }
            }
        }
    }

    private fun checkInput(email: String, password: String) {
        if (email.isNotEmpty() && email.contains("@")) binding.emailLayout.isErrorEnabled = false
        if (password.isNotEmpty() && password.length >= 6) binding.passwordEditTextLayout.isErrorEnabled = false
    }

    private fun showLoading(it: Boolean) {
        binding.progressBarLogin.visibility =
            if (it) {
                View.VISIBLE
            } else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val loginText = ObjectAnimator.ofFloat(binding.loginText, View.ALPHA, 1f).setDuration(500)
        val loginDesc = ObjectAnimator.ofFloat(binding.loginDesc, View.ALPHA, 1f).setDuration(500)
        val labelEmail = ObjectAnimator.ofFloat(binding.labelEmail, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(500)
        val labelPassword = ObjectAnimator.ofFloat(binding.labelPassword, View.ALPHA, 1f).setDuration(500)
        val passwordLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(loginText, loginDesc, labelEmail, emailLayout ,labelPassword, passwordLayout, login)
            startDelay = 500
        }.start()

    }
}