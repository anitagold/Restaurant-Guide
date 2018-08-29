package com.example.android.restaurantguide.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestProvider extends AndroidTestCase {

    public void testRestaurantInsert() {
        ContentValues testValues = new ContentValues();
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_PLACE_ID, "test place");
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_RATING, 3.3f);
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_REFERENCE, "test reference");
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_RESTAURANT_NAME, "test name");
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_HEIGHT, 690);
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_WIDTH, 1290);
        testValues.put(RestaurantGuideContract.RestaurantEntry.COLUMN_PHOTO_REFERENCE, "http://test.url.hu");

        SQLiteDatabase db = new RestaurantGuideDbHelper(this.mContext).getWritableDatabase();


        long restaurantRowId = db.insertOrThrow(RestaurantGuideContract.RestaurantEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert RestaurantEntry into the Database", restaurantRowId != -1);

        String moviesWhereClause = RestaurantGuideContract.RestaurantEntry._ID + " = ? ";

        Cursor restCursor = mContext.getContentResolver().query(RestaurantGuideContract.RestaurantEntry.CONTENT_URI
                , null
                , moviesWhereClause
                , new String[]{Long.toString(restaurantRowId)}
                , null);

        TestUtilities.validateCursor("testBasicQuery", restCursor, testValues);
        db.close();
    }


    public void testReviewInsert() {
        ContentValues testValues = new ContentValues();
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_PLACE_ID, "test place");
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_AUTHOR_NAME, "test author");
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_PROFILE_PHOTO_URL, "http://testplace.jpg");
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_RATING, "3.3");
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_RELATIVE_TIME_DESCRIPTION, "4 days before");
        testValues.put(RestaurantGuideContract.ReviewEntry.COLUMN_TEXT, "not too good restaurant");

        SQLiteDatabase db = new RestaurantGuideDbHelper(this.mContext).getWritableDatabase();


        long reviewRowId = db.insertOrThrow(RestaurantGuideContract.ReviewEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert RestaurantEntry into the Database", reviewRowId != -1);

        String moviesWhereClause = RestaurantGuideContract.ReviewEntry.COLUMN_PLACE_ID + " = ? ";

        Cursor restCursor = mContext.getContentResolver().query(RestaurantGuideContract.ReviewEntry.buildReviewUri("test place")
                , null
                , moviesWhereClause
                , new String[]{"test string"}
                , null);

        TestUtilities.validateCursor("testBasicQuery", restCursor, testValues);
        db.close();
    }

    public void testDetailsInsert() {
        ContentValues testValues = new ContentValues();
        testValues.put(RestaurantGuideContract.DetailsEntry.COLUMN_PLACE_ID, "test place");
        testValues.put(RestaurantGuideContract.DetailsEntry.COLUMN_CONTACT_NO, "1111111111");
        testValues.put(RestaurantGuideContract.DetailsEntry.COLUMN_ADDRESS, "Test Address nr 11");
        testValues.put(RestaurantGuideContract.DetailsEntry.COLUMN_TIMINGS, "10.00AM to 10.00PM");

        SQLiteDatabase db = new RestaurantGuideDbHelper(this.mContext).getWritableDatabase();

        long detailRowId = db.insertOrThrow(RestaurantGuideContract.DetailsEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert RestaurantEntry into the Database", detailRowId != -1);

        String moviesWhereClause = RestaurantGuideContract.DetailsEntry.COLUMN_PLACE_ID + " = ? ";

        Cursor restCursor = mContext.getContentResolver().query(RestaurantGuideContract.DetailsEntry.buildDetailUri("g7wg73wkufj")
                , null
                , moviesWhereClause
                , new String[]{"test string"}
                , null);

        TestUtilities.validateCursor("testBasicQuery", restCursor, testValues);
        db.close();
    }
}
