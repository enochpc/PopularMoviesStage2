package com.example.popularmoviesstage2.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.util.Log;
import com.example.popularmoviesstage2.Models.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String LOG_TAG = MovieDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "TmdbMovies";
    private static MovieDatabase sInstance;

    public static MovieDatabase getInstance(Context context){

        if (sInstance == null){
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating the database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, MovieDatabase.DATABASE_NAME).build();

            }
        }

        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }
    public abstract MovieDao movieDao();
}