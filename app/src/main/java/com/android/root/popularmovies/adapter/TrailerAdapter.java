package com.android.root.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.model.Trailer;

import java.util.ArrayList;

/**
 * Created by root on 5/13/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.CustomViewHolder> {

    private Context mContext;
    private ArrayList<Trailer> mTrailers;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        this.mContext = context;
        this.mTrailers = trailers;
    }

    @Override
    public TrailerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        boolean shouldAttachToParentImmediately = false;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trailer_item, parent, shouldAttachToParentImmediately);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.CustomViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer trailer = mTrailers.get(position);
                Intent appIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
                mContext.startActivity(appIntent);
            }
        });
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder  {

        private TextView trailerName;

        public CustomViewHolder(View itemView) {
            super(itemView);
            trailerName = (TextView) itemView.findViewById(R.id.video_trailer_text);
        }

        public void bind(int position) {
            String trailerName = mTrailers.get(position).getName();
            this.trailerName.setText(trailerName);
        }
    }
}
