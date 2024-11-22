package com.garpoo.cinatix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.MovieApiService
import com.garpoo.cinatix.data.paging.NowPlayingMoviesPagingSource
import com.garpoo.cinatix.data.paging.UpcomingMoviesPagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpcomingMoviesViewModel(private val apiService: MovieApiService) : ViewModel() {
    private val _nowPlayingMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val nowPlayingMovies: StateFlow<PagingData<Movie>> = _nowPlayingMovies

    private val _upcomingMovies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val upcomingMovies: StateFlow<PagingData<Movie>> = _upcomingMovies

    // Function to load 'Now Playing' movies using PagingSource
    fun loadNowPlayingMovies() {
        val pager = Pager(
            config = PagingConfig(pageSize = 17, enablePlaceholders = false),
            pagingSourceFactory = { NowPlayingMoviesPagingSource(apiService) } // Using the PagingSource
        )

        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _nowPlayingMovies.emit(it) // Emitting the result
            }
        }
    }

    // Function to load 'Upcoming' movies using PagingSource
    fun loadUpcomingMovies() {
        val pager = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { UpcomingMoviesPagingSource(apiService) } // Using the PagingSource
        )

        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _upcomingMovies.emit(it) // Emitting the result
            }
        }
    }
}