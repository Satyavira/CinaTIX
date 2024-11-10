package com.garpoo.cinatix.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.garpoo.cinatix.R
import com.garpoo.cinatix.databinding.ItemMovieCardBinding
import com.garpoo.cinatix.model.Movie
import com.garpoo.cinatix.model.getGenreNameById
import java.text.DecimalFormat
import kotlin.math.nextUp

class MovieRecyclerAdapter(
    private var movies: MutableList<Movie>
) : RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder>() {

    private var context: Context? = null

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        movies.addAll(movies)
        notifyDataSetChanged()
    }

    // ViewHolder class with ViewBinding
    inner class MovieViewHolder(private val binding: ItemMovieCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind method to load data into views
        fun bind(movie: Movie) {
            context?.let {
                // Load movie poster using Glide with rounded corners
                Glide.with(it)
                    .load("https://image.tmdb.org/t/p/w500".plus(movie.poster_path))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60))) // Center crop with rounded corners
                    .into(binding.moviePosterImage)
            }

            // Set text data
            binding.movieTitle.text = movie.title
            val firstGenreId = movie.genre_ids.firstOrNull() ?: 0
            val genreName = getGenreNameById(firstGenreId)
            binding.movieGenre.text = genreName
            val rating = (movie.vote_average / 2).nextUp()
            binding.ratingText.text = context?.getString(
                R.string.of_5,
                DecimalFormat("#.##").format(rating)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        val binding = ItemMovieCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}