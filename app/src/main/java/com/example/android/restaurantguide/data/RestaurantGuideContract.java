package com.example.android.restaurantguide.data;

import android.net.Uri;

import android.content.ContentResolver;
import android.content.ContentUris;

import android.provider.BaseColumns;

public class RestaurantGuideContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.restaurantguide.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String RESTAURANTS = "restaurants";
    public static final String DETAILS = "details";
    public static final String REVIEW = "review";

    public static final class RestaurantEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(RESTAURANTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + RESTAURANTS;

        //public static final String CONTENT_ITEM_TYPE =
        //        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + RESTAURANTS;

        public static final String TABLE_NAME = "restaurants";

        public static final String COLUMN_PLACE_ID = "place_id";

        public static final String COLUMN_RESTAURANT_NAME = "rest_name";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_REFERENCE = "reference";

        public static final String COLUMN_PHOTO_REFERENCE = "photo_reference";

        public static final String COLUMN_WIDTH = "width";

        public static final String COLUMN_HEIGHT = "height";

        public static Uri buildRestaurantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class DetailsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DETAILS).build();

        //public static final String CONTENT_TYPE =
        //        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DETAILS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DETAILS;

        public static final String TABLE_NAME = "details";

        public static final String COLUMN_PLACE_ID = "place_id";

        public static final String COLUMN_ADDRESS = "address";

        public static final String COLUMN_CONTACT_NO = "contact";

        public static final String COLUMN_TIMINGS = "timings";

        public static final String COLUMN_WEBSITE = "website";

        public static Uri buildDetailUri(String placeId) {
            return CONTENT_URI.buildUpon().appendPath(placeId).build();
        }

        public static Uri buildDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(REVIEW).build();

        //public static final String CONTENT_TYPE =
        //        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_PLACE_ID = "place_id";

        public static final String COLUMN_AUTHOR_NAME = "author_name";

        public static final String COLUMN_PROFILE_PHOTO_URL = "profile_photo";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_RELATIVE_TIME_DESCRIPTION = "time_of_review";

        public static final String COLUMN_TEXT = "text";

        public static Uri buildReviewUri(String placeId) {
            return CONTENT_URI.buildUpon().appendPath(placeId).build();
        }

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static String getPlaceIdFromUri(Uri uri) {
        return (uri.getPathSegments().get(1));
    }
}
