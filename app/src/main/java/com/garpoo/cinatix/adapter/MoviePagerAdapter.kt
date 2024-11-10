package com.garpoo.cinatix.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.databinding.ItemMoviePosterBinding
import com.garpoo.cinatix.model.Movie
import com.garpoo.cinatix.model.getGenreNameById

class MoviePagerAdapter(
    private var movies: MutableList<Movie>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<MoviePagerAdapter.MovieViewHolder>() {

    private var context: Context? = null

    // Runnable to duplicate items for infinite scrolling
    private val runnable = Runnable {
        movies.addAll(movies) // Duplicate items for infinite scrolling
        notifyDataSetChanged() // Refresh adapter to display new items
    }

    // ViewHolder class with ViewBinding for `item_movie_poster`
    inner class MovieViewHolder(private val binding: ItemMoviePosterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind method to load data into views
        fun bind(movie: Movie) {
            context?.let {
                Glide.with(it)
                    .load("https://image.tmdb.org/t/p/w500".plus(movie.poster_path))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60))) // Center crop with rounded corners
                    .into(binding.moviePosterImage) // Load image into ImageView
            }

            // Set text data
            binding.movieTitle.text = movie.title
            val firstGenreId = movie.genre_ids.firstOrNull() ?: 0
            val genreName = getGenreNameById(firstGenreId)
            binding.movieGenre.text = genreName

            // Show/hide movie information based on the current position
            if (adapterPosition == currentPosition) {
                binding.movieInfoLayout.visibility = View.VISIBLE
            } else {
                binding.movieInfoLayout.visibility = View.INVISIBLE
            }
        }
    }

    // Track the current position for infinite scrolling effect
    private var currentPosition = 0

    fun setCurrentPosition(position: Int) {
        currentPosition = position
        Handler(Looper.getMainLooper()).post {
            notifyItemChanged(position - 1)
            notifyItemChanged(position)
            notifyItemChanged(position + 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        val binding = ItemMoviePosterBinding.inflate(LayoutInflater.from(context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])

        // Trigger the runnable for infinite scrolling when reaching the second-last item
//        if (position == movies.size - 2) {
//            viewPager2.post(runnable)
//        }
    }

    override fun getItemCount(): Int = movies.size
}
