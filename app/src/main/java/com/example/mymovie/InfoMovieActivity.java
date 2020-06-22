package com.example.mymovie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.example.mymovie.db.MovieDao;
import com.example.mymovie.db.MoviesDB;
import com.example.mymovie.model.Movie;

public class InfoMovieActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textType, textFirstGenre, textSecondGenre, textDate, textTitle, textContent;
    private RatingBar ratingBar;
    private MovieDao dao;
    private Movie temp;
    private Bitmap bitmap;
    private Uri uri = null;
    public static final String MOVIE_EXTRA_Key = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_movie);

        InitializeWidget();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getExtras() != null)
            loadMovie();
        else {
            finish();
            Log.d("InfoMovieActivity", "intent null 발생!");
        }
    }

    private void loadMovie() {
        int id= getIntent().getExtras().getInt(MOVIE_EXTRA_Key);
        temp = dao.getmovieById(id);
        textType.setText(temp.getMovieType());
        textFirstGenre.setText(temp.getMovieGenre().split("/")[0]);
        textSecondGenre.setText(temp.getMovieGenre().split("/")[1]);
        textDate.setText(temp.getMovieDate());
        textTitle.setText(temp.getMovieTitle());
        textContent.setText(temp.getMovieContent());
        ratingBar.setRating(temp.getStar());

        // uri 방식으로 glide 라이브러리 사용
        if(temp.getMovieImagePath() != null){
            Uri tempUri = Uri.parse(temp.getMovieImagePath());
                Glide
                    .with(this)
                    .load(tempUri)
                    .error(R.drawable.baseline_add_photo_alternate_black_48dp)
                    .into(imageView);
        }
    }

    private void InitializeWidget() {
        imageView = (ImageView) findViewById(R.id.image_movie);
        textType = (TextView) findViewById(R.id.text_view_type);
        textFirstGenre = (TextView) findViewById(R.id.text_view_genre1);
        textSecondGenre = (TextView) findViewById(R.id.text_view_genre2);
        textDate = (TextView) findViewById(R.id.text_view_year);
        textTitle = (TextView) findViewById(R.id.text_view_title);
        textContent = (TextView) findViewById(R.id.text_view_comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        dao = MoviesDB.getInstance(this).moviesDao();

        ImageButton backButton = (ImageButton) findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageButton deleteButton = (ImageButton) findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("작품 삭제");
                builder.setMessage("정말로 작품을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                dao.deletemovie(temp);
                                InfoMovieActivity.this.finish();
                            }
                        })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });

        ImageButton modifyButton = (ImageButton) findViewById(R.id.button_modify);
        modifyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), ConfirmMovieActivity.class);
                moveIntent.putExtra(MOVIE_EXTRA_Key, temp.getId());
                startActivity(moveIntent);
            }
        });
    }
}
