package com.example.android.restaurantguide.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.widget.RemoteViews;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.sync.PlacesPullJob;

public class RestaurantGuideWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, RestaurantListWidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_restaurant_guide);

            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.list_view, intent);

            remoteViews.setEmptyView(R.id.list_view, R.id.empty_txt_view);

            remoteViews.setInt(R.id.list_view, "setBackgroundResource", R.color.colorPrimaryDark);
            remoteViews.setInt(R.id.widget_content, "setBackgroundResource", R.color.colorPrimary);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (PlacesPullJob.getActionDataUpdated().equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }
    }
}
