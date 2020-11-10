package com.example.popularmoviesstage2;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import com.example.popularmoviesstage2.Models.Movie;
import com.example.popularmoviesstage2.Database.MovieRepository;


public class DetailsActivityViewModel extends AndroidViewModel{

    private MovieRepository movieRepository;

    public DetailsActivityViewModel(@NonNull Application application){
        super(application);
        movieRepository = new MovieRepository(application);
    }

    public boolean isFavorite(int movieId){ return movieRepository.isFavorite(movieId); }

    public void addMovieToFavorites(Movie movie){ movieRepository.addMovieToFavorites(movie);}

    public void removeMovieFromFavorites(Movie movie){ movieRepository.deleteFavoriteMovie(movie);}

    public void updateFavoriteMovie(int movieId, boolean isFavorite) {
        movieRepository.updateFavoriteMovie(movieId, isFavorite);
    }
}
