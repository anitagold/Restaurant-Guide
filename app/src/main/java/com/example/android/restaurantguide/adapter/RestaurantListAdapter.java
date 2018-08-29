package com.example.android.restaurantguide.adapter;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.data.RestaurantGuideContract;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {

    Activity activity;
    Cursor cursor;
    ClickListener clickListener;
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    private static final String LOG_TAG = "RESTAURANT_LIST_ADAPTER";
    private static final int imageHeight = 150;
    private int myHeight;


    public RestaurantListAdapter(Cursor cursor, Activity activity, ClickListener clickListener) {
        this.activity = activity;
        this.cursor = cursor;
        this.clickListener = clickListener;
        myHeight = Math.round(dipToPixels(this.activity, imageHeight));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        cursor.moveToPosition(position);
        holder.restaurantNameTextView.setText(cursor.getString(
                cursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME)));

        float ratingScore = cursor.getFloat(cursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_RATING));

        String photoReference = cursor.getString(
                cursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_PHOTO_REFERENCE));

        StringBuilder urlBuilder = new StringBuilder(PHOTO_BASE_URL);
        urlBuilder.append("?");
        urlBuilder.append("maxheight=" + myHeight + "&");
        urlBuilder.append("photoreference=" + photoReference + "&");
        urlBuilder.append("key=" + activity.getString(R.string.api_key));

        holder.ratingBar.setRating(ratingScore);
        Glide.clear(holder.restaurantImageView);
        Glide.with(holder.itemView.getContext()).load(urlBuilder.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate().listener(new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                             Target<GlideDrawable> target,
                                                             boolean isFromMemoryCache, boolean isFirstResource) {
                            Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                            Palette palette = Palette.generate(bitmap);
                            int defaultColor = 0xFF333333;
                            int darkMutedColor = palette.getDarkMutedColor(defaultColor);
                            holder.cardView.setBackgroundColor(darkMutedColor);
                            return false;
                            }
                          }
                ).into(holder.restaurantImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {

                                     if (clickListener != null) {
                                         clickListener.onItemClick(view, viewHolder.getAdapterPosition());
                                     }
                                 }
                             }
        );

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView restaurantImageView;
        TextView restaurantNameTextView;
        CardView cardView;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            restaurantImageView = (ImageView) view.findViewById(R.id.thumbnail);
            restaurantNameTextView = (TextView) view.findViewById(R.id.rest_name);
            ratingBar = (RatingBar) view.findViewById(R.id.ratings);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ClickListener {
        void onItemClick(View view, int pos);
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, displayMetrics);
    }
}
