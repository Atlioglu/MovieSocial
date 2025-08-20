package com.example.moviesocial.service

import com.example.moviesocial.model.Movie
import com.example.moviesocial.model.MovieDetails
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieAPI {

    @GET("movie/now_playing")
    @Headers("Authorization: ${ApiConstants.AUTH_TOKEN}")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Movie

    @GET("trending/movie/day")
    @Headers("Authorization: ${ApiConstants.AUTH_TOKEN}")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 1
    ): Movie


    @GET("search/movie")
    @Headers("Authorization: ${ApiConstants.AUTH_TOKEN}")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Movie

    @GET("movie/{movie_id}")
    @Headers("Authorization: ${ApiConstants.AUTH_TOKEN}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetails


    @GET("movie/latest")
    @Headers("Authorization: ${ApiConstants.AUTH_TOKEN}")
    suspend fun getLatestMovie(
        @Query("language") language: String = "en-US"
    ): MovieDetails

}

object ApiConstants {
    const val AUTH_TOKEN =  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkNjFlNjcyNDk0ZTQ0NjM1ZTM1ODY1YzMyOWUxYmQzNiIsIm5iZiI6MTc1MzQ0Mzk4OS4xNjcsInN1YiI6IjY4ODM2ZTk1OTBjNzM3ZGFkNzE2YjkxZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Ge8oo8CGQPxlcpH_KHhaJMU5jh9R3mbFj3-eYWhptpU"

}