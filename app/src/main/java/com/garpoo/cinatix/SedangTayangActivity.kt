package com.garpoo.cinatix

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.garpoo.cinatix.adapter.MovieItemRecyclerAdapter
import com.garpoo.cinatix.databinding.ActivitySedangTayangBinding
import com.garpoo.cinatix.model.Movie
import com.garpoo.cinatix.model.UpcomingMoviesResponse
import com.garpoo.cinatix.network.ApiClient
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SedangTayangActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySedangTayangBinding
    private lateinit var movieItemRecyclerAdapter: MovieItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySedangTayangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        movieItemRecyclerAdapter = MovieItemRecyclerAdapter(listOf())
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SedangTayangActivity, 2)
            adapter = movieItemRecyclerAdapter
        }

        binding.tabLayout.apply {
            addTab(newTab().setText("SEDANG TAYANG"))
            addTab(newTab().setText("AKAN TAYANG"))
        }

        val selectedTab = intent.getIntExtra("SELECTED_TAB", 0)
        binding.tabLayout.getTabAt(selectedTab)?.select()

        if (selectedTab == 1) {
            loadComingSoonMovies()
        } else {
            loadNowPlayingMovies()
        }

        // Set up TabLayout listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadNowPlayingMovies()
                    1 -> loadComingSoonMovies()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                populateRecyclerView(listOf())
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                populateRecyclerView(listOf())
                when (tab?.position) {
                    0 -> loadNowPlayingMovies()
                    1 -> loadComingSoonMovies()
                }
            }
        })
    }

    // Function to load "Now Playing" movies from the API
    private fun loadNowPlayingMovies() {
        binding.progressBarNowPlaying.visibility = View.VISIBLE
        val nowPlayingCall = ApiClient.movieApiService.getNowPlayingMovies()
        nowPlayingCall.enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                binding.progressBarNowPlaying.visibility = View.GONE
                if (response.isSuccessful) {
                    val nowPlayingMovies = response.body()?.results
                    if (nowPlayingMovies != null) {
                        populateRecyclerView(nowPlayingMovies)
                    }
                } else {
                    Toast.makeText(this@SedangTayangActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                binding.progressBarNowPlaying.visibility = View.GONE
                Toast.makeText(this@SedangTayangActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadComingSoonMovies() {
        binding.progressBarNowPlaying.visibility = View.VISIBLE
        val comingSoonCall = ApiClient.movieApiService.getUpcomingMovies()
        comingSoonCall.enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                binding.progressBarNowPlaying.visibility = View.GONE
                if (response.isSuccessful) {
                    val comingSoonMovies = response.body()?.results
                    if (comingSoonMovies != null) {
                        populateRecyclerView(comingSoonMovies)
                    }
                } else {
                    Toast.makeText(this@SedangTayangActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                binding.progressBarNowPlaying.visibility = View.GONE
                Toast.makeText(this@SedangTayangActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to populate the RecyclerView
    private fun populateRecyclerView(movies: List<Movie>) {
        movieItemRecyclerAdapter.updateMovies(movies)
    }
}

