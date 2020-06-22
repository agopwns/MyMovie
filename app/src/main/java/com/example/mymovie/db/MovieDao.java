package com.example.mymovie.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mymovie.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertmovie(Movie movie);

    @Delete
    void deletemovie(Movie movie);

    @Update
    void updatemovie(Movie movie);

    @Query("SELECT * FROM movies")
    List<Movie> getmovies();

    @Query("SELECT * FROM movies WHERE id LIKE :movieId") // 수정, 삭제시 필요
    Movie getmovieById(int movieId);

    @Query("DELETE FROM movies WHERE id LIKE :movieId")
    void deletemovieById(int movieId);
}
