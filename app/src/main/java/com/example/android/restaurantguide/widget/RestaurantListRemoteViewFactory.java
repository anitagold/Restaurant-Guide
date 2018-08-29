package com.example.android.restaurantguide.widget;

import android.appwidget.AppWidgetManager;
import android.database.Cursor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import android.widget.RemoteViews;
import android.widget.AdapterView;
import android.widget.RemoteViewsService;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.data.RestaurantGuideContract;

public class RestaurantListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor dataCursor = null;
    private Context context = null;
    private int widgetId;

    private static String[] RestaurantColumns = {
            RestaurantGuideContract.RestaurantEntry._ID,
            RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME,
            RestaurantGuideContract.RestaurantEntry.COLUMN_RATING,
            RestaurantGuideContract.RestaurantEntry.COLUMN_PLACE_ID
    };

    RestaurantListRemoteViewFactory(Context context, Intent intent) {
        this.context = context;
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

        dataCursor = context.getContentResolver().query(RestaurantGuideContract.RestaurantEntry.CONTENT_URI,
                RestaurantColumns,null,null,null);
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||
                dataCursor == null || !dataCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        if (dataCursor != null) {
            String name = dataCursor.getString(dataCursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME));

            float rating = dataCursor.getFloat(dataCursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_RATING));

            Resources resources = context.getResources();
            String text = resources.getString(R.string.widget_rating_txt, name, String.valueOf(rating));

            remoteViews.setTextViewText(R.id.near_rest_name , text );
           // views.setTextViewText(R.id.near_rest_name, restName + " Rating : " + String.valueOf(rating));
        }

        return remoteViews;
    }


    @Override
    public int getCount() {
        return dataCursor == null ? 0 : dataCursor.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        int item = 0;
        if (dataCursor.moveToPosition(position)) {
            item = dataCursor.getInt(dataCursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry._ID));
        }
        return item;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        if (dataCursor != null) {
            dataCursor.close();
            dataCursor = null;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
