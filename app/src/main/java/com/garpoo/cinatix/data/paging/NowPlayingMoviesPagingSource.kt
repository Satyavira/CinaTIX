package com.garpoo.cinatix.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.garpoo.cinatix.data.api.Movie
import com.garpoo.cinatix.data.api.MovieApiService
import retrofit2.HttpException
import java.io.IOException

class NowPlayingMoviesPagingSource(
    private val apiService: MovieApiService
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getNowPlayingMoviesForPager(page = page)
            val movieList = response.results
            Log.d("nowplaying", "$movieList")
            val nextKey = if (response.page < response.total_pages) response.page + 1 else null
            LoadResult.Page(
                data = movieList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }
}
