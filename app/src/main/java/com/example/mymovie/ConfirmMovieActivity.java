package com.example.mymovie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymovie.adapters.SpinnerAdapter;
import com.example.mymovie.db.MovieDao;
import com.example.mymovie.db.MoviesDB;
import com.example.mymovie.model.Movie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static com.example.mymovie.InfoMovieActivity.MOVIE_EXTRA_Key;

public class ConfirmMovieActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner typeSpinner, firstGenreSpinner, secondGenreSpinner;
    private EditText textDate, textTitle, textComment;
    private RatingBar ratingBar;
    private MovieDao dao;
    private Movie temp;
    private List<Movie> list;
    private Bitmap bitmap;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_movie);

        // Spinner 초기화
        initializeSpinner();

        textDate = (EditText) findViewById(R.id.edit_year);
        textTitle = (EditText) findViewById(R.id.edit_title);
        textComment = (EditText) findViewById(R.id.edit_comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new Listener());

        // 뒤로 가기 버튼 초기화
        ImageButton backButton = (ImageButton) findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 작품 등록 버튼 초기화
        ImageButton confirmButton = (ImageButton) findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onSave();
                finish();
            }
        });

        // 이미지 추가 버튼 초기화
        imageView = (ImageView) findViewById(R.id.image_movie);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO : content에 이미지 추가 기능 넣기

                // 1. 갤러리에서 이미지 가져오기
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "image pick"), 1);
            }
        });

        dao = MoviesDB.getInstance(this).moviesDao();

        if(getIntent().getExtras() != null){
            int id= getIntent().getExtras().getInt(MOVIE_EXTRA_Key);
            temp = dao.getmovieById(id);

            typeSpinner.setSelection(getIndex(typeSpinner, temp.getMovieType()));
            firstGenreSpinner.setSelection(getIndex(firstGenreSpinner, temp.getMovieGenre().split("/")[0]));
            secondGenreSpinner.setSelection(getIndex(secondGenreSpinner, temp.getMovieGenre().split("/")[1]));
            textDate.setText(temp.getMovieDate());
            textTitle.setText(temp.getMovieTitle());
            textComment.setText(temp.getMovieContent());
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
        } else {
            temp = new Movie();
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {

            try {
                // 1. 가져온 이미지 editText에 넣기
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);

                imageView.setImageBitmap(bitmap);

                // 2. ROOM DB 에 큰 크기의 파일을 저장하면 row too big 오류가 나므로
                // 이미지의 절대 경로만 저장하여 note 객체에 저장한다
                if (data != null) {
                    uri = data.getData();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Listener implements RatingBar.OnRatingBarChangeListener{

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            ratingBar.setRating(rating);
        }
    }

    private void initializeSpinner() {
        typeSpinner = (Spinner)findViewById(R.id.spinner_type);
        String[] models = getResources().getStringArray(R.array.movie_type);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);

        // 장르1 Spinner
        firstGenreSpinner = (Spinner)findViewById(R.id.spinner_genre1);
        String[] models2 = getResources().getStringArray(R.array.movie_genre);
        SpinnerAdapter spinnerAdapter2 = new SpinnerAdapter(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models2);
        spinnerAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        firstGenreSpinner.setAdapter(spinnerAdapter2);

        // 장르2 Spinner
        secondGenreSpinner = (Spinner)findViewById(R.id.spinner_genre2);
        SpinnerAdapter spinnerAdapter3 = new SpinnerAdapter(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models2);
        spinnerAdapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        secondGenreSpinner.setAdapter(spinnerAdapter3);
    }

    private void onSave(){

        if(textTitle.getText().toString().equals("작품 제목") ||
                textTitle.getText().toString().trim().equals("")) {
            Log.d("ConfirmMovieActivity", "저장 중 제목 null 발생");
            return;
        }

        // 연도, 제목, 한줄평
        temp.setMovieDate(textDate.getText().toString());
        temp.setMovieTitle(textTitle.getText().toString());
        if(textComment != null)
            temp.setMovieContent(textComment.getText().toString());

        // 타입
        temp.setMovieType(typeSpinner.getSelectedItem().toString());

        // 장르
        String firstSpinnerText = firstGenreSpinner.getSelectedItem().toString();
        String secondSpinnerText = secondGenreSpinner.getSelectedItem().toString();
        String resultGenre = "";
        resultGenre += firstSpinnerText;
        resultGenre += "/" + secondSpinnerText;
        temp.setMovieGenre(resultGenre);

        // 별점
        temp.setStar((int)ratingBar.getRating());

        // 이미지
        if(uri != null)
            temp.setMovieImagePath(uri.toString());

        if(temp.getId() != 0)
            dao.updatemovie(temp);
        else{
            // 입력한 시간
            long date = new Date().getTime();
            temp.setConfirmDate(date);
            dao.insertmovie(temp);
        }
        Log.d("ConfirmMovieActivity", "저장 성공 발생");
    }
}

