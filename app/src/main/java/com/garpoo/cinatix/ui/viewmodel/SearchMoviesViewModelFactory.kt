package com.garpoo.cinatix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.garpoo.cinatix.data.api.MovieApiService

class SearchMoviesViewModelFactory(
    private val apiService: MovieApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchMoviesViewModel::class.java)) {
            return SearchMoviesViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
