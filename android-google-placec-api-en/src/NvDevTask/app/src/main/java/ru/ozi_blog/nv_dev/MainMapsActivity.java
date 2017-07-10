package ru.ozi_blog.nv_dev;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.ozi_blog.nv_dev.location.LocationProvider;
import ru.ozi_blog.nv_dev.photo.simply.CollageCreatorFactory;
import ru.ozi_blog.nv_dev.photo.CollageListener;
import ru.ozi_blog.nv_dev.photo.CollageMaker;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * The main activity of application. It shows map and current user location. If user
 * click on FAB it starts collage creating. User can choose collage type and photo
 * search radius.
 */
public class MainMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        CollageListener, LocationListener {
    // search radius values
    private static final int RADIUS_LEAST   = 100;
    private static final int RADIUS_LITTLE  = 1000;
    private static final int RADIUS_LARGE   = 10000;
    private static final int RADIUS_LARGEST = 100000;
    // out of range value
    private static final int OUT_OF_RANGE = -1;
    // default search radius
    private static final int DEFAULT_RADIUS = RADIUS_LITTLE;
    // Google map
    private GoogleMap mMap = null;
    // marker on map
    private Marker mMarker = null;
    // last known location
    private Location mLocation = null;
    // making collage progress dialog
    private ProgressDialog progressDialog = null;
    // current search radius
    private int mRadius = DEFAULT_RADIUS;
    // current collage type
    private CollageCreatorFactory.CollageType mCollageType
            = CollageCreatorFactory.CollageType.VERTICAL;
    // checked radius menu item
    private int mRadiusItemId = OUT_OF_RANGE;
    // checked collage menu item
    private int mCollageItemId = OUT_OF_RANGE;
    // current zoom on Google map
    private float mCameraZoom = OUT_OF_RANGE;
    // the used LocationProvider
    private LocationProvider mLocationProvider;
    // factory method for creating CollageCreators
    private CollageCreatorFactory mCreatorFactory = new CollageCreatorFactory(10, Color.WHITE);
    // tag for logging
    private static final String TAG = MainMapsActivity.class.getSimpleName();
    /**
     * Creating menu.
     *
     * @param menu menu for creation.
     * @return true if menu created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        // sets stored radius radio button state
        if (mRadiusItemId != OUT_OF_RANGE) {
            MenuItem radiusItem = menu.findItem(mRadiusItemId);
            if (radiusItem != null) {
                radiusItem.setChecked(true);
            }
        }
        // sets stored collage type radio utton state
        if (mCollageItemId != OUT_OF_RANGE) {
            MenuItem collageItem = menu.findItem(mCollageItemId);
            if (collageItem != null) {
                collageItem.setChecked(true);
            }
        }
        return true;
    }

    /**
     * Called when user clicks on a menu item.
     * @param item the item that has been clicked.
     * @return true if action has been processed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection;
        Log.d(TAG, "Change menu state.");
        // if a radius radio button checked
        boolean radiusItem = false;
        // if a collage type radio button checked
        boolean collageItem = false;
        switch (item.getItemId()) {
            case R.id.radius_least:
                mRadius = RADIUS_LEAST;
                radiusItem = true;
                break;
            case R.id.radius_little:
                mRadius = RADIUS_LITTLE;
                radiusItem = true;
                break;
            case R.id.radius_large:
                mRadius = RADIUS_LARGE;
                radiusItem = true;
                break;
            case R.id.radius_largest:
                mRadius = RADIUS_LARGEST;
                radiusItem = true;
                break;
            case R.id.horizontal:
                mCollageType = CollageCreatorFactory.CollageType.HORIZONTAL;
                collageItem = true;
                break;
            case R.id.vertical:
                mCollageType = CollageCreatorFactory.CollageType.VERTICAL;
                collageItem = true;
                break;
            case R.id.rectangle:
                mCollageType = CollageCreatorFactory.CollageType.RECTANGLE;
                collageItem = true;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        // getting checked items
        if (radiusItem)
            mRadiusItemId = item.getItemId();
        if(collageItem)
            mCollageItemId = item.getItemId();
        // sets checked radio button
        item.setChecked(true);
        return true;
    }

    /**
     * Saves instance of the activity.
     *
     * @param bundle the Bundle object.
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(getString(R.string.main_save_radius_state), mRadius);
        bundle.putInt(getString(R.string.main_save_radius_id_state), mRadiusItemId);
        bundle.putInt(getString(R.string.main_save_collage_type_id_state), mCollageItemId);
        bundle.putInt(getString(R.string.main_save_collage_type_state), mCollageType.ordinal());
        if (mLocation != null) {
            bundle.putDouble(getString(R.string.main_save_latitude_state),
                    mLocation.getLatitude());
            bundle.putDouble(getString(R.string.main_save_longitude_state),
                    mLocation.getLongitude());
        }
    }

    /**
     * Restores instance of the activity.
     *
     * @param bundle the Bundle object.
     */
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            mRadius = bundle.getInt(getString(R.string.main_save_radius_state));
            mRadiusItemId = bundle.getInt(getString(R.string.main_save_radius_id_state));
            mCollageItemId = bundle.getInt(getString(R.string.main_save_collage_type_id_state));
            mCollageType = CollageCreatorFactory.CollageType
                    .values()[bundle.getInt(getString(R.string.main_save_collage_type_state))];
            mLocation = new Location("");
            mLocation.setLatitude(bundle.getDouble(getString(R.string.main_save_latitude_state)));
            mLocation.setLongitude(bundle.getDouble(getString(R.string.main_save_longitude_state)));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        // starts location provider
        mLocationProvider.connect();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        // stops location provider
        mLocationProvider.disconnect();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // creation of the activity
        super.onCreate(savedInstanceState);
        mLocationProvider = new LocationProvider(this, this);
        setContentView(R.layout.activity_google_maps);
        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // getting fab button
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton plusFab = myFab;
        // fab starts creation of a collage when clicked
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // if location not found
                if(mLocation == null) {
                    showCanNotFindLocationDialog();
                    return;
                }
                // creation of collage maker
                new CollageMaker(mCreatorFactory.getCreator(mCollageType),
                        mLocation, MainMapsActivity.this, mRadius,
                        getString(R.string.google_place_key)).makeCollage();
                // sets fab invisibility
                plusFab.setVisibility(View.INVISIBLE);
                // running a progress dialog
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MainMapsActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage(getString(R.string.progress_dialog_making_collage));
                    progressDialog.show();
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // if location was found
        if(mLocation != null) {
            onLocationChanged(mLocation);
            return;
        }
        /* If location not found creates async task.
           It runs progress dialog and checks mLocation state
           if after 3 sec a device location not found than
           alert dialog will be showed.*/
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog progressDialog = null;
            // 100 * 100 = 10 000 ms = 3 sec
            private int SLEEP_MLS   = 100;
            private int SLEEP_COUNT = 100;
            // open progress dialog
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.progressDialog = new ProgressDialog(MainMapsActivity.this);
                this.progressDialog.setCanceledOnTouchOutside(false);
                this.progressDialog.setMessage(getString(R.string.progress_dialog_searching_location));
                this.progressDialog.show();
            }
            // checks mLocation state
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    for(int i = 0; i < SLEEP_COUNT; i++) {
                        // sleeping 100 ms
                        Thread.sleep(SLEEP_MLS);
                        // if location found
                        if(mLocation != null) {
                            return true;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "async task can not sleep.");
                }
                // if location not found
                return false;
            }
            // checks result
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                // close progress dialog
                this.progressDialog.dismiss();
                // if location not found
                if(!result) {
                    // showing alert dialog
                    showCanNotFindLocationDialog();
                }
            }
        };
        task.execute();
    }

    /**
     * Called when CollageMaker can not make collage.
     */
    @Override
    public void onCollageMakeFail() {
        Log.d(TAG, "Can not create collage.");
        // show alert dialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_can_not_make_title))
                .setMessage(getString(R.string.dialog_can_not_make_message))
                .setPositiveButton(getString(R.string.dialog_ok_button),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
        // sets fab visible
        final FloatingActionButton plus = (FloatingActionButton) findViewById(R.id.fab);
        plus.setVisibility(View.VISIBLE);
        // stops progress dialog
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Called when a collage has been created by CollageMaker.
     * @param collage the collage that has been created.
     */
    @Override
    public void onCollageMake(Bitmap collage) {
        Log.d(TAG, "Collage has been created.");
        FileOutputStream outputStream = null;
        try {
            // writing collage into file
            File imagePath = new File(getCacheDir(), "");
            File newFile = new File(imagePath, getString(R.string.collage_cache_file));
            outputStream = new FileOutputStream(newFile);
            collage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            // getting URI via Provider
            Uri contentUri = getUriForFile(this, getString(R.string.image_provider), newFile);
            // starting Collage CollageActivity
            Intent intent = new Intent(this, CollageActivity.class);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri.toString());
            startActivity(intent);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Can not open a temporary file for writing collage.");
        } finally {
            try {
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException | NullPointerException e) {
                Log.d(TAG, "Can not a save collage content in the file.");
            }
        }
        // sets fab visible
        final FloatingActionButton plus = (FloatingActionButton) findViewById(R.id.fab);
        plus.setVisibility(View.VISIBLE);
        // stops progress dialog
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Called when device location state changed.
     *
     * @param location the device location.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed.");
        mLocation = location;
        if(mMap != null) {
            // clear old markers
            if (mMarker != null)
                mMarker.remove();
            mMap.clear();
            // creating marker
            LatLng latLng = new LatLng(mLocation.getLatitude()
                    , mLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(getString(R.string.marker_current_position));
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMarker = mMap.addMarker(markerOptions);
            //move map camera
            if (mCameraZoom == OUT_OF_RANGE) {
                mCameraZoom = 11;
            } else {
                mCameraZoom = mMap.getCameraPosition().zoom;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mCameraZoom));
        } else {
            Log.d(TAG, "Map is not ready.");
        }
    }

    /**
     * Creates dialog which informs the user about disabled Location Service.
     */
    private void showCanNotFindLocationDialog() {
        new AlertDialog.Builder(MainMapsActivity.this)
                .setTitle(getString(R.string.dialog_can_not_find_location_title))
                .setMessage(getString(R.string.dialog_can_not_find_location_message))
                .setPositiveButton(getString(R.string.dialog_ok_button),
                        new DialogInterface.OnClickListener() {
                            // finish the activity if user press OK button
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainMapsActivity.this.finish();
                            }
                        })
                .create()
                .show();
    }
}
