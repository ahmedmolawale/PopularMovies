package com.android.root.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.root.popularmovies.R;
import com.android.root.popularmovies.model.Review;

import java.util.ArrayList;

/**
 * Created by root on 5/13/17.
 */

public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder>{

    ArrayList<Review> mReviews;
    public ReviewAdapter(ArrayList<Review> reviews){
        this.mReviews = reviews;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout = R.layout.review_item;
        boolean shouldAttachToParentImmediately = false;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layout,parent,shouldAttachToParentImmediately);
        CustomViewHolder customViewHolder= new CustomViewHolder(view);
        return customViewHolder;
    }
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView authorName;
        private TextView reviewContent;
        public CustomViewHolder(View itemView) {
            super(itemView);
            authorName = (TextView) itemView.findViewById(R.id.review_author);
            reviewContent = (TextView) itemView.findViewById(R.id.review_content);
        }
        private void bind(int position) {
            Review review = mReviews.get(position);
            authorName.setText(review.getAuthor());
            Log.d("SEE", review.getContent());
            reviewContent.setText(review.getContent());
        }
    }
}
