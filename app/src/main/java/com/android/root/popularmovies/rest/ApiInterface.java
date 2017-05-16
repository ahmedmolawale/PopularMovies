package com.android.root.popularmovies.rest;

import com.android.root.popularmovies.model.Movies;
import com.android.root.popularmovies.model.Reviews;
import com.android.root.popularmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by root on 4/14/17.
 */

public interface ApiInterface {


    /**
     *
     *
     * Defines a method signature to simulate the HTTP GET request with @GET annotation
     * @param  sort_by
     * @param apiKey
     *
     */
    @GET("/3/movie/{sort_by}")
    Call<Movies> getMovies(@Path("sort_by") String sort_by,@Query("api_key") String apiKey);

    /**
     *
     *
     * Defines a method signature to simulate the HTTP GET request with @GET annotation
     * @param  id
     * @param apiKey
     *
     */
   @GET("/3/movie/{id}/videos")
    Call<Trailers> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);


    /**
     *
     *
     * Defines a method signature to simulate the HTTP GET request with @GET annotation
     * @param  id
     * @param apiKey
     *
     */
    @GET("/3/movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String apiKey);
}
