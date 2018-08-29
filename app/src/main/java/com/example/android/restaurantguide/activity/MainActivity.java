package com.example.android.restaurantguide.activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;

import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.content.IntentSender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.android.restaurantguide.R;
import com.example.android.restaurantguide.fragment.DetailsActivityFragment;
import com.example.android.restaurantguide.fragment.MainActivityFragment;
import com.example.android.restaurantguide.sync.PlacesPullIntentService;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MainActivityFragment.Callback {

    private static Location location;
    private GoogleApiClient myApiClient;
    private double latitudeGPS = 0.0;
    private double longitudeGPS = 0.0;
    private Intent intent;
    private boolean mTwoPane = false;
    @BindView(R.id.error)
    TextView errorTextview;
    private static final int REQUEST_ERROR = 1001;
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private static final int MY_FINE_LOCATION_PERMISSION_REQUEST_CODE = 101;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            if (isOnline()) {
                // network is avalable
                Log.i(LOG_TAG, "network is available");
                if (CheckGooglePlayServices()) {
                    if (myApiClient == null) {
                        myApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                    }

                } else {
                    //network not available
                    errorTextview.setText(getString(R.string.error_no_network));
                    errorTextview.setVisibility(View.VISIBLE);
                }
            } else {
                //handle errorTextview case when internet is not available
                errorTextview.setText(getString(R.string.error_no_network));
                errorTextview.setVisibility(View.VISIBLE);
            }
        }


        if (findViewById(R.id.rest_details_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rest_details_container, new DetailsActivityFragment(), DETAILFRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    protected void onStart() {
        if (myApiClient != null) {
            myApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (myApiClient != null) {
            myApiClient.disconnect();
        }
        super.onStop();
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "Google Play Services Connection Successfull");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(myApiClient);
            // need to check whether location is null!
            if (location != null) {
                // Good! handle new location
               latitudeGPS = location.getLatitude();
               longitudeGPS = location.getLongitude();
            }
            else {
                // Error! Location is null!
                Log.e(LOG_TAG, "Error occured! Location is null!");
            }

            if (latitudeGPS != 0.0 && longitudeGPS != 0.0) {
                String location = String.valueOf(latitudeGPS) + "," + String.valueOf(longitudeGPS);

                intent = new Intent(this, PlacesPullIntentService.class);
                intent.putExtra("current_location", location);
                Log.i(LOG_TAG, "Current location" + location);
                this.startService(intent);
            } else {
                Log.e(LOG_TAG, "Error occured! Cannot retrieve current location");
            }

        } else {
            // if no permission request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /* Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error. */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                // Thrown if Google Play services canceled the original PendingIntent
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /* If no resolution is available, display a dialog to the
             * user with the error. */
            Log.i(LOG_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Google Play Services Connection Suspended");
    }

    // check Google Play Services availability
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int available = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (available != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(available)) {
                googleApiAvailability.getErrorDialog(this, available,0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestcode) {
            case MY_FINE_LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        location = LocationServices.FusedLocationApi.getLastLocation(myApiClient);
                        if (location != null) {
                            // set coordinates
                            latitudeGPS = location.getLatitude();
                            longitudeGPS = location.getLongitude();
                        }

                        if (latitudeGPS != 0.0 && longitudeGPS != 0.0) {
                            String location = String.valueOf(latitudeGPS) + "," + String.valueOf(longitudeGPS);
                            intent = new Intent(this, PlacesPullIntentService.class);
                            intent.putExtra("current_location", location);
                            this.startService(intent);
                        } else {
                            Log.e(LOG_TAG, "Error occured ! Cannot retrieve current location");
                        }
                    }
                } else {
                    // location permission is denied
                    errorTextview.setText(getString(R.string.error_no_network));
                    errorTextview.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    public void onItemSelected(String placeId) {
        if (mTwoPane) {
            //two pane layout
            Bundle bundle = new Bundle();
            bundle.putString("placeId", placeId);

            DetailsActivityFragment activityFragment = new DetailsActivityFragment();
            activityFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rest_details_container, activityFragment, DETAILFRAGMENT_TAG).commit();
        } else {
            //one pane layout
            Intent detailsIntent = new Intent(this, DetailsActivity.class);
            detailsIntent.putExtra("placeId", placeId);
            startActivity(detailsIntent);
        }
    }
}
