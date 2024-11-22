package com.garpoo.cinatix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.garpoo.cinatix.data.api.MovieApiService

class UpcomingMoviesViewModelFactory(
    private val apiService: MovieApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingMoviesViewModel::class.java)) {
            return UpcomingMoviesViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
