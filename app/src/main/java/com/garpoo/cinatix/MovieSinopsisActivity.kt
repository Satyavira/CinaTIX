package com.garpoo.cinatix

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.adapter.CastAdapter
import com.garpoo.cinatix.databinding.ActivityMovieSinopsisBinding
import com.garpoo.cinatix.model.CreditsResponse
import com.garpoo.cinatix.model.MovieDetailsResponse
import com.garpoo.cinatix.network.ApiClient
import com.garpoo.cinatix.network.MovieApiService
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MovieSinopsisActivity : AppCompatActivity() {

    private lateinit var movieApiService: MovieApiService // Retrofit API service
    private var movieId: Int = -1 // The movie ID passed from the previous activity
    private lateinit var binding: ActivityMovieSinopsisBinding // View binding for your layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieSinopsisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the movieId passed from the previous activity
        movieId = intent.getIntExtra("movieId", -1)
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish() // Close activity if invalid ID
            return
        }

        binding.tabLayout.apply {
            addTab(newTab().setText("SINOPSIS"))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // Initialize Retrofit
        movieApiService = ApiClient.movieApiService

        // Set up the UI
        setupUI()

        // Fetch movie details and credits
        fetchMovieDetails(movieId)
    }

    private fun setupUI() {
        // Set up back button click listener
        binding.backIcon.setOnClickListener {
            onBackPressed() // Go back to the previous activity
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        // Fetch movie details from the API
        val movieDetailsCall = movieApiService.getMovieDetails(movieId)
        movieDetailsCall.enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(
                call: Call<MovieDetailsResponse>,
                response: Response<MovieDetailsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val movieDetails = response.body()!!
                    updateMovieDetailsUI(movieDetails)
                } else {
                    Toast.makeText(this@MovieSinopsisActivity, "Failed to load movie details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                Toast.makeText(this@MovieSinopsisActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch movie credits (cast) from the API
        val creditsCall = movieApiService.getMovieCredits(movieId)
        creditsCall.enqueue(object : Callback<CreditsResponse> {
            override fun onResponse(
                call: Call<CreditsResponse>,
                response: Response<CreditsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val credits = response.body()!!
                    updateMovieCreditsUI(credits)
                } else {
                    Toast.makeText(this@MovieSinopsisActivity, "Failed to load movie credits", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                Toast.makeText(this@MovieSinopsisActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateMovieDetailsUI(movieDetails: MovieDetailsResponse) {
        // Update movie title
        binding.movieTitle.text = movieDetails.title

        // Set movie image (backdrop)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movieDetails.backdrop_path}")
            .apply(RequestOptions().centerCrop())
            .into(binding.movieImage)

        // Update the description and other movie details
        binding.synopsisText.text = movieDetails.overview

        // Update genres, year, duration, etc.
        val genreNames = movieDetails.genres.joinToString(", ") { it.name }
        val year = movieDetails.release_date.split("-")[0] // Extract year
        val runtime = movieDetails.runtime
        binding.deskripsiFilm.text = getString(R.string.deskripsiFilm, year, genreNames, runtime)

        // You can update other UI elements like IMDb, budget, revenue, etc. here
    }

    private fun updateMovieCreditsUI(credits: CreditsResponse) {
        // Populate the RecyclerView with cast information
        val castAdapter = CastAdapter(credits.cast)
        binding.castRecyclerView.adapter = castAdapter
        val producer = credits.crew.filter {
            crew ->
            crew.job == "Producer"
        }
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${producer[0].profile_path}")
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60))) // Center crop with rounded corners
            .into(binding.imgProducer)
        binding.namaProducer.text = producer[0].job + "\n" + producer[0].name
    }
}
