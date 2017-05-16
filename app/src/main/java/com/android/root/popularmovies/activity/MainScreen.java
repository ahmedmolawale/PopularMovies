package com.android.root.popularmovies.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.adapter.MovieAdapter;
import com.android.root.popularmovies.data.MovieContract;
import com.android.root.popularmovies.model.Movie;
import com.android.root.popularmovies.model.Movies;
import com.android.root.popularmovies.model.Review;
import com.android.root.popularmovies.model.Reviews;
import com.android.root.popularmovies.model.Trailer;
import com.android.root.popularmovies.model.Trailers;
import com.android.root.popularmovies.rest.ApiClient;
import com.android.root.popularmovies.rest.ApiInterface;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreen extends AppCompatActivity implements MovieAdapter.OnListItemClickListener {

    //please place your API KEY here
    public final static String API_KEY = "";
    public static final String SORT_KEY = "sort_by";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVOURITE = "favourite";
    private static final String POPULAR_MOVIE_TITLE = "Popular Movies";
    private static final String TOP_RATED_MOVIE_TITLE = "Top Rated Movies";
    private static final String FAVOURITE_MOVIE_TITLE = "Favourite Movies";
    private static final String MOVIES_ID = "movies-id";

    public static final String TRAILERS_ID = "trailer_id";
    public static final String REVIEWS_ID = "reviews_id";
    public static final String IMAGE_POSTER_BYTE_ARRAY = "image_poster_byte_array";


    @InjectView(R.id.movies_recyclerview)
    RecyclerView mRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SharedPreferences sharedPreferences;


    ArrayList<Movie> movies;
    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);

        //setting the ButterKnife Lib
        ButterKnife.inject(this);

        Context context = MainScreen.this;
        mRecyclerView.setHasFixedSize(true);
        //if landscape mode, span size should be 4
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridLayoutManager = new GridLayoutManager(context, 5);
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mGridLayoutManager = new GridLayoutManager(context, 3);
        }
        mRecyclerView.setLayoutManager(mGridLayoutManager);


        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(MOVIES_ID);
            mMovieAdapter = new MovieAdapter(MainScreen.this, movies, MainScreen.this);
            mRecyclerView.setAdapter(mMovieAdapter);
            return;
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = sharedPreferences.getString(SORT_KEY, POPULAR);
        if (sortBy.equals(TOP_RATED)) {
            makeApiCall(sortBy);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(TOP_RATED_MOVIE_TITLE);
            }

        } else if (sortBy.equals(POPULAR)) {
            makeApiCall(sortBy);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(POPULAR_MOVIE_TITLE);
            }

        } else if (sortBy.equals(FAVOURITE)) {
            //load from db
            if (checkFavouriteMovies()) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(FAVOURITE_MOVIE_TITLE);
                }
                loadMoviesLocally();
            }
        }
    }

    public void loadMoviesLocally() {

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        //build the list of movies from the cursor and pass to the movie adapter

        if (cursor != null && cursor.moveToFirst()) {
            movies = new ArrayList<>();
            for (int i = 0; i < cursor.getCount(); i++) {
                Movie movie = new Movie();
                cursor.moveToPosition(i);
                String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
                String movieSynopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS));
                String movieRating = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING));
                String movieReleasedDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASED_DATE));
                byte[] image = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
                movie.setId(Integer.parseInt(movieId));
                movie.setOriginalTitle(movieTitle);
                movie.setOverview(movieSynopsis);
                movie.setVoteAverage(Double.parseDouble(movieRating));
                movie.setReleaseDate(movieReleasedDate);
                movie.setImage(image);
                movies.add(movie);
            }
            //create the movies list adapter here
            mMovieAdapter = new MovieAdapter(MainScreen.this, movies, MainScreen.this);
            mMovieAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mMovieAdapter);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        movies = savedInstanceState.getParcelableArrayList(MOVIES_ID);
        mMovieAdapter = new MovieAdapter(MainScreen.this, movies, MainScreen.this);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    public boolean checkFavouriteMovies() {

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        //build the list of movies from the cursor and pass to the movie adapter
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menuSortPopular) {

            getSupportActionBar().setTitle(POPULAR_MOVIE_TITLE);
            //store in preference in case when activity is re-launched
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainScreen.this);
            sharedPreferences.edit().putString(SORT_KEY, POPULAR).apply();
            makeApiCall(POPULAR);

        } else if (itemId == R.id.menuSortTopRated) {

            getSupportActionBar().setTitle(TOP_RATED_MOVIE_TITLE);
            //store in preference in case when activity is re-launched
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainScreen.this);
            sharedPreferences.edit().putString(SORT_KEY, TOP_RATED).apply();
            makeApiCall(TOP_RATED);
        } else if (itemId == R.id.menuSortFavourite) {
            //check whether we have any favourite movies
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainScreen.this);
            sharedPreferences.edit().putString(SORT_KEY, FAVOURITE).apply();
            if (checkFavouriteMovies()) {
                getSupportActionBar().setTitle(FAVOURITE_MOVIE_TITLE);
                mMovieAdapter.notifyDataSetChanged();
                loadMoviesLocally();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No favourites yet.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else if (itemId == R.id.menuDb) {

            startActivity(new Intent(MainScreen.this, AndroidDatabaseManager.class));

        }
        return true;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void makeApiCall(String sortBy) {

        if (!isOnline()) {

            String title = "Connection";
            String message = "No internet connection. Please try again.";
            displayMessage(title, message);

            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Movies> callToApi = apiInterface.getMovies(sortBy, API_KEY);

        callToApi.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {

                mProgressBar.setVisibility(View.INVISIBLE);
                //getting the response body
                Movies moviesBody = response.body();
                if (moviesBody == null) {

                    ResponseBody responseBody = response.errorBody();
                    String errorTitle;
                    String errorMessage;
                    if (responseBody != null) {
                        errorTitle = "Error";
                        errorMessage = "An error occurred.";
                    } else {
                        errorTitle = "Error";
                        errorMessage = "No data Received.";
                    }
                    displayMessage(errorTitle, errorMessage);

                } else {
                    movies = response.body().getResults();

                    if (movies.size() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Movies", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        //create the movies list adapter here
                        mMovieAdapter = new MovieAdapter(MainScreen.this, movies, MainScreen.this);
                        mRecyclerView.setAdapter(mMovieAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                String errorTitle = "Error";
                String errorMessage = "Data request failed.";
                displayMessage(errorTitle, errorMessage);
            }
        });


    }

    public void displayMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onListItemClick(int position) {

        Movie movie = movies.get(position);

        Context context = MainScreen.this;
        Class activityToStart = MovieDetails.class;
        intent = new Intent(context, activityToStart);
        intent.putExtra(Intent.EXTRA_TEXT, movie);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPreferences.getString(SORT_KEY, POPULAR);
        if (sortBy.equals(FAVOURITE)) {
            //load details locally
            intent.putExtra(IMAGE_POSTER_BYTE_ARRAY,movie.getImage());
            loadTrailersAndReviews(movie.getId());
        } else {
            //get the movie review and trailers
            makeApiCallForReviewAndTrailer(movie.getId());
        }
    }

    private void loadTrailersAndReviews(int movieId) {

        ContentResolver contentResolver = getContentResolver();
        String id = String.valueOf(movieId);
        Uri uri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        trailers = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Trailer trailer = new Trailer();
                cursor.moveToPosition(i);
                String name = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME));
                String key = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY));
                trailer.setName(name);
                trailer.setKey(key);
                trailers.add(trailer);
            }
        }

        if (trailers.size() > 0) {
            intent.putParcelableArrayListExtra(TRAILERS_ID, trailers);
        } else
            Toast.makeText(getApplicationContext(), "No trailers.", Toast.LENGTH_LONG).show();

        Uri uri2 = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        Cursor cursor2 = contentResolver.query(uri2, null, null, null, null);
        reviews = new ArrayList<>();
        if (cursor2 != null && cursor2.moveToFirst()) {
            for (int i = 0; i < cursor2.getCount(); i++) {
                Review review = new Review();
                cursor2.moveToPosition(i);
                String authorName = cursor2.getString(cursor2.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME));
                String reviewContent = cursor2.getString(cursor2.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT));
                review.setAuthor(authorName);
                review.setContent(reviewContent);
                reviews.add(review);
            }
        }
        if (reviews.size() > 0) {
            intent.putParcelableArrayListExtra(REVIEWS_ID, reviews);
        } else
            Toast.makeText(getApplicationContext(), "No Reviews.", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    public void makeApiCallForReviewAndTrailer(int movieId) {
        if (!isOnline()) {

            String title = "Connection";
            String message = "No internet connection. Please try again.";
            displayMessage(title, message);
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //for the trailers
        Call<Trailers> callToApi = apiInterface.getTrailers(movieId, MainScreen.API_KEY);
        //for the reviews
        Call<Reviews> callToApi2 = apiInterface.getReviews(movieId, MainScreen.API_KEY);
        callToApi.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                //getting the response body
                Trailers trailersBody = response.body();
                if (trailersBody == null) {
                    ResponseBody responseBody = response.errorBody();
                    String errorTitle;
                    String errorMessage;
                    if (responseBody != null) {
                        errorTitle = "Error";
                        errorMessage = "An error occurred.";

                    } else {
                        errorTitle = "Error";
                        errorMessage = "No data Received.";
                    }
                    displayMessage(errorTitle, errorMessage);
                } else {
                    trailers = response.body().getTrailers();
                    intent.putParcelableArrayListExtra(TRAILERS_ID, trailers);
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                String errorTitle = "Error";
                String errorMessage = "Data request failed.";
                displayMessage(errorTitle, errorMessage);
                return;
            }
        });
        callToApi2.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {

                mProgressBar.setVisibility(View.INVISIBLE);
                //getting the response body
                Reviews reviewsBody = response.body();
                if (reviewsBody == null) {
                    ResponseBody responseBody = response.errorBody();
                    String errorTitle;
                    String errorMessage;
                    if (responseBody != null) {
                        errorTitle = "Error";
                        errorMessage = "An error occurred.";
                    } else {
                        errorTitle = "Error";
                        errorMessage = "No data Received.";
                    }
                    displayMessage(errorTitle, errorMessage);
                } else {
                    reviews = response.body().getReviews();
                    intent.putParcelableArrayListExtra(REVIEWS_ID, reviews);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                String errorTitle = "Error";
                String errorMessage = "Data request failed.";
                displayMessage(errorTitle, errorMessage);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //to avoid any reload when orientation is changed
        outState.putParcelableArrayList(MOVIES_ID, movies);
        super.onSaveInstanceState(outState);
    }
}
