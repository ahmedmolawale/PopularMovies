package com.android.root.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MovieDetails extends AppCompatActivity {

     @InjectView(R.id.movie_image_detail) ImageView mMovieImage;
    @InjectView(R.id.movie_name_detail) TextView mMovieTitle;
     @InjectView(R.id.movie_overview) TextView mMovieOverview;
    @InjectView(R.id.user_rating) TextView mUserRating;
     @InjectView(R.id.release_date) TextView mReleaseDate;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);

        //injecting view with the ButterKnife Library, its so sweet
        ButterKnife.inject(this);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
           movie = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
            setUpContents(movie);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_details_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_share){
            String mimeType = "text/plain";
            String title = "Share Movie";
            String message = "Hey Buddy, Here is a movie you need to see: "+ movie.getOriginalTitle();

            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this);
            intentBuilder.setType(mimeType);
            intentBuilder.setChooserTitle(title);
            intentBuilder.setText(message);
            intentBuilder.startChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpContents(Movie movie){

        getSupportActionBar().setTitle(movie.getOriginalTitle());
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String IMAGE_SIZE = "w185/";
        String POSTER_PATH = movie.getPosterPath();
        Context context = MovieDetails.this;
        String imageUrl = BASE_URL.concat(IMAGE_SIZE).concat(POSTER_PATH);
        Picasso.with(context).load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.action_error)
                .into(mMovieImage);


        mMovieTitle.setText(movie.getOriginalTitle());
        mMovieOverview.setText(movie.getOverview());
        mUserRating.setText(String.valueOf(movie.getVoteAverage()));
        mReleaseDate.setText(movie.getReleaseDate());
    }
}
