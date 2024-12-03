package com.garpoo.cinatix.data.api

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
    @GET("movie/upcoming")
    suspend fun getUpcomingMoviesForPager(
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "ID",
        @Query("with_release_type") withReleaseType: String = "1|2|3",
        @Query("page") page: Int = 1
    ): UpcomingMoviesResponse
    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "ID",
        @Query("page") page: Int = 1
    ): Call<UpcomingMoviesResponse>
    @GET("movie/now_playing")
    suspend fun getNowPlayingMoviesForPager(
        @Header("Authorization") authorization: String = tmdb,
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "ID",
        @Query("page") page: Int = 1
    ): UpcomingMoviesResponse
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
    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") authorization: String = tmdb,
    ): Call<UpcomingMoviesResponse>
    @GET("search/movie")
    suspend fun searchMoviesForPager(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("accept") accept: String = "application/json",
        @Header("Authorization") authorization: String = tmdb,
    ): UpcomingMoviesResponse
}