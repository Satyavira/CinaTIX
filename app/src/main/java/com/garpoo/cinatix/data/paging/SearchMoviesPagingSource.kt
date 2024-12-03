package com.garpoo.cinatix.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.MovieApiService
import retrofit2.HttpException
import java.io.IOException

class SearchMoviesPagingSource(
    private val apiService: MovieApiService,
    private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            // Call the API with the search query and page number
            val response = apiService.searchMoviesForPager(query = query, page = page)
            val movieList = response.results
            Log.d("SearchMovies", "Movies: $movieList")

            // Determine next and previous keys for pagination
            val nextKey = if (response.page < response.total_pages) response.page + 1 else null
            LoadResult.Page(
                data = movieList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            // Handle network errors
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // Handle HTTP errors
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Return the key for the closest page to the anchor position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
