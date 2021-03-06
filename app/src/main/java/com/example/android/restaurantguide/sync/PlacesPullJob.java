package com.example.android.restaurantguide.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.data.RestaurantGuideContract;
import com.example.android.restaurantguide.model.PlaceDetails;
import com.example.android.restaurantguide.model.PlacePhoto;
import com.example.android.restaurantguide.model.Restaurant;
import com.example.android.restaurantguide.model.RestaurantResponse;
import com.example.android.restaurantguide.rest.ApiClient;
import com.example.android.restaurantguide.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public final class PlacesPullJob {

    private static final int RADIUS = 2000;
    private static final String TYPE = "restaurant";
    private static final String LOG_TAG = "PLACESPULLJOB";
    private static Call<RestaurantResponse> call = null;
    private static final String ACTION_DATA_UPDATED = "com.example.android.restaurantguide.ACTION_DATA_UPDATED";
    //private static Call<PlaceDetailsResponse> callDetails = null;
    private static Context mContext;
    private static List<Restaurant> mRestaurantList;
    private static List<PlaceDetails> mPlaceDtailsList;

    public static String getActionDataUpdated() {
        return ACTION_DATA_UPDATED;
    }

    public PlacesPullJob() {
    }

    /**
     * This method gets the places nearby
     */
    public static void getNearByPlaces(Context context, String location) {
        mContext = context;

        dataCleanUp();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        call = apiService.restaurantList(location, RADIUS, TYPE, context.getString(R.string.api_key));

        if (call != null) {
            call.enqueue(new retrofit2.Callback<RestaurantResponse>() {
                @Override
                public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                    if (response != null) {
                        List<Restaurant> restaurantList = response.body().getResults();

                        Log.i(LOG_TAG, String.valueOf(restaurantList.size()));
                        if (restaurantList == null || restaurantList.size() == 0) {
                            Log.i(LOG_TAG, "Failed to retrieve required data");
                        } else {
                            //bulk insert in restaurants table
                            insertRestaurantData(restaurantList);
                        }

                    } else {
                        Log.e(LOG_TAG, "Result object is null");
                    }
                }

                @Override
                public void onFailure(Call<RestaurantResponse> call, Throwable throwable) {
                    Log.e(LOG_TAG, throwable.toString());
                }
            });

        }
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        mContext.sendBroadcast(dataUpdatedIntent);


    }

    /**
     * This method inserts the restaurant data
     */
    public static void insertRestaurantData(List<Restaurant> restaurantList) {
        if (restaurantList != null && restaurantList.size() > 0) {
            Iterator<Restaurant> it = restaurantList.iterator();
            ArrayList<ContentValues> restaurantCVs = new ArrayList<>();

            while (it.hasNext()) {
                Restaurant restaurant = it.next();
                ContentValues restaurantCV = new ContentValues();
                restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME, restaurant.getName());
                restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_RATING, restaurant.getRating());
                restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_PLACE_ID, restaurant.getPlace_id());
                restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_REFERENCE, restaurant.getReference());


                List<PlacePhoto> placePhotos = restaurant.getPhotos();
                if (placePhotos != null) {
                    restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_WIDTH, placePhotos.get(0).getWidth());
                    restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_HEIGHT, placePhotos.get(0).getHeight());
                    restaurantCV.put(RestaurantGuideContract.RestaurantEntry.COLUMN_PHOTO_REFERENCE,
                            placePhotos.get(0).getPhoto_reference());

                } else {
                    continue;
                }
                restaurantCVs.add(restaurantCV);
            }

            int rowsInserted = mContext.getContentResolver().bulkInsert(RestaurantGuideContract.RestaurantEntry.CONTENT_URI,
                    restaurantCVs.toArray(new ContentValues[restaurantCVs.size()]));

            Log.i(LOG_TAG, "Data insertion successful for rows :" + rowsInserted);


        } else {
            //handle error case when Restaurant object is null
        }

    }

    public static void insertReviews() {

    }

    public static void dataCleanUp() {
        int rowsDeleted = mContext.getContentResolver().delete(RestaurantGuideContract.RestaurantEntry.CONTENT_URI, null, null);
        Log.i(LOG_TAG, "Data deletion successful for rows :" + rowsDeleted);
    }

}
