package com.example.intermediatesubmission_1.story

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intermediatesubmission_1.R
import com.example.intermediatesubmission_1.adapter.LoadingStateAdapter
import com.example.intermediatesubmission_1.adapter.StoryAdapter
import com.example.intermediatesubmission_1.databinding.ActivityStoryBinding
import com.example.intermediatesubmission_1.model.UserPreference
import com.example.intermediatesubmission_1.view.maps.MapsActivity
import com.example.intermediatesubmission_1.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    private lateinit var rvStory: RecyclerView

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
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        return when (item.itemId) {
            R.id.menu_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("token", token)
                startActivity(intent)

                true
            }
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
        storyViewModel.getUser().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = it.token
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

    private fun getData() {

        rvStory.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter()

        rvStory.adapter = StoryAdapter()
        rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.getUser().observe(this) { user ->
            storyViewModel.story(user.token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }
    }
}