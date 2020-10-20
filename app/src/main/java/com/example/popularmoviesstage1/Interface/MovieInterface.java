package com.example.popularmoviesstage1.Interface;

import com.example.popularmoviesstage1.Models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//interface used to switch sort between toprated and popular
public interface MovieInterface {

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page);

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                         @Query("page") int page);
}

