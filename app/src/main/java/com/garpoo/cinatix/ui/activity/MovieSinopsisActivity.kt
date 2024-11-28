package com.garpoo.cinatix.ui.activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.R
import com.garpoo.cinatix.ui.adapter.CastAdapter
import com.garpoo.cinatix.databinding.ActivityMovieSinopsisBinding
import com.garpoo.cinatix.data.api.CreditsResponse
import com.garpoo.cinatix.data.api.MovieDetailsWithCreditsAndVideosResponse
import com.garpoo.cinatix.data.api.ApiClient
import com.garpoo.cinatix.data.api.MovieApiService
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieSinopsisActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var movieApiService: MovieApiService // Retrofit API service
    private var movieId: Int = -1 // The movie ID passed from the previous activity
    private lateinit var binding: ActivityMovieSinopsisBinding // View binding for your layout
    private lateinit var movieDatas: MovieDetailsWithCreditsAndVideosResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMovieSinopsisBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

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

        movieApiService = ApiClient.movieApiService

        // Set up the UI
        setupUI()

        // Fetch movie details and credits
        fetchMovieDetails(movieId)

        checkIsFavorite()
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setupUI() {
        // Set up back button click listener
        binding.backIcon.setOnClickListener {
            onBackPressed() // Go back to the previous activity
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        // Fetch movie details from the API
        val movieDetailsCall = movieApiService.getMovieDetailsWithCreditsAndVideos(movieId)
        movieDetailsCall.enqueue(object : Callback<MovieDetailsWithCreditsAndVideosResponse> {
            override fun onResponse(
                call: Call<MovieDetailsWithCreditsAndVideosResponse>,
                response: Response<MovieDetailsWithCreditsAndVideosResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val movieDetails = response.body()!!
                    movieDatas = movieDetails
                    updateMovieDetailsUI(movieDetails)
                } else {
                    Toast.makeText(this@MovieSinopsisActivity, "Failed to load movie details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetailsWithCreditsAndVideosResponse>, t: Throwable) {
                Toast.makeText(this@MovieSinopsisActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch movie credits (cast) from the API
//        val creditsCall = movieApiService.getMovieCredits(movieId)
//        creditsCall.enqueue(object : Callback<CreditsResponse> {
//            override fun onResponse(
//                call: Call<CreditsResponse>,
//                response: Response<CreditsResponse>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    val credits = response.body()!!
//                    updateMovieCreditsUI(credits)
//                } else {
//                    Toast.makeText(this@MovieSinopsisActivity, "Failed to load movie credits", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
//                Toast.makeText(this@MovieSinopsisActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    private fun updateMovieDetailsUI(movieDetailsWithCreditsAndVideos: MovieDetailsWithCreditsAndVideosResponse) {
        // Update movie title
        binding.movieTitle.text = movieDetailsWithCreditsAndVideos.title

        // Set movie image (backdrop)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w1280${movieDetailsWithCreditsAndVideos.backdrop_path}")
            .apply(RequestOptions().centerCrop())
            .into(binding.movieImage)

        // Update the description and other movie details
        binding.synopsisText.text = movieDetailsWithCreditsAndVideos.overview

        // Update genres, year, duration, etc.
        val genreNames = movieDetailsWithCreditsAndVideos.genres.joinToString(", ") { it.name }
        val year = movieDetailsWithCreditsAndVideos.release_date.split("-")[0] // Extract year
        val runtime = movieDetailsWithCreditsAndVideos.runtime
        binding.deskripsiFilm.text = getString(R.string.deskripsiFilm, year, genreNames, runtime)

        updateMovieCreditsUI(movieDetailsWithCreditsAndVideos.credits)
        // You can update other UI elements like IMDb, budget, revenue, etc. here
    }

    private var isFavorite = false

    private fun toggleFavorite() {
        binding.btnFavorite.isEnabled = false
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "You need to log in to add favorites", Toast.LENGTH_SHORT).show()
            return
        }

        if (isFavorite) {
            removeFromFavorite(userId)
        } else {
            addToFavorite(userId)
        }
        binding.btnFavorite.isEnabled = true
    }

    private fun addToFavorite(userId: String) {
        val timestamp = System.currentTimeMillis()
        var movieImage = "https://image.tmdb.org/t/p/w500${movieDatas.poster_path}/"
        val favoriteMap = hashMapOf(
            "movieId" to movieId.toString(),
            "userId" to userId,
            "isFavorite" to true,  // Default value when adding a favorite
            "timestamp" to timestamp,
            "movieTitle" to movieDatas.title,
            "genre" to movieDatas.genres[0].name,
            "movieImage" to movieImage,
            "rating" to movieDatas.vote_average,
        )

        val documentId = "$userId-$movieId"
        val favoriteRef = FirebaseFirestore.getInstance().collection("Favorites").document(documentId)

        favoriteRef.set(favoriteMap)
            .addOnSuccessListener {
                isFavorite = true
                updateFavoriteButton()
                Toast.makeText(this, "Movie successfully added to favorites", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Favorite added")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding favorite", e)
            }
    }

    private fun removeFromFavorite(userId: String) {
        val documentId = "$userId-$movieId"
        val favoriteRef = FirebaseFirestore.getInstance().collection("Favorites").document(documentId)

        favoriteRef.delete()
            .addOnSuccessListener {
                isFavorite = false
                updateFavoriteButton()
                Toast.makeText(this, "Movie successfully removed from favorites", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Favorite removed")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting favorite", e)
            }
    }

    private fun checkIsFavorite() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val favoriteRef = FirebaseFirestore.getInstance().collection("Favorites")
            favoriteRef.whereEqualTo("userId", userId).whereEqualTo("movieId", movieId.toString())
                .get()
                .addOnSuccessListener { snapshot ->
                    isFavorite = !snapshot.isEmpty
                    updateFavoriteButton()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            isFavorite = false
            updateFavoriteButton()
        }
    }


    private fun updateFavoriteButton() {
        val icon = R.drawable.ic_favorite // Ikon tombol favorit
        binding.btnFavorite.setImageResource(icon)

        val color = if (isFavorite) {
            ContextCompat.getColor(this, R.color.red)
        } else {
            ContextCompat.getColor(this, R.color.gray)
        }
        binding.btnFavorite.setColorFilter(color)
    }



    private fun updateMovieCreditsUI(credits: CreditsResponse) {
        // Populate the RecyclerView with cast information
        if (credits.cast.isNotEmpty()) {
            val castAdapter = CastAdapter(credits.cast)
            binding.castRecyclerView.adapter = castAdapter
        } else {
            binding.castLabel.visibility = View.GONE
            binding.castRecyclerView.visibility = View.GONE
        }
        val producer = credits.crew.filter {
            crew ->
            crew.job == "Producer"
        }
        if (producer.isNotEmpty()) {
            if (producer.first().profile_path != null) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500${producer[0].profile_path}")
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60))) // Center crop with rounded corners
                    .into(binding.imgProducer)
            } else {
                binding.imgProducer.visibility = View.GONE
            }
            binding.namaProducer.text = buildString {
                append(producer[0].job)
                append("\n")
                append(producer[0].name)
            }
        } else {
            binding.namaProducer.visibility = View.GONE
            binding.imgProducer.visibility = View.GONE
        }
    }
}
