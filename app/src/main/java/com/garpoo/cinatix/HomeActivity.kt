package com.garpoo.cinatix

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.garpoo.cinatix.adapter.MoviePagerAdapter
import com.garpoo.cinatix.databinding.ActivityHomeBinding
import com.garpoo.cinatix.model.Movie
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: MoviePagerAdapter
    private lateinit var sliderHandler: Handler
    private val sliderRunnable = Runnable {
        binding.viewPager2.currentItem += 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sliderHandler = Handler(Looper.getMainLooper())

        // Hardcoded list of movies for now
        val movies = mutableListOf(
            Movie("https://statik.tempo.co/data/2024/08/08/id_1325911/1325911_720.jpg", "Kuasa Gelap", "Horror"),
            Movie("https://statik.tempo.co/data/2024/08/08/id_1325911/1325911_720.jpg", "My Annoying Brother", "Comedy"),
            Movie("https://statik.tempo.co/data/2024/08/08/id_1325911/1325911_720.jpg", "Home Sweet Loan", "Drama Comedy")
        )
        setupViewPager(movies)

        // Firebase code is commented out for now
        /*
        database = FirebaseDatabase.getInstance()
        initBanner()
        */
    }

    private fun setupViewPager(movies: MutableList<Movie>) {
        viewPager2 = binding.viewPager2
        adapter = MoviePagerAdapter(movies, viewPager2)
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
            }
        })
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

    /*
    private fun initBanner() {
        val myRef: DatabaseReference = database.getReference("Banners")
        binding.progressBarSlider.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderItems>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(SliderItems::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                binding.progressBarSlider.visibility = View.GONE
                setupViewPager(lists)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    */
}
