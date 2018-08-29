package com.example.android.restaurantguide.fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.adapter.RestaurantImageSlider;
import com.example.android.restaurantguide.adapter.ReviewsAdapter;
import com.example.android.restaurantguide.model.PlaceDetails;
import com.example.android.restaurantguide.model.PlaceDetailsResponse;
import com.example.android.restaurantguide.model.PlacePhoto;
import com.example.android.restaurantguide.model.RestaurantTimings;
import com.example.android.restaurantguide.model.Review;
import com.example.android.restaurantguide.rest.ApiClient;
import com.example.android.restaurantguide.rest.ApiInterface;

public class DetailsActivityFragment extends Fragment {

    private static final String LOG_TAG = "DetailsActivityFragment";

    //private static Context mContext;

    public DetailsActivityFragment() {
    }

    @BindView(R.id.restaurant_name)
    TextView restaurantTextView;

    @BindView(R.id.rest_address)
    TextView addressTextView;

    @BindView(R.id.rest_phone)
    TextView phoneTextView;

    @BindView(R.id.hours)
    TextView hoursTextView;

    @BindView(R.id.rating)
    TextView ratingTextView;

    @BindView(R.id.ratingbar)
    RatingBar ratingBarView;

    @BindView(R.id.isOpen)
    TextView status;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolBarLayout;

    ViewPager viewPager;
    RestaurantImageSlider restaurantImageSlider;
    private static Call<PlaceDetailsResponse> callDetails = null;
    ReviewsAdapter reviewAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ButterKnife.bind(this, view);
        Intent intent = getActivity().getIntent();

        recyclerView = (RecyclerView) view.findViewById(R.id.reviews_container);
        mCollapsingToolBarLayout.setTitle(getString(R.string.collapsing_toolbar_title));

        //check if any data received via intent
        if (intent != null && intent.hasExtra("placeId")) {
            String placeId = intent.getStringExtra("placeId");
            if (isOnline()) {
                setUpDetailsUI(placeId);
            } else {
                //no network connectivity
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
            }

        } else {
           // Log.e(LOG_TAG, "Error ! Place Id not received");
            Bundle arguments = getArguments();
            if (arguments != null) {
                String placeId = arguments.getString("placeId");
                if (isOnline()) {
                    setUpDetailsUI(placeId);
                } else {
                    //no network connectivity
                    Toast.makeText(getActivity(), getActivity().getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
                }
            }
        }


        viewPager = (ViewPager) view.findViewById(R.id.pager);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        return view;
    }

    /**
     * Method to check internet connectivity
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


    public void setUpDetailsUI(String placeId) {
        ApiInterface apiInterface =
                ApiClient.getClient().create(ApiInterface.class);

        callDetails = apiInterface.restaurantDetails(placeId, getString(R.string.api_key));


        if (callDetails != null) {
            callDetails.enqueue(new retrofit2.Callback<PlaceDetailsResponse>() {
                @Override
                public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {

                    PlaceDetails placeDetails = response.body().getResult();
                    if (placeDetails != null) {
                        restaurantTextView.setText(placeDetails.getName());
                        ratingTextView.setText(String.valueOf(placeDetails.getRating()));
                        ratingBarView.setRating(placeDetails.getRating());
                        addressTextView.setText(placeDetails.getFormatted_address());
                        phoneTextView.setText(placeDetails.getFormatted_phone_number());

                        List<PlacePhoto> placePhotos = placeDetails.getPhotos();

                        if (placePhotos != null) {
                            //we have photos
                            restaurantImageSlider = new RestaurantImageSlider(getActivity(), placePhotos);
                            if (viewPager != null) {
                                viewPager.setAdapter(restaurantImageSlider);
                                viewPager.setOffscreenPageLimit(5);
                            } else {
                                Log.e(LOG_TAG, "viewPager is null");
                            }
                        } else {
                            //no photo
                            Log.e(LOG_TAG, "placePhotos is null");
                        }

                        RestaurantTimings openingHours = placeDetails.getOpening_hours();

                        if (openingHours != null) {
                            if (openingHours.isOpen_now()) {
                                status.setText(getActivity().getString(R.string.status_open));
                                status.setTextColor(Color.GREEN);
                            } else {
                                status.setText(getActivity().getString(R.string.status_close));
                                status.setTextColor(Color.RED);
                            }

                            List<String> openingHoursList = openingHours.getWeekday_text();
                            //Log.i(LOG_TAG, "fetched list" + dayTime.toString());
                            if (openingHoursList != null && openingHoursList.size() > 0) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                                Date date = new Date();
                                String dayOfTheWeek = simpleDateFormat.format(date);
                                //Log.i(LOG_TAG , "today date " + dayOfTheWeek);
                                Iterator<String> iterator = openingHoursList.iterator();
                                while (iterator.hasNext()) {
                                    String day = iterator.next();
                                    //Log.i(LOG_TAG , "element" + day);
                                    String[] splitString = day.split(":");
                                    Log.i(LOG_TAG, splitString.toString());
                                    if (dayOfTheWeek.equalsIgnoreCase(splitString[0])) {
                                        hoursTextView.setText(day);
                                        break;
                                    }
                                }
                            }
                        }

                        List<Review> reviewList = placeDetails.getReviews();

                        if (reviewList != null) {
                            ReviewsAdapter reviewAdapter = new ReviewsAdapter(reviewList, getActivity());
                            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(reviewAdapter);

                        }
                    }
                }


                @Override
                public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {
                    Log.e(LOG_TAG, t.toString());
                }
            });
        }

    }

}


