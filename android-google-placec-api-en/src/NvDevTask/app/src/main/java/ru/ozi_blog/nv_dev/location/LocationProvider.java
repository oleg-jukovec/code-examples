package ru.ozi_blog.nv_dev.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * The class provides location functionality: getting last location and location
 * changes through GoogleApiClient.
 *
 * Using {@link #connect()} and {@link #disconnect()} provider can stop updates of
 * the device location. It can be used on onSaveInstanceState and onRestoreInstanceState
 * by activity.
 */
public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    // 10 seconds, in milliseconds
    private final static int INTERVAL = 10000;
    // 1 second, in milliseconds
    private final static int FASTEST_INTERVAL = 1000;
    // a location request priority
    private final static int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    // tag for logging
    private static final String TAG = LocationProvider.class.getSimpleName();
    // listener for location changed callbacks
    private LocationListener mLocationCallback;
    // Context of the object
    private Context mContext;
    // Google Api Client
    private GoogleApiClient mGoogleApiClient;
    // location request for Google Api Client
    private LocationRequest mLocationRequest;

    /**
     * The constructor creates object. To start location updates call
     * {@link #connect()}
     *
     * @param context the context of the provider
     * @param callback the listener for location changed callbacks.
     */
    public LocationProvider(Context context, LocationListener callback) {
        // creating the Google Api client
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationCallback = callback;

        // creating the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(PRIORITY)
                .setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        mContext = context;
    }
    /**
     * {@inheritDoc}
     */
    public void connect() {
        Log.i(TAG, "Location services connect.");
        mGoogleApiClient.connect();
    }
    /**
     * {@inheritDoc}
     */
    public void disconnect() {
        Log.i(TAG, "Location services disconnect.");
        // disconnecting from services
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Returns a last known location via System Services.
     *
     * @param onlyEnabled if true than will be asked only enabled services.
     * @return a last known location via System Services.
     */
    public Location getLastKnownLocation(boolean onlyEnabled) {
        LocationManager manager =
                (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location utilLocation = null;
        // getting providers list
        List<String> providers = manager.getProviders(onlyEnabled);
        for(String provider : providers) {
            // getting last known location
            try {
                utilLocation = manager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                Log.d(TAG, "Can not get last known location by " + provider);
            }
            if (utilLocation != null)
                return utilLocation;
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        try {
            // getting last known location
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                mLocationCallback.onLocationChanged(location);
            }
        } catch(SecurityException e) {
            Log.i(TAG, "Not enough permission.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services connection failed.");
    }

    /**
     * Called when a device location changed.
     * @param location new location.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed.");
        // transfer to listener
        mLocationCallback.onLocationChanged(location);
    }
}
