package com.example.intermediatesubmission_1.register

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
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.ViewModelFactory
import com.example.intermediatesubmission_1.databinding.ActivityRegisterBinding
import com.example.intermediatesubmission_1.model.UserModel
import com.example.intermediatesubmission_1.model.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        showLoading(false)
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
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
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener{
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                name.isEmpty() -> {
                    checkInput(name, email, password)
                    binding.nameLayout.error = getString(R.string.name_error)
                }
                email.isEmpty() -> {
                    checkInput(name, email, password)
                    binding.emailLayout.error = getString(R.string.email_error)
                }
                email.isNotEmpty() && !email.contains("@") -> {
                    checkInput(name, email, password)
                    binding.emailLayout.error = getString(R.string.email_not_valid)
                }
                password.isEmpty() -> {
                    checkInput(name, email, password)
                    binding.passwordEditTextLayout.error = getString(R.string.password_empty)
                }
                password.length < 6 -> {
                    checkInput(name, email, password)
                    binding.passwordEditTextLayout.error = getString(R.string.password_minimum)
                }

                else -> {
                    registerViewModel.saveUser(UserModel(name, email, token = "", isLogin = false))
                    registerViewModel.registerUser(name, email, password)

                    registerViewModel.registerCheck.observe(this) { registerCheck ->
                        if (registerCheck) {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert_title))
                                setMessage(getString(R.string.alert_msg_register))
                                setPositiveButton(getString(R.string.alert_btn)) { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                        } else {
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert_title_negative))
                                setMessage(getString(R.string.alert_msg_register_negative))
                                setNegativeButton(getString(R.string.alert_btn_negative)) { _, _ ->
                                    val intent = Intent(context, RegisterActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

    private fun checkInput(name: String, email: String, password: String) {
        if (name.isNotEmpty()) binding.nameLayout.isErrorEnabled = false
        if (email.isNotEmpty() && email.contains("@")) binding.emailLayout.isErrorEnabled = false
        if (password.isNotEmpty() && password.length >= 6) binding.passwordEditTextLayout.isErrorEnabled = false
    }

    private fun showLoading(it: Boolean?){
        binding.progressBarRegister.visibility =
            if (it == true) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val registerText = ObjectAnimator.ofFloat(binding.registerText, View.ALPHA, 1f).setDuration(500)
        val registerDesc = ObjectAnimator.ofFloat(binding.registerDesc, View.ALPHA, 1f).setDuration(500)
        val labelName = ObjectAnimator.ofFloat(binding.labelName, View.ALPHA, 1f).setDuration(500)
        val nameLayout = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1f).setDuration(500)
        val labelEmail = ObjectAnimator.ofFloat(binding.labelEmail, View.ALPHA, 1f).setDuration(500)
        val emailLayout = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(500)
        val labelPassword = ObjectAnimator.ofFloat(binding.labelPassword, View.ALPHA, 1f).setDuration(500)
        val passwordLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(registerText, registerDesc,labelName, nameLayout, labelEmail, emailLayout ,labelPassword, passwordLayout, register)
            startDelay = 500
        }.start()
    }
}