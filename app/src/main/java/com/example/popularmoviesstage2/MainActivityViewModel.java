package com.example.popularmoviesstage2;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import android.util.Log;

import com.example.popularmoviesstage2.Database.MovieRepository;
import com.example.popularmoviesstage2.Models.Movie;

import java.util.List;


public class MainActivityViewModel extends AndroidViewModel{

    private static String TAG = MainActivityViewModel.class.getSimpleName();
    private LiveData<List<Movie>> favMovies;

    public MainActivityViewModel(@NonNull Application application){

        super(application);
        MovieRepository movieRepository = new MovieRepository(application);
        Log.d(TAG,"Retrieving tasks from database via ViewModel");
        favMovies = movieRepository.loadAllFavoriteMovies();

    }

    public LiveData<List<Movie>> getFavoriteMovies(){ return favMovies; }
}
