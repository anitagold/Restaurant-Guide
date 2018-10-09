package com.example.android.restaurantguide.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.adapter.RestaurantListAdapter;
import com.example.android.restaurantguide.data.RestaurantGuideContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, RestaurantListAdapter.ClickListener {

    public MainActivityFragment() {
    }

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mGridLayoutManager;
    private final int COLUMN_COUNT = 2;
    private static final String LOG_TAG = "MAIN_ACTIVITY_FRAGMENT";
    Cursor mCursor;
    MainActivityFragment mainActivityFragment;
    public int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mainActivityFragment = this;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        if (savedInstanceState != null) {
            // Restore last state for position
            position = savedInstanceState.getInt("position", 0);
            Log.i(LOG_TAG, "On activity created: "+position);
            //((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position,0);
            mRecyclerView.scrollToPosition(position);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri getRestaurantListForLocation = RestaurantGuideContract.RestaurantEntry.CONTENT_URI;

        String sortOrder = RestaurantGuideContract.RestaurantEntry.COLUMN_RATING + " DESC";

        return new CursorLoader(getActivity(),
                getRestaurantListForLocation,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        RestaurantListAdapter adapter = new RestaurantListAdapter(data, getActivity(), mainActivityFragment);
        mRecyclerView.setAdapter(adapter);
        mGridLayoutManager = new GridLayoutManager(getActivity(), COLUMN_COUNT);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onItemClick(View view, int pos) {
        if (mCursor != null) {
            mCursor.moveToPosition(pos);
            String placeId = mCursor.getString(mCursor.getColumnIndex(RestaurantGuideContract.RestaurantEntry.COLUMN_PLACE_ID));
            ((Callback) getActivity()).onItemSelected(placeId);
        }
    }

    public interface Callback {
        public void onItemSelected(String placeId);
    }


    /*
     * Save the scroll position
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        savedInstanceState.putInt("position", position);
        Log.i(LOG_TAG, "Save position: "+position);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (position != 0) {
            Log.i(LOG_TAG, "On resume: "+position);
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position,0);

        }
    }

}
