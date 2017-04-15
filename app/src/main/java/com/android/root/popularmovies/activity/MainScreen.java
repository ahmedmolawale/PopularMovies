package com.android.root.popularmovies.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.root.popularmovies.model.Movie;
import com.android.root.popularmovies.model.Movies;
import com.android.root.popularmovies.rest.ApiClient;
import com.android.root.popularmovies.rest.ApiInterface;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainScreen extends AppCompatActivity implements MovieAdapter.OnListItemClickListener{

    //please place your API KEY here
    private final static String API_KEY = "API_KEY_HERE";
    public static final String SORT_KEY = "sort_by";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String POPULAR_MOVIE_TITLE = "Popular Movies";
    private static final String TOP_RATED_MOVIE_TITLE = "Top Rated Movies";


    @InjectView(R.id.movies_recyclerview) RecyclerView mRecyclerView;
    @InjectView(R.id.progress_bar) ProgressBar mProgressBar;

    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SharedPreferences sharedPreferences;


    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);


        //setting the ButterKnife Lib
        ButterKnife.inject(this);

        Context context = MainScreen.this;
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(context,3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = sharedPreferences.getString(SORT_KEY,POPULAR);
        if(sortBy.equals(TOP_RATED)){
            getSupportActionBar().setTitle(TOP_RATED_MOVIE_TITLE);
        }else if(sortBy.equals(POPULAR_MOVIE_TITLE)){
            getSupportActionBar().setTitle(TOP_RATED_MOVIE_TITLE);
        }
        makeApiCall(sortBy);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == R.id.menuSortPopular){

            getSupportActionBar().setTitle(POPULAR_MOVIE_TITLE);
            //store in preference in case when activity is re-launched
            sharedPreferences.edit().putString(SORT_KEY,POPULAR).apply();
            makeApiCall(POPULAR);

        }else if(itemId == R.id.menuSortTopRated){

            getSupportActionBar().setTitle(TOP_RATED_MOVIE_TITLE);
            //store in preference in case when activity is re-launched
            sharedPreferences.edit().putString(SORT_KEY,TOP_RATED).apply();
            makeApiCall(TOP_RATED);
        }

        return true;
    }

    private boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public  void makeApiCall(String sortBy){

        if(!isOnline()){

            String title = "Connection";
            String message = "No internet connection. Please try again.";
            displayMessage(title,message);

            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Movies> callToApi = apiInterface.getMovies(sortBy,API_KEY);

        callToApi.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {

                mProgressBar.setVisibility(View.INVISIBLE);
                //getting the response body
                 Movies moviesBody = response.body();
                if(moviesBody == null){

                    ResponseBody responseBody = response.errorBody();
                    String errorTitle;
                    String errorMessage;
                    if(responseBody != null){
                        errorTitle = "Error";
                        errorMessage = "An error occurred.";
                    }else{
                        errorTitle = "Error";
                        errorMessage = "No data Received.";
                    }
                    displayMessage(errorTitle,errorMessage);

                }else{
                     movies = response.body().getResults();

                    if (movies.size() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(),"No Movies", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        //create the movies list adapter here
                        mMovieAdapter = new MovieAdapter(MainScreen.this,movies,MainScreen.this);
                        mRecyclerView.setAdapter(mMovieAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                String errorTitle = "Error";
                String errorMessage = "Data request failed.";
                displayMessage(errorTitle,errorMessage);
            }
        });



    }

    public  void displayMessage(String title, String message){
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

        Intent intent = new Intent(context,activityToStart);
        intent.putExtra(Intent.EXTRA_TEXT,movie);
        startActivity(intent);
    }
}
