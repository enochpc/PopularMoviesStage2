package com.example.popularmoviesstage2.Database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage2.Models.Movie;
import com.example.popularmoviesstage2.Utils.AppExecutors;
import android.util.Log;
import java.util.List;
import java.util.concurrent.Executor;


public class MovieRepository {

    private static final String LOG_TAG = MovieDatabase.class.getSimpleName();
    private MovieDao movieDao;
    private AppExecutors appExecutors;

    private LiveData<List<Movie>> movies;

    public MovieRepository(Application application){
        movieDao = MovieDatabase.getInstance(application).movieDao();

        movies = movieDao.loadAllFavoriteMovies();

        appExecutors = AppExecutors.getExecutorInstance();

    }

    public LiveData<List<Movie>> loadAllFavoriteMovies() { return movies; }

    public boolean isFavorite(int movieId){ return movieDao.isFavorite(movieId); }

    public void updateFavoriteMovie(int movieId, boolean isFavorite){
        appExecutors.getDiskIO().execute(() -> {
            movieDao.updateFavoriteMovie(movieId, isFavorite);
        });

    }

    public void addMovieToFavorites(final Movie movie){
        appExecutors.getDiskIO().execute(() -> movieDao.insertFavoriteMovie(movie));
    }

    public void deleteFavoriteMovie(Movie movie){
        appExecutors.getDiskIO().execute(() -> movieDao.deleteFavoriteMovie(movie));
    }
}
