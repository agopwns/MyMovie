package com.example.mymovie.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie implements Comparable<Movie>{

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "type")
    private String movieType;
    @ColumnInfo(name = "genre")
    private String movieGenre;
    @ColumnInfo(name = "title")
    private String movieTitle;
    @ColumnInfo(name = "content")
    private String movieContent;
    @ColumnInfo(name = "date")
    private String movieDate;
    @ColumnInfo(name = "confirmDate")
    private long confirmDate;
    @ColumnInfo(name = "star")
    private int star;
    @ColumnInfo(name = "image")
    private String movieImagePath;

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieType() { return movieType; }

    public void setMovieType(String movieType) { this.movieType = movieType; }

    public String getMovieGenre() { return movieGenre; }

    public void setMovieGenre(String movieGenre) { this.movieGenre = movieGenre; }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieContent() {
        return movieContent;
    }

    public void setMovieContent(String movieContent) {
        this.movieContent = movieContent;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public long getConfirmDate() { return confirmDate; }

    public void setConfirmDate(long confirmDate) {
        this.confirmDate = confirmDate;
    }

    public int getStar() { return star; }

    public void setStar(int star) { this.star = star; }

    public String getMovieImagePath() {
        return movieImagePath;
    }

    public void setMovieImagePath(String movieImagePath) {
        this.movieImagePath = movieImagePath;
    }

    @Override
    public String toString() {
        return "movie{" +
                "id=" + id +
                ", movieTitle=" + movieTitle +
                ", movieDate=" + movieDate +
                ", confirmDate=" + confirmDate +
                ", movieImagePath=" + movieImagePath +
                '}';
    }

    // 가장 최근 글이 위로 오게 정렬(내림차순) 하기 위해
    // Comparable 를 상속 받아 사용하므로 compareTo를 Override 해야 함
    @Override
    public int compareTo(Movie o) {

        if (this.confirmDate < o.getConfirmDate()) {
            return 1;
        } else if (this.confirmDate > o.getConfirmDate()) {
            return -1;
        }
        return 0;
    }
}
