package com.android.root.popularmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.model.Movie;

import butterknife.InjectView;

/**
 * Created by root on 5/11/17.
 */

public class MoreFragment extends Fragment {
    private TextView synopsis;
    private TextView userRating;
    private TextView releaseDate;
    private Movie movie;
    public MoreFragment(){
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.more_fragment,container,false);
        synopsis = (TextView) view.findViewById(R.id.movie_synopsis);
        userRating = (TextView)view.findViewById(R.id.user_rating);
        releaseDate = (TextView)view.findViewById(R.id.release_date);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            setUp(movie);
        }
        return  view;
    }
    private void setUp(Movie movie){
        synopsis.setText(movie.getOverview());
        userRating.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
    }
}
