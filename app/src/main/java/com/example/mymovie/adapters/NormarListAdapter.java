package com.example.mymovie.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymovie.R;
import com.example.mymovie.callbacks.MovieEventListener;
import com.example.mymovie.model.Movie;

import java.util.ArrayList;

import static com.example.mymovie.utils.DateUtils.dateFromLong;

public class NormarListAdapter extends RecyclerView.Adapter<NormarListAdapter.MovieHolder> {

    private Context context;
    private ArrayList<Movie> movies;
    private MovieEventListener listener;

    // 생성자에서 데이터 ArrayList 와 Context 전달 받음
    public NormarListAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.normal_list_layout, parent, false);

        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        final Movie movie = getmovie(position);
        if(movie!=null){
            holder.movieTitle.setText(movie.getMovieTitle());
            holder.movieDate.setText(movie.getMovieDate());
            holder.movieComment.setText(movie.getMovieContent());
            holder.movieConfirmDate.setText(dateFromLong(movie.getConfirmDate()));
            holder.ratingBar.setRating(movie.getStar());

            if(movie.getMovieImagePath() != null){
                Uri tempUri = Uri.parse(movie.getMovieImagePath());
                Glide
                        .with(holder.itemView.getContext())
                        .load(tempUri)
                        .into(holder.movieImageView);
            } else {
                Glide
                        .with(holder.itemView.getContext())
                        .load(R.drawable.baseline_add_photo_alternate_black_48dp)
                        .into(holder.movieImageView);
            }

            // 노트 클릭 이벤트 초기화
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMovieClick(movie);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private Movie getmovie (int position){
        return movies.get(position);
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        TextView movieComment, movieTitle, movieDate, movieConfirmDate;
        ImageView movieImageView;
        RatingBar ratingBar;

        public MovieHolder(View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movie_title);
            movieComment = itemView.findViewById(R.id.movie_comment);
            movieDate = itemView.findViewById(R.id.movie_date);
            movieConfirmDate = itemView.findViewById(R.id.movie_confirmDate);
            movieImageView = itemView.findViewById(R.id.movie_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

    public void setListener(MovieEventListener listener){
        this.listener = listener;
    }
}
