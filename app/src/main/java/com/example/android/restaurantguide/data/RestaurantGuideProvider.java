package com.example.android.restaurantguide.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.support.annotation.Nullable;

public class RestaurantGuideProvider extends ContentProvider {

    private static final int RESTAURANT = 100;
    //private static final int PHOTO = 200;
    //private static final int PHOTO_WITH_PLACEID = 201;
    private static final int REVIEW = 300;
    private static final int REVIEW_WITH_PLACEID = 301;
    private static final int PLACE_DETAILS = 400;
    private static final int PLACE_DETAILS_WITH_PLACE_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String sDetailsSelection =
            RestaurantGuideContract.DetailsEntry.TABLE_NAME +
                    "." + RestaurantGuideContract.DetailsEntry.COLUMN_PLACE_ID + " = ?";
    private static final String sReviewsSelection =
            RestaurantGuideContract.ReviewEntry.TABLE_NAME +
                    "." + RestaurantGuideContract.ReviewEntry.COLUMN_PLACE_ID + " = ?";
    private RestaurantGuideDbHelper mDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RestaurantGuideContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RestaurantGuideContract.RESTAURANTS, RESTAURANT);
        //matcher.addURI(authority, RestaurantGuideContract.PATH_PLACE_PHOTOS, PHOTO);
        matcher.addURI(authority, RestaurantGuideContract.REVIEW, REVIEW);
        matcher.addURI(authority, RestaurantGuideContract.DETAILS, PLACE_DETAILS);
        matcher.addURI(authority, RestaurantGuideContract.DETAILS + "/*", PLACE_DETAILS_WITH_PLACE_ID);
        //matcher.addURI(authority, RestaurantGuideContract.PATH_PLACE_PHOTOS + "/*", PHOTO_WITH_PLACEID);
        matcher.addURI(authority, RestaurantGuideContract.REVIEW + "/*", REVIEW_WITH_PLACEID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case RESTAURANT:
                return RestaurantGuideContract.RestaurantEntry.CONTENT_TYPE;
            /*case PHOTO_WITH_PLACEID:
                return RestaurantGuideContract.PhotoEntry.CONTENT_ITEM_TYPE;*/
            case REVIEW_WITH_PLACEID:
                return RestaurantGuideContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case PLACE_DETAILS_WITH_PLACE_ID:
                return RestaurantGuideContract.DetailsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RestaurantGuideDbHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case RESTAURANT: {
                rowsUpdated = db.update(RestaurantGuideContract.RestaurantEntry.TABLE_NAME, values
                        , selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(RestaurantGuideContract.ReviewEntry.TABLE_NAME, values
                        , selection, selectionArgs);
                break;
            }
            case PLACE_DETAILS: {
                rowsUpdated = db.update(RestaurantGuideContract.DetailsEntry.TABLE_NAME, values
                        , selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case RESTAURANT:
                cursor = mDbHelper.getReadableDatabase().query(
                        RestaurantGuideContract.RestaurantEntry.TABLE_NAME,
                        projection, selection, selectionArgs,null,null, sortOrder);
                break;

            case REVIEW_WITH_PLACEID:
                cursor = getReviewsForPlaceId(uri, projection);
                break;

            case PLACE_DETAILS_WITH_PLACE_ID:
                cursor = getPlaceDetailsForPlaceId(uri, projection);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;


        switch (sUriMatcher.match(uri)) {
            case RESTAURANT: {
                rowsDeleted = db.delete(RestaurantGuideContract.RestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case REVIEW: {
                rowsDeleted = db.delete(RestaurantGuideContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case PLACE_DETAILS: {
                rowsDeleted = db.delete(RestaurantGuideContract.DetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)) {
            case RESTAURANT: {
                long _id = db.insert(RestaurantGuideContract.RestaurantEntry.TABLE_NAME, null, values);

                if (_id > 0) {
                    returnUri = RestaurantGuideContract.RestaurantEntry.buildRestaurantUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case REVIEW: {
                long _id = db.insert(RestaurantGuideContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = RestaurantGuideContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case PLACE_DETAILS: {
                long _id = db.insert(RestaurantGuideContract.DetailsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = RestaurantGuideContract.DetailsEntry.buildDetailUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri = null;
        int returnCount = 0;

        switch (sUriMatcher.match(uri)) {

            case RESTAURANT: {
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RestaurantGuideContract.RestaurantEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }

            case REVIEW: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RestaurantGuideContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }

            case PLACE_DETAILS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RestaurantGuideContract.DetailsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnCount;
    }


    public Cursor getReviewsForPlaceId(Uri uri, String[] projection) {
        String idFromUri = RestaurantGuideContract.getPlaceIdFromUri(uri);
        Cursor cursor;
        cursor = mDbHelper.getReadableDatabase().query(
                RestaurantGuideContract.ReviewEntry.TABLE_NAME,
                projection, sReviewsSelection,
                new String[]{idFromUri},null,null,null);
        return cursor;
    }

    public Cursor getPlaceDetailsForPlaceId(Uri uri, String[] projection) {
        String idFromUri = RestaurantGuideContract.getPlaceIdFromUri(uri);
        Cursor cursor;

        cursor = mDbHelper.getReadableDatabase().query(
                RestaurantGuideContract.DetailsEntry.TABLE_NAME,
                projection, sDetailsSelection,
                new String[]{idFromUri},null,null,null);
        return cursor;
    }
}
