package com.example.popularmoviesstage2.Database;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;

import com.example.popularmoviesstage2.Models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY vote_average DESC")
    LiveData<List<Movie>> loadAllFavoriteMovies();

    @Query("SELECT is_favorite FROM movie WHERE movie_id = :movieId")
    boolean isFavorite(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteMovie(Movie movie);

    @Query("UPDATE movie SET is_favorite = :isFavorite WHERE movie_id = :movieId")
    void updateFavoriteMovie(int movieId, boolean isFavorite);

    @Delete
    void deleteFavoriteMovie(Movie movie);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    Movie getMovie(int movieId);

}
