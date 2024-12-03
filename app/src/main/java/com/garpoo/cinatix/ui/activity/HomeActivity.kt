package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.garpoo.cinatix.R
import com.garpoo.cinatix.adapter.MovieRecyclerAdapter
import com.garpoo.cinatix.data.api.ApiClient
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.UpcomingMoviesResponse
import com.garpoo.cinatix.databinding.ActivityHomeBinding
import com.garpoo.cinatix.ui.adapter.MoviePagerAdapter
import com.garpoo.cinatix.utils.BottomNavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerIndicator: LinearLayout
    private lateinit var adapter: MoviePagerAdapter
    private lateinit var recyclerViewComingSoon: RecyclerView
    private lateinit var movieRecyclerAdapter: MovieRecyclerAdapter
    private lateinit var sliderHandler: Handler
    private lateinit var firebaseAuth: FirebaseAuth
    private val sliderRunnable = Runnable {
        binding.viewPager2.currentItem += 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvWelcome.text = firebaseAuth.currentUser?.displayName ?: "Anonymous"

        sliderHandler = Handler(Looper.getMainLooper())

        viewPager2 = binding.viewPager2
        viewPagerIndicator = binding.viewPagerIndicator
        recyclerViewComingSoon = binding.recyclerViewComingSoon

        val nowPlayingCall = ApiClient.movieApiService.getNowPlayingMovies()
        nowPlayingCall.enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val upcomingMovies = response.body()?.results?.subList(0, 8)
                    runOnUiThread {
                        binding.progressBarNowPlaying.visibility = View.GONE
                        if (upcomingMovies != null) {
                            setupViewPager(upcomingMovies)
                            setupIndicators(upcomingMovies.size)
                            setCurrentIndicator(0)
                            binding.tvNowPlayingSeeAll.setOnClickListener {
                                val intent = Intent(this@HomeActivity, SedangTayangActivity::class.java)
                                intent.putExtra("SELECTED_TAB", 0)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Error: ${response.code()}, ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@HomeActivity, "Failure: ${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
            }
        })

        val upcomingCall = ApiClient.movieApiService.getUpcomingMovies()
        upcomingCall.enqueue(object : Callback<UpcomingMoviesResponse> {
            override fun onResponse(
                call: Call<UpcomingMoviesResponse>,
                response: Response<UpcomingMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val upcomingMovies = response.body()?.results
                    runOnUiThread {
                        binding.progressBarComingSoon.visibility = View.GONE
                        if (upcomingMovies != null) {
                            movieRecyclerAdapter = MovieRecyclerAdapter(upcomingMovies) { movie -> movieToSipnosis(movie)}
                            recyclerViewComingSoon.adapter = movieRecyclerAdapter
                            recyclerViewComingSoon.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                            binding.tvComingSoonSeeAll.setOnClickListener {
                                val intent = Intent(this@HomeActivity, SedangTayangActivity::class.java)
                                intent.putExtra("SELECTED_TAB", 1)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Error: ${response.code()}, ${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpcomingMoviesResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@HomeActivity, "Failure: ${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_home
        BottomNavigationHandler.handleNavigation(this@HomeActivity,binding.bottomNavigation)

    }

    override fun onRestart() {
        super.onRestart()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun setupViewPager(movies: List<Movie>?) {
        adapter = movies?.let { MoviePagerAdapter(it) { movie -> movieToSipnosis(movie)} }!!
        viewPager2.adapter = adapter

        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // Custom PageTransformer for rotation and translation effect
        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(60))
            addTransformer { page, position ->
                val absPos = abs(position)
                page.scaleY = 0.85f + (1 - absPos) * 0.15f
                page.alpha = 0.5f + (1 - absPos) * 0.5f
                page.rotation = if (position < 0) {
                    position * 20f
                } else {
                    position * 20f
                }
                page.translationY = if (position < 0) {
                    page.height * - position * 1 / 10
                } else {
                    page.height * position * 1 / 10
                }
            }
        }

        viewPager2.setPageTransformer(compositePageTransformer)

        binding.viewPager2.currentItem = 0

        // Auto-scroll with delay
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.setCurrentPosition(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                // Reset to start if we reach the last item for infinite effect
                if (position == adapter.itemCount - 1) {
                    binding.viewPager2.postDelayed({
                        binding.viewPager2.setCurrentItem(0, true) // Smooth transition back to start
                    }, 4000) // Adjust delay as needed
                } else {
                    sliderHandler.postDelayed(sliderRunnable, 4000)
                }
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(count: Int) {
        val indicators = Array(count) { ImageView(this) }
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0) // Space between dots

        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i].setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.indicator_dot_unselected)
            )
            indicators[i].layoutParams = layoutParams
            viewPagerIndicator.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = viewPagerIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = viewPagerIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_dot_selected)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_dot_unselected)
                )
            }
        }
    }

    private fun movieToSipnosis(movieId: Int) {
        val intent = Intent(this, MovieSinopsisActivity::class.java)
        intent.putExtra("movieId", movieId)
        startActivity(intent)
    }

    // Remove callbacks on pause
    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    // Resume auto-scroll on resume
    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 2000)
    }

}
