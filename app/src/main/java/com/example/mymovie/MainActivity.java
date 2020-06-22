package com.example.mymovie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovie.adapters.MoviesAdapter;
import com.example.mymovie.callbacks.MovieEventListener;

import com.example.mymovie.db.MovieDao;
import com.example.mymovie.db.MoviesDB;
import com.example.mymovie.model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mymovie.InfoMovieActivity.MOVIE_EXTRA_Key;

public class MainActivity extends AppCompatActivity implements MovieEventListener {

    private RecyclerView recentRecyclerView, movieRecyclerView, animeRecyclerView, dramaRecyclerView;
    private TextView textMovieCount, textAnimeCount, textDramaCount, textTotalCount,
                     textRecentType, textMovieType, textAnimeType, textDramaType;
    private ArrayList<Movie> movies;
    private MoviesAdapter adapter;
    private MovieDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 허용 작업
        // 권한 거부시 앱 종료
        checkPermission();

        textMovieCount = (TextView)findViewById(R.id.text_movie_count);
        textAnimeCount = (TextView)findViewById(R.id.text_anime_count);
        textDramaCount = (TextView)findViewById(R.id.text_drama_count);
        textTotalCount = (TextView)findViewById(R.id.text_total_count);

        textRecentType = (TextView)findViewById(R.id.text_view_recentType);
        textRecentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveListMovie("최근");
            }
        });
        textMovieType = (TextView)findViewById(R.id.text_view_movieType);
        textMovieType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveListMovie("영화");
            }
        });
        textAnimeType = (TextView)findViewById(R.id.text_view_animeType);
        textAnimeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveListMovie("애니메이션");
            }
        });
        textDramaType = (TextView)findViewById(R.id.text_view_dramaType);
        textDramaType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveListMovie("드라마");
            }
        });

        // 리사이클러 뷰 초기화
        initializeRecyclerview();

        // 작품 등록 버튼 초기화
        ImageButton moveCreateActivityButton = (ImageButton) findViewById(R.id.button_under_create);
        moveCreateActivityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), ConfirmMovieActivity.class);
                startActivity(moveIntent);
            }
        });

        // dao DB 바인딩
        dao = MoviesDB.getInstance(this).moviesDao();
    }

    private void moveListMovie(String type) {
        Intent moveIntent = new Intent(getApplicationContext(), ListMovieActivity.class);
        moveIntent.putExtra("type", type);
        startActivity(moveIntent);
    }


    private void initializeRecyclerview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recentRecyclerView = findViewById(R.id.recyclerView_main_list);
        recentRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        movieRecyclerView = findViewById(R.id.recyclerView_main_list2);
        movieRecyclerView.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        animeRecyclerView = findViewById(R.id.recyclerView_main_list3);
        animeRecyclerView.setLayoutManager(layoutManager3);

        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        dramaRecyclerView = findViewById(R.id.recyclerView_main_list4);
        dramaRecyclerView.setLayoutManager(layoutManager4);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMovies();
    }

    private void loadMovies() {
        this.movies = new ArrayList<>();
        List<Movie> list = dao.getmovies(); // DB 에서 모든 노트 가져오기
        Collections.sort(list); // 가장 최근 글이 위로 오게 정렬
        this.movies.addAll(list);
        this.adapter = new MoviesAdapter(this, movies);
        this.adapter.setListener(this);
        this.recentRecyclerView.setAdapter(adapter);
        int tempCount = movies.size();
        textTotalCount.setText(tempCount + " 개의 작품을 감상했군요.");

        // 영화
        this.movies = new ArrayList<>();
        this.movies.addAll(getListByType("영화", list));
        this.adapter = new MoviesAdapter(this, movies);
        this.adapter.setListener(this);
        this.movieRecyclerView.setAdapter(adapter);
        tempCount = movies.size();
        textMovieCount.setText(tempCount + " 개");

        // 애니
        this.movies = new ArrayList<>();
        this.movies.addAll(getListByType("애니메이션", list));
        this.adapter = new MoviesAdapter(this, movies);
        this.adapter.setListener(this);
        this.animeRecyclerView.setAdapter(adapter);
        tempCount = movies.size();
        textAnimeCount.setText(tempCount + " 개");

        // 드라마
        this.movies = new ArrayList<>();
        this.movies.addAll(getListByType("드라마", list));
        this.adapter = new MoviesAdapter(this, movies);
        this.adapter.setListener(this);
        this.dramaRecyclerView.setAdapter(adapter);
        tempCount = movies.size();
        textDramaCount.setText(tempCount + " 개");
    }


    private List<Movie> getListByType(String type, List<Movie> list){

        List<Movie> returnList = new ArrayList<>();
        try {
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).getMovieType().equals(type))
                    returnList.add(list.get(i));
            }
        } catch (Exception ex){
            Log.d("MainActivity","getListByType 에러 발생 : " + ex);
        }
        return returnList;
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent moveIntent = new Intent(this, InfoMovieActivity.class);
        moveIntent.putExtra(MOVIE_EXTRA_Key, movie.getId());
        startActivity(moveIntent);
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        String [] permission_list = {"android.permission.WRITE_EXTERNAL_STORAGE"
                                    , "android.permission.INTERNET"
                                    , "android.permission.READ_EXTERNAL_STORAGE"
                                    , "android.permission.CAMERA"};

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱 권한을 설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

}
