package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.garpoo.cinatix.databinding.ActivitySearchBinding
import com.garpoo.cinatix.data.api.ApiClient
import com.garpoo.cinatix.ui.adapter.UpcomingMoviesPagerAdapter
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModel
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var upcomingMoviesPagerAdapter: UpcomingMoviesPagerAdapter
    private lateinit var viewModel: UpcomingMoviesViewModel
    private lateinit var viewModelFactory: UpcomingMoviesViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
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
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = upcomingMoviesPagerAdapter
        }

        // Observe the movies (for initial display)
        lifecycleScope.launch {
            viewModel.nowPlayingMovies.collectLatest { pagingData ->
                upcomingMoviesPagerAdapter.submitData(pagingData)
            }
        }

        // Handle search input
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    filterMovies(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterMovies(newText)
                }
                return false
            }
        })

        // Adjust insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun filterMovies(query: String) {
        lifecycleScope.launch {
            viewModel.nowPlayingMovies.collectLatest { pagingData ->
                val filteredData = pagingData.filter { movie ->
                    movie.title.contains(query, ignoreCase = true)
                }
                upcomingMoviesPagerAdapter.submitData(filteredData)
            }
        }
    }

    private fun movieToSipnosis(movieId: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }
}
