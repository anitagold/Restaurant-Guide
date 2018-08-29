package com.example.android.restaurantguide.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class RestaurantListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RestaurantListRemoteViewFactory(this.getApplicationContext(), intent);
    }
}
