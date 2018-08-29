package com.example.android.restaurantguide.adapter;

import android.support.v7.widget.RecyclerView;
import android.content.Context;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewsAdapter(List<Review> reviews, Context context) {
        this.context = context;
        reviewList = reviews;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.nameTextView.setText(review.getAuthor_name());

        holder.reviewTextView.setText(review.getText());
        Glide.with(holder.itemView.getContext())
                .load(review.getProfile_photo_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.profilePic);

    }

    @Override
    public int getItemCount() {

        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic;
        TextView nameTextView;
        TextView reviewTextView;

        public ViewHolder(View view) {
            super(view);
            profilePic = (ImageView) view.findViewById(R.id.profile_pic);
            nameTextView = (TextView) view.findViewById(R.id.name_textview);
            reviewTextView = (TextView) view.findViewById(R.id.review_textview);
        }
    }
}
