package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.garpoo.cinatix.R
import com.garpoo.cinatix.databinding.ActivitySedangTayangBinding
import com.garpoo.cinatix.data.api.ApiClient
import com.garpoo.cinatix.ui.adapter.UpcomingMoviesPagerAdapter
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModel
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModelFactory
import com.garpoo.cinatix.utils.BottomNavigationHandler
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SedangTayangActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySedangTayangBinding
    private lateinit var upcomingMoviesPagerAdapter: UpcomingMoviesPagerAdapter
    private lateinit var viewModel: UpcomingMoviesViewModel
    private lateinit var viewModelFactory: UpcomingMoviesViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySedangTayangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the API service
        val apiService = ApiClient.movieApiService

        // Create the ViewModelFactory
        viewModelFactory = UpcomingMoviesViewModelFactory(apiService)

        // Get the ViewModel using the factory
        viewModel = ViewModelProvider(this, viewModelFactory)[UpcomingMoviesViewModel::class.java]


        // Initialize RecyclerView
        upcomingMoviesPagerAdapter = UpcomingMoviesPagerAdapter { movieId -> movieToSipnosis(movieId) }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SedangTayangActivity, 2)
            adapter = upcomingMoviesPagerAdapter
        }

        // Set up TabLayout
        binding.tabLayout.apply {
            addTab(newTab().setText("SEDANG TAYANG"))
            addTab(newTab().setText("AKAN TAYANG"))
        }

        // Observe Now Playing movies
        lifecycleScope.launch {
            viewModel.nowPlayingMovies.collectLatest { pagingData ->
                upcomingMoviesPagerAdapter.submitData(pagingData)
            }
        }

        // Observe Upcoming movies
        lifecycleScope.launch {
            viewModel.upcomingMovies.collectLatest { pagingData ->
                upcomingMoviesPagerAdapter.submitData(pagingData)
            }
        }

        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadNowPlayingMovies()  // Load Now Playing
                    1 -> viewModel.loadUpcomingMovies()   // Load Upcoming Movies
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                lifecycleScope.launch {
                    upcomingMoviesPagerAdapter.submitData(PagingData.empty())
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadNowPlayingMovies()
                    1 -> viewModel.loadUpcomingMovies()
                }
            }
        })

        // Initial load for selected tab
        val selectedTab = intent.getIntExtra("SELECTED_TAB", 0)
        if (selectedTab == 1) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedTab))
            viewModel.loadUpcomingMovies()
        } else {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(selectedTab))
            viewModel.loadNowPlayingMovies()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        BottomNavigationHandler.handleNavigation(this@SedangTayangActivity,binding.bottomNavigation)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun movieToSipnosis(movieId: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }
}
