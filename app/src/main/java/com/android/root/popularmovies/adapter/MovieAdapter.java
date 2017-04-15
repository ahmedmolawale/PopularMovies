package com.android.root.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by root on 4/14/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<Movie> movies;
    Context context;
    private final OnListItemClickListener mOnListItemClickListener;

    public MovieAdapter(Context context, List<Movie> movies,OnListItemClickListener onListItemClickListener){
        this.context = context;
        this.movies = movies;
        this.mOnListItemClickListener = onListItemClickListener;
    }

    //interface to handle clicks

    public interface OnListItemClickListener{

         void onListItemClick(int position);
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdOfMovieItem = R.layout.movie_item;
        boolean shouldAttachToParentImmediately = false;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View movieItemView = layoutInflater.inflate(layoutIdOfMovieItem,parent,shouldAttachToParentImmediately);
        MovieViewHolder movieViewHolder = new MovieViewHolder(movieItemView);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
            holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView movieImage;
        private TextView movieTitle;

        public MovieViewHolder(View itemView){
            super(itemView);

            movieImage = (ImageView) itemView.findViewById(R.id.movie_image);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_name);
            itemView.setOnClickListener(this);
        }


        public void bind(int position){
            //getting the movie at the binding position
            Movie movie = movies.get(position);
            //setting the movie title and the movie image
            movieTitle.setText(movie.getOriginalTitle());
            String BASE_URL = "http://image.tmdb.org/t/p/";
            String IMAGE_SIZE = "w185/";
            String POSTER_PATH = movie.getPosterPath();

            String imageUrl =  BASE_URL.concat(IMAGE_SIZE).concat(POSTER_PATH);
            Picasso.with(context).load(imageUrl).into(movieImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnListItemClickListener.onListItemClick(position);
        }
    }
}
