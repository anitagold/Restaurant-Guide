package com.example.android.restaurantguide.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantGuideDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "restaurant.db";

    public RestaurantGuideDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createTableRestaurantScript());

        db.execSQL(createTableDetailScript());

        db.execSQL(createTableReviewScript());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + RestaurantGuideContract.RestaurantEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantGuideContract.DetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantGuideContract.ReviewEntry.TABLE_NAME);
        onCreate(db);
    }

    public String createTableRestaurantScript() {

        final String SQL_CREATE_RESTAURANT_TABLE = "CREATE TABLE " + RestaurantGuideContract.RestaurantEntry.TABLE_NAME
                + " ( " + RestaurantGuideContract.RestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME + " TEXT NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_RATING + " REAL NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_REFERENCE + " TEXT NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_PHOTO_REFERENCE + " TEXT NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_WIDTH + " INTEGER NOT NULL, " +
                RestaurantGuideContract.RestaurantEntry.COLUMN_HEIGHT + " INTEGER NOT NULL );";

        return SQL_CREATE_RESTAURANT_TABLE;
    }

    public String createTableDetailScript() {
        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + RestaurantGuideContract.DetailsEntry.TABLE_NAME +
                " ( " + RestaurantGuideContract.DetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RestaurantGuideContract.DetailsEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                RestaurantGuideContract.DetailsEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                RestaurantGuideContract.DetailsEntry.COLUMN_CONTACT_NO + " TEXT NOT NULL, " +
                RestaurantGuideContract.DetailsEntry.COLUMN_TIMINGS + " TEXT NOT NULL, " +
                RestaurantGuideContract.DetailsEntry.COLUMN_WEBSITE + " TEXT NOT NULL );";

        return SQL_CREATE_DETAIL_TABLE;
    }

    public String createTableReviewScript() {
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + RestaurantGuideContract.ReviewEntry.TABLE_NAME +
                " ( " + RestaurantGuideContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_PROFILE_PHOTO_URL + " TEXT NOT NULL, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_RATING + " REAL NOT NULL, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_RELATIVE_TIME_DESCRIPTION + " TEXT NOT NULL, " +
                RestaurantGuideContract.ReviewEntry.COLUMN_TEXT + " TEXT NOT NULL );";
        return SQL_CREATE_REVIEW_TABLE;
    }
}
