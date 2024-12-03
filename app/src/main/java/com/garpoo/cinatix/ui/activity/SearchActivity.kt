package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.garpoo.cinatix.R
import com.garpoo.cinatix.databinding.ActivitySearchBinding
import com.garpoo.cinatix.data.api.ApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.garpoo.cinatix.ui.adapter.UpcomingMoviesPagerAdapter
import com.garpoo.cinatix.ui.viewmodel.SearchMoviesViewModel
import com.garpoo.cinatix.ui.viewmodel.SearchMoviesViewModelFactory
import com.garpoo.cinatix.utils.BottomNavigationHandler

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var movieAdapter: UpcomingMoviesPagerAdapter
    private lateinit var viewModel: SearchMoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Setup ViewModel
        val apiService = ApiClient.movieApiService
        val viewModelFactory = SearchMoviesViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchMoviesViewModel::class.java]

        // Setup RecyclerView
        movieAdapter = UpcomingMoviesPagerAdapter { movieId -> moveToSinopsis(movieId) }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = movieAdapter
        }

        lifecycleScope.launch {
            viewModel.movies.collectLatest { pagingData ->
                movieAdapter.submitData(pagingData)
            }
        }

        // Setup SearchView
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    query.let {
                        viewModel.searchMovies(it)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    newText.let {
                        viewModel.searchMovies(it)
                    }
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
        binding.bottomNavigation.selectedItemId = R.id.nav_search
        BottomNavigationHandler.handleNavigation(this@SearchActivity,binding.bottomNavigation)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bottomNavigation.selectedItemId = R.id.nav_search
    }

    private fun moveToSinopsis(movieId: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }
}
