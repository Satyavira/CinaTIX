package com.garpoo.cinatix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.MovieApiService
import com.garpoo.cinatix.data.paging.SearchMoviesPagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchMoviesViewModel(private val apiService: MovieApiService) : ViewModel() {
    private val _movies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val movies: StateFlow<PagingData<Movie>> = _movies

    fun searchMovies(query: String) {
        val pager = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchMoviesPagingSource(apiService, query) } // Using the PagingSource
        )
        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _movies.emit(it) // Emitting the result
            }
        }
    }
}