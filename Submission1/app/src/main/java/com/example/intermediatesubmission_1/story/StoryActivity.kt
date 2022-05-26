package com.example.intermediatesubmission_1.story

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.ViewModelFactory
import com.example.intermediatesubmission_1.databinding.ActivityStoryBinding
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.response.ListStoryItem
import com.example.intermediatesubmission_1.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel

    private lateinit var rvStory: RecyclerView
    private val list = ArrayList<ListStoryItem>()

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()

        rvStory = binding.rvStory
        rvStory.setHasFixedSize(true)
        showListStory(list)

        storyViewModel.listStory.observe(this)
        {
            showListStory(it)
        }

        showLoading(true)
        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.alert_title_logout))
                    setMessage(getString(R.string.alert_msg_logout))
                    setPositiveButton(getString(R.string.alert_btn_logout)) { _, _ ->
                        storyViewModel.logout()
                        finish()
                    }
                    create()
                    show()
                }
                true
            }
            else -> true
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
    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[StoryViewModel::class.java]

        storyViewModel.getUser().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = it.token
                storyViewModel.listStories(token)
            }
        }

    }

    private fun setupAction(){
        binding.fabAdd.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }

    private fun showListStory(list: List<ListStoryItem>) {
        rvStory.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter(list)

        if (adapter.itemCount == 0) {
            binding.imgStoryNotFound.visibility = View.VISIBLE
            binding.textNotFound.visibility = View.VISIBLE
        } else {
            rvStory.adapter = adapter
            binding.imgStoryNotFound.visibility = View.GONE
            binding.textNotFound.visibility = View.GONE
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarListStory.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE

    }
}