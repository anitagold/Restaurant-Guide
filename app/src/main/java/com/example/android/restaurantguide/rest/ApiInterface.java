package com.example.android.restaurantguide.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.example.android.restaurantguide.model.PlaceDetailsResponse;
import com.example.android.restaurantguide.model.RestaurantResponse;

public interface ApiInterface {

    @GET("nearbysearch/json")
    public Call<RestaurantResponse> restaurantList(@Query("location") String location, @Query("radius") int radius
            , @Query("type") String placeType, @Query("key") String api_key);

    @GET("details/json")
    public Call<PlaceDetailsResponse> restaurantDetails(@Query("placeid") String placeId, @Query("key") String api_key);
}
