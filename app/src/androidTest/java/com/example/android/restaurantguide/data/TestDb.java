package com.example.android.restaurantguide.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Iterator;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        mContext.deleteDatabase(RestaurantGuideDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();

        tableNameHashSet.add(RestaurantGuideContract.RestaurantEntry.TABLE_NAME);
        // tableNameHashSet.add(RestaurantGuideContract.PhotoEntry.TABLE_NAME);
        tableNameHashSet.add(RestaurantGuideContract.DetailsEntry.TABLE_NAME);
        tableNameHashSet.add(RestaurantGuideContract.ReviewEntry.TABLE_NAME);

        deleteDatabase();

        SQLiteDatabase db = new RestaurantGuideDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        Iterator<String> iterator = tableNameHashSet.iterator();

        while (iterator.hasNext()) {
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            assertNotNull("Table does not exist: ", cursor);
            iterator.next();
            cursor.close();
        }

    }
}
