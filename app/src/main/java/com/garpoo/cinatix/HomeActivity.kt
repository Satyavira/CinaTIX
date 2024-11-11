package com.garpoo.cinatix

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.garpoo.cinatix.adapter.MoviePagerAdapter
import com.garpoo.cinatix.adapter.MovieRecyclerAdapter
import com.garpoo.cinatix.databinding.ActivityHomeBinding
import com.garpoo.cinatix.model.Movie
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerIndicator: LinearLayout
    private lateinit var adapter: MoviePagerAdapter
    private lateinit var recyclerViewComingSoon: RecyclerView
    private lateinit var movieRecyclerAdapter: MovieRecyclerAdapter
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
            Movie(
                adult = false,
                backdrop_path = "/gMQibswELoKmB60imE7WFMlCuqY.jpg",
                genre_ids = listOf(27, 53, 9648),
                id = 1034541,
                original_language = "en",
                original_title = "Terrifier 3",
                overview = "Five years after surviving Art the Clown's Halloween massacre, Sienna and Jonathan are still struggling to rebuild their shattered lives. As the holiday season approaches, they try to embrace the Christmas spirit and leave the horrors of the past behind. But just when they think they're safe, Art returns, determined to turn their holiday cheer into a new nightmare. The festive season quickly unravels as Art unleashes his twisted brand of terror, proving that no holiday is safe.",
                popularity = 6883.159,
                poster_path = "/63xYQj1BwRFielxsBDXvHIJyXVm.jpg",
                release_date = "2024-10-09",
                title = "Terrifier 3",
                video = false,
                vote_average = 7.3,
                vote_count = 585
            ),
            Movie(
                adult = false,
                backdrop_path = "/3V4kLQg0kSqPLctI5ziYWabAZYF.jpg",
                genre_ids = listOf(878, 28, 12),
                id = 912649,
                original_language = "en",
                original_title = "Venom: The Last Dance",
                overview = "Eddie and Venom are on the run. Hunted by both of their worlds and with the net closing in, the duo are forced into a devastating decision that will bring the curtains down on Venom and Eddie's last dance.",
                popularity = 5590.757,
                poster_path = "/k42Owka8v91trK1qMYwCQCNwJKr.jpg",
                release_date = "2024-10-22",
                title = "Venom: The Last Dance",
                video = false,
                vote_average = 6.7,
                vote_count = 467
            ),
            Movie(
                adult = false,
                backdrop_path = "/4zlOPT9CrtIX05bBIkYxNZsm5zN.jpg",
                genre_ids = listOf(16, 878, 10751),
                id = 1184918,
                original_language = "en",
                original_title = "The Wild Robot",
                overview = "After a shipwreck, an intelligent robot called Roz is stranded on an uninhabited island. To survive the harsh environment, Roz bonds with the island's animals and cares for an orphaned baby goose.",
                popularity = 4321.421,
                poster_path = "/wTnV3PCVW5O92JMrFvvrRcV39RU.jpg",
                release_date = "2024-09-12",
                title = "The Wild Robot",
                video = false,
                vote_average = 8.5,
                vote_count = 2365
            ),
            Movie(
                adult = false,
                backdrop_path = "/oPUOpnl3pqD8wuidjfUn17mO1yA.jpg",
                genre_ids = listOf(16, 878, 12, 10751),
                id = 698687,
                original_language = "en",
                original_title = "Transformers One",
                overview = "The untold origin story of Optimus Prime and Megatron, better known as sworn enemies, but once were friends bonded like brothers who changed the fate of Cybertron forever.",
                popularity = 2550.704,
                poster_path = "/iHPIBzrjJHbXeY9y7VVbEVNt7LW.jpg",
                release_date = "2024-09-11",
                title = "Transformers One",
                video = false,
                vote_average = 8.169,
                vote_count = 528
            ),
            Movie(
                adult = false,
                backdrop_path = "/9oYdz5gDoIl8h67e3ccv3OHtmm2.jpg",
                genre_ids = listOf(18, 27, 878),
                id = 933260,
                original_language = "en",
                original_title = "The Substance",
                overview = "Have you ever dreamt of a better version of yourself? You, only better in every way. You should try this new product, it's called The Substance. IT CHANGED MY LIFE.",
                popularity = 2881.789,
                poster_path = "/lqoMzCcZYEFK729d6qzt349fB4o.jpg",
                release_date = "2024-09-07",
                title = "The Substance",
                video = false,
                vote_average = 7.3,
                vote_count = 1359
            )
        )
        viewPager2 = binding.viewPager2
        viewPagerIndicator = binding.viewPagerIndicator
        recyclerViewComingSoon = binding.recyclerViewComingSoon
        movieRecyclerAdapter = MovieRecyclerAdapter(movies)
        setupViewPager(movies)
        setupIndicators(movies.size)
        setCurrentIndicator(0)
        recyclerViewComingSoon.adapter = movieRecyclerAdapter
        recyclerViewComingSoon.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        // Firebase code is commented out for now
        /*
        database = FirebaseDatabase.getInstance()
        initBanner()
        */
    }

    private fun setupViewPager(movies: MutableList<Movie>) {
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
