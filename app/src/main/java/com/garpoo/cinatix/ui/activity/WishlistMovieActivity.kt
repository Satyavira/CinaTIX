package com.garpoo.cinatix.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.garpoo.cinatix.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garpoo.cinatix.adapter.WishlistMovieRecyclerAdapter
import com.garpoo.cinatix.data.api.wishlistMovie
import com.garpoo.cinatix.databinding.ActivityWishlistMovieBinding
import com.garpoo.cinatix.ui.adapter.MovieItemRecyclerAdapter
import com.garpoo.cinatix.utils.BottomNavigationHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WishlistMovieActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityWishlistMovieBinding
    private lateinit var wishlistAdapter: WishlistMovieRecyclerAdapter
    private val wishlistMovies = mutableListOf<wishlistMovie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWishlistMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Load wishlist movies from SharedPreferences
        loadWishlist()

        lifecycleScope.launch {
            fetchFavoriteMovie()
        }

        wishlistAdapter = WishlistMovieRecyclerAdapter(wishlistMovies) { movie ->
            openMovieDetails(movie)
        }

        binding.recyclerViewWishlist.apply {
            layoutManager = LinearLayoutManager(this@WishlistMovieActivity)
            adapter = wishlistAdapter
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_wishlist
        BottomNavigationHandler.handleNavigation(this@WishlistMovieActivity,binding.bottomNavigation)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    override fun onRestart() {
        super.onRestart()
        binding.bottomNavigation.selectedItemId = R.id.nav_wishlist
    }

    private fun loadWishlist() {
        val sharedPreferences = getSharedPreferences("wishlist", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("wishlist_movies", null)
        if (json != null && json.isNotEmpty()) {
            val type = object : TypeToken<List<wishlistMovie>>() {}.type
            wishlistMovies.addAll(Gson().fromJson(json, type))
        }
    }

    private suspend fun fetchFavoriteMovie() {
        try {
            val user = firebaseAuth.currentUser
            if (user!=null){
                val documents = FirebaseFirestore.getInstance()
                    .collection("Favorites")
                    .whereEqualTo("isFavorite", true)
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()

                val favoriteMovies = mutableListOf<wishlistMovie>()

                for (document in documents) {
                    val movieId = document.getString("movieId")?.toIntOrNull()
                    val isFavorite = document.getBoolean("isFavorite") ?: false
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val genre = document.getString("genre") ?: "Unknown"
                    val movieImage = document.getString("movieImage") ?: ""
                    val movieTitle = document.getString("movieTitle") ?: "Unknown"
                    val rating = document.getDouble("rating") ?: 0.0

                    if (movieId != null) {
                        // Tambahkan film ke daftar favorit
                        favoriteMovies.add(
                            wishlistMovie(
                                id = movieId,
                                isFavorite = true,
                                genre = genre,
                                poster_path = movieImage,
                                title = movieTitle,
                                rating = rating,
                            )
                        )
                    }
                }

                withContext(Dispatchers.Main) {
                    wishlistMovies.clear()
                    wishlistMovies.addAll(favoriteMovies)
                    wishlistAdapter.notifyDataSetChanged()
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@WishlistMovieActivity,
                    "Error fetching bookmarks: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveWishlist() {
        val sharedPreferences = getSharedPreferences("wishlist", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(wishlistMovies)
        editor.putString("wishlist_movies", json)
        editor.apply()
    }

    private fun openMovieDetails(movie: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movie)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        saveWishlist()
    }
}