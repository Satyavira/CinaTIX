package com.garpoo.cinatix.network

import com.garpoo.cinatix.model.CreditsResponse
import com.garpoo.cinatix.model.MovieDetailsResponse
import com.garpoo.cinatix.model.MovieDetailsWithCreditsAndVideosResponse
import com.garpoo.cinatix.model.UpcomingMoviesResponse
import com.garpoo.cinatix.model.tmdb
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "ID",
        @Query("with_release_type") withReleaseType: String = "1|2|3",
        @Query("page") page: Int = 1
    ): Call<UpcomingMoviesResponse>
    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "ID",
        @Query("page") page: Int = 1
    ): Call<UpcomingMoviesResponse>
    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US"
    ): Call<CreditsResponse>
    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US"
    ): Call<MovieDetailsResponse>
    @GET("movie/{movie_id}")
    fun getMovieDetailsWithCreditsAndVideos(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") authorization: String = tmdb,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("language") language: String = "en-US"
    ): Call<MovieDetailsWithCreditsAndVideosResponse>
}