package com.garpoo.cinatix.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.R
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.getGenreNameById
import com.garpoo.cinatix.databinding.ItemFilmBinding
import java.text.DecimalFormat
import kotlin.math.nextUp

class UpcomingMoviesPagerAdapter(private val movieToSipnosis: (Int) -> Unit) : PagingDataAdapter<Movie, UpcomingMoviesPagerAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        val binding = ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(movie)
        }
    }

    inner class MovieViewHolder(private val binding: ItemFilmBinding) : RecyclerView.ViewHolder(binding.root) {
        // Bind method to load data into views
        fun bind(movie: Movie) {
            binding.root.setOnClickListener {
                movieToSipnosis.invoke(movie.id)
            }
            context?.let {
                // Load movie poster using Glide with rounded corners
                Glide.with(it)
                    .load("https://image.tmdb.org/t/p/w500".plus(movie.poster_path))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60)))
                    .into(binding.moviePoster)
            }

            // Set text data
            binding.movieTitle.text = movie.title
            val firstGenreId = movie.genre_ids.firstOrNull() ?: 0
            val genreName = getGenreNameById(firstGenreId)
            binding.movieGenre.text = genreName
            val rating: Double = if (movie.vote_average != 0.0) {
                (movie.vote_average / 2).nextUp()
            } else {
                0.0
            }
            binding.ratingText.text = context?.getString(
                R.string.of_5,
                DecimalFormat("#.#").format(rating)
            ) ?: "0 of 5"

            // Set star ratings dynamically based on movie rating
            val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
            for (i in stars.indices) {
                if (i < rating) {
                    stars[i].setImageResource(R.drawable.ic_visible)
                } else {
                    stars[i].setImageResource(R.drawable.ic_invisible)
                }
            }
        }
    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
