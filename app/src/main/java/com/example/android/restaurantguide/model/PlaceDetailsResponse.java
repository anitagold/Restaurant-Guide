package com.example.android.restaurantguide.model;

public class PlaceDetailsResponse {

    PlaceDetails result;
    String status;

    public PlaceDetailsResponse() {

    }

    public PlaceDetails getResult() {
        return result;
    }

    public void setResult(PlaceDetails result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
