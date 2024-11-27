package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import com.garpoo.cinatix.databinding.ActivitySearchBinding
import com.garpoo.cinatix.data.api.ApiClient
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.ui.adapter.MoviePagerAdapter
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModel
import com.garpoo.cinatix.ui.viewmodel.UpcomingMoviesViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var movieAdapter: MoviePagerAdapter
    private lateinit var viewModel: UpcomingMoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel
        val apiService = ApiClient.movieApiService
        val viewModelFactory = UpcomingMoviesViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[UpcomingMoviesViewModel::class.java]

        // Setup RecyclerView
        movieAdapter = MoviePagerAdapter(emptyList()) { movieId -> moveToSinopsis(movieId) }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = movieAdapter
        }

        // Observe UpcomingMovies
        observeUpcomingMovies()

        // Setup SearchView
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    filterMovies(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    filterMovies(newText)
                }
                return false
            }
        })

        // Adjust Insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun observeUpcomingMovies() {
        lifecycleScope.launch {
            viewModel.upcomingMovies.collectLatest { pagingData ->
                val movieList = pagingData.map { movie ->
                    movie ?: throw IllegalStateException("Movie cannot be null")
                }
                movieAdapter.updateMovies(movieList.toList())
            }
        }
    }

    private fun filterMovies(query: String) {
        lifecycleScope.launch {
            viewModel.upcomingMovies
                .map { pagingData ->
                    pagingData.map { movie ->
                        if (movie?.title?.contains(query, ignoreCase = true) == true) movie else null
                    }.filterNotNull()
                }
                .collectLatest { filteredPagingData ->
                    movieAdapter.updateMovies(filteredPagingData.toList())
                }
        }
    }

    private fun moveToSinopsis(movieId: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }
}
