package com.example.mymovie.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mymovie.model.Movie;

@Database(entities = Movie.class, version = 1)
public abstract class MoviesDB extends RoomDatabase {

    public abstract MovieDao moviesDao();

    public static final String DATABASE_NAME = "moviesDb";
    private static MoviesDB instance;

    public static MoviesDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, MoviesDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void closeInstance(){

        if(instance != null){
            instance.getOpenHelper().close();
        }
    }
}
