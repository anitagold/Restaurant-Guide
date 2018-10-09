package com.example.android.restaurantguide.adapter;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.model.PlacePhoto;

public class RestaurantImageSlider extends PagerAdapter {

    List<PlacePhoto> photoList;
    Context context;
    private static int picture_height;
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    private final String LOG_TAG = "RESTAURANT_IMAGE_SLIDER";


    public RestaurantImageSlider(Context con, List<PlacePhoto> photoList) {
        this.photoList = photoList;
        context = con;
        picture_height = Math.round(dipToPixels(con, 200));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.restImg);
        PlacePhoto placePhoto = photoList.get(position);
        StringBuilder urlBuilder = new StringBuilder(PHOTO_BASE_URL);

        // make the URL
        urlBuilder.append("?");
        urlBuilder.append("maxheight=" + picture_height + "&");
        urlBuilder.append("photoreference=" + placePhoto.getPhoto_reference() + "&");
        urlBuilder.append("key=" + context.getString(R.string.api_key));

        Glide.with(context).load(urlBuilder.toString()).into(imageView);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, displayMetrics);
    }
}
