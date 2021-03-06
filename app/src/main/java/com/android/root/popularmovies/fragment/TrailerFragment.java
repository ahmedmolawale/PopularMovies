package com.android.root.popularmovies.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.activity.MainScreen;
import com.android.root.popularmovies.adapter.TrailerAdapter;
import com.android.root.popularmovies.model.Movie;
import com.android.root.popularmovies.model.Trailer;
import com.android.root.popularmovies.model.Trailers;
import com.android.root.popularmovies.rest.ApiClient;
import com.android.root.popularmovies.rest.ApiInterface;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 5/11/17.
 */

public class TrailerFragment extends Fragment {

    private RecyclerView trailersRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private TrailerAdapter mTrailerAdapter;
    private ArrayList<Trailer> trailers;

    public TrailerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.trailer_fragment, container, false);
        trailersRecycler = (RecyclerView) view.findViewById(R.id.trailers_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        trailersRecycler.setLayoutManager(layoutManager);
        trailersRecycler.setHasFixedSize(true);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MainScreen.TRAILERS_ID)) {
            trailers = intent.getParcelableArrayListExtra(MainScreen.TRAILERS_ID);
            setUp(trailers);
        }
        return view;
    }
    private void setUp(ArrayList<Trailer> trailers){
        if (trailers.size() == 0) {
            Toast toast = Toast.makeText(getActivity(), "No Trailers", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            //create the movies list adapter here
            mTrailerAdapter = new TrailerAdapter(getActivity(), trailers);
            trailersRecycler.setAdapter(mTrailerAdapter);
        }

    }
}
