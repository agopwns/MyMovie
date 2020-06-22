package com.example.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mymovie.adapters.MoviesAdapter;
import com.example.mymovie.adapters.NormarListAdapter;
import com.example.mymovie.adapters.StaggeredListAdapter;
import com.example.mymovie.db.MovieDao;
import com.example.mymovie.db.MoviesDB;
import com.example.mymovie.model.Movie;
import com.example.mymovie.callbacks.MovieEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mymovie.InfoMovieActivity.MOVIE_EXTRA_Key;

public class ListMovieActivity extends AppCompatActivity implements MovieEventListener {

    private RecyclerView recyclerView;
    TextView textType;
    Spinner viewWaySpinner;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ArrayList<Movie> movies;
    private MoviesAdapter gridAdapter;
    private StaggeredListAdapter staggeredAdapter;
    private NormarListAdapter normarAdapter;
    private MovieDao dao;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movie);

        textType = (TextView) findViewById(R.id.text_view_type_title);

        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView = findViewById(R.id.recyclerView_main_list);
        recyclerView.setLayoutManager(layoutManager);

        // 뒤로 가기 버튼 초기화
        ImageButton backButton = (ImageButton) findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getExtras() != null){
            type = getIntent().getExtras().getString("type");
        } else {
            type = "오류";
        }

        // Spinner
        viewWaySpinner = (Spinner)findViewById(R.id.spinner_view_way);
        String[] models = getResources().getStringArray(R.array.view_way);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        viewWaySpinner.setAdapter(spinnerAdapter);

        viewWaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = viewWaySpinner.getSelectedItem().toString();

                if(selectedItem.equals("일반")){
                    linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false);

                    recyclerView = findViewById(R.id.recyclerView_main_list);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    loadMovies(type);

                } else if (selectedItem.equals("액자식")){
                    gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
                    recyclerView = findViewById(R.id.recyclerView_main_list);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    loadMovies(type);
                } else {
                    staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    recyclerView = findViewById(R.id.recyclerView_main_list);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    loadMovies(type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dao = MoviesDB.getInstance(this).moviesDao();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMovies(type);
    }

    private void loadMovies(String type) {

        String selectedItem = viewWaySpinner.getSelectedItem().toString();

        List<Movie> list = dao.getmovies(); // DB 에서 모든 노트 가져오기
        Collections.sort(list); // 가장 최근 글이 위로 오게 정렬

        this.movies = new ArrayList<>();

        if(type.equals("최근"))
            this.movies.addAll(list);
        else
            this.movies.addAll(getListByType(type, list));

        if(selectedItem.equals("일반")){
            normarAdapter = new NormarListAdapter(this, movies);
            this.normarAdapter.setListener(this);
            this.recyclerView.setAdapter(normarAdapter);
        } else if (selectedItem.equals("액자식")){
            gridAdapter = new MoviesAdapter(this, movies);
            this.gridAdapter.setListener(this);
            this.recyclerView.setAdapter(gridAdapter);
        } else {
            staggeredAdapter = new StaggeredListAdapter(this, movies);
            this.staggeredAdapter.setListener(this);
            this.recyclerView.setAdapter(staggeredAdapter);
        }

        textType.setText(type);
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
}
