package com.garpoo.cinatix.model

data class UpcomingMoviesResponse(
    val dates: Dates,
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)

data class Dates(
    val maximum: String,
    val minimum: String
)

data class Movie(
    val adult: Boolean,
    val backdrop_path: String?,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String?,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class CreditsResponse(
    val id: Int,
    val cast: List<CastMember>
)

data class CastMember(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val known_for_department: String,
    val name: String,
    val original_name: String,
    val popularity: Double,
    val profile_path: String?,
    val cast_id: Int,
    val character: String,
    val credit_id: String,
    val order: Int
)

data class MovieDetailsResponse(
    val adult: Boolean,
    val backdrop_path: String?,
    val belongs_to_collection: BelongsToCollection?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Int,
    val imdb_id: String?,
    val origin_country: List<String>,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String?,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: String,
    val revenue: Long,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class BelongsToCollection(
    val id: Int,
    val name: String,
    val poster_path: String?,
    val backdrop_path: String?
)


data class GenreList(
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

data class SpokenLanguage(
    val english_name: String,
    val iso_639_1: String,
    val name: String
)

data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

data class Video(
    val iso_639_1: String,
    val iso_3166_1: String,
    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,
    val published_at: String,
    val id: String
)

val tmdb = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNjQ4MmM2ZWE5ZTlkOWFlYzY5NjhiOTVkZDZlNDBkNSIsIm5iZiI6MTczMTg1ODkwNC4zNDczOTMsInN1YiI6IjY3MjlkOGE4MDZkYzg4NTk2MzI0MDJhZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.URW9VWtRtPPxWgaHRKFRPKJIfHJNw7SWNoOeFXQTu8E"

fun getGenreList(): GenreList {
    return GenreList(
        genres = listOf(
            Genre(id = 28, name = "Action"),
            Genre(id = 12, name = "Adventure"),
            Genre(id = 16, name = "Animation"),
            Genre(id = 35, name = "Comedy"),
            Genre(id = 80, name = "Crime"),
            Genre(id = 99, name = "Documentary"),
            Genre(id = 18, name = "Drama"),
            Genre(id = 10751, name = "Family"),
            Genre(id = 14, name = "Fantasy"),
            Genre(id = 36, name = "History"),
            Genre(id = 27, name = "Horror"),
            Genre(id = 10402, name = "Music"),
            Genre(id = 9648, name = "Mystery"),
            Genre(id = 10749, name = "Romance"),
            Genre(id = 878, name = "Science Fiction"),
            Genre(id = 10770, name = "TV Movie"),
            Genre(id = 53, name = "Thriller"),
            Genre(id = 10752, name = "War"),
            Genre(id = 37, name = "Western")
        )
    )
}

fun getGenreNameById(genreId: Int): String {
    val genre = getGenreList().genres.find { it.id == genreId }
    return genre?.name ?: "Unknown" // Return "Unknown" if no match is found
}