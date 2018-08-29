package com.example.android.restaurantguide.model;

import java.util.ArrayList;
import java.util.List;

public class RestaurantResponse {

    List<Restaurant> results;
    String status;

    public RestaurantResponse() {
        results = new ArrayList<Restaurant>();
    }

    public List<Restaurant> getResults() {
        return results;
    }

    public void setResults(List<Restaurant> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
