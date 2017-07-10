package ru.ozi_blog.nv_dev.net;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
/**
 * An asynchronous task that using Google Places Api Web Service to getting
 * references of photos that are nearby of a specified location.
 */
public final class GetPhotoReferencesTask
        extends AsyncTask<Location, Void, List<String>> {
    // base url for Google Places Api Web Service
    private static final String PLACES_API_BASE_URL
            = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    // location parameter for query
    private static final String LOCATION_PARAM = "location";
    // searching places radius parameter for query
    private static final String RADIUS_PARAM   = "radius";
    // Google Places Api key parameter for query
    private static final String KEY_PARAM      = "key";

    // results tag for parsing result by JSON
    private static final String RESULT         = "results";
    // photos tag for parsing result by JSON
    private static final String PHOTOS         = "photos";
    // photo_reference tag for parsing result by JSON
    private static final String REFERENCE      = "photo_reference";

    // tag for logging
    private static final String  TAG = GetPhotoReferencesTask.class.getSimpleName();

    // listener that called when list with photo references has been created
    private final PhotoReferencesListener mListener;
    // radius for search places and photos
    private final int                     mRadius;
    // Google Places Api key
    private final String                  mApiKey;

    /**
     * Constructor create the task.
     *
     * @param radius radius for photo search.
     * @param apiKey Google Places Api key.
     * @param listener listener that called when list with photo references
     *                 has been created.
     */
    public GetPhotoReferencesTask(final int radius, final String apiKey,
                                  final PhotoReferencesListener listener) {
        mRadius   = radius;
        mListener = listener;
        mApiKey   = apiKey;
    }

    /**
     * Background task that get photo references nearby places of the locations.
     *
     * @param locations list of locations.
     * @return list of photo reference of nearby places.
     */
    @Override
    protected List<String> doInBackground(Location... locations) {
        // list of photo references
        List<String> photoReferences = new LinkedList<>();
        // buffer for reading data from response
        StringBuilder buffer = new StringBuilder();
        // for all locations
        for(Location location : locations) {
            // make query for Google Places Api
            Uri uri = Uri.parse(PLACES_API_BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM,
                            String.valueOf(location.getLatitude())
                            + "," + String.valueOf(location.getLongitude()))
                    .appendQueryParameter(RADIUS_PARAM,
                            Integer.toString(mRadius))
                    .appendQueryParameter(KEY_PARAM, mApiKey)
                    .build();
            URL url;
            // transform Uri to URL
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                return null;
            }

            Log.d(TAG, "URL for getting photo references: " + url.toString());
            BufferedReader reader = null;
            // read data from URL
            try {
                URLConnection urlConnection = url.openConnection();
                InputStream stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while((line = reader.readLine()) != null)
                    buffer.append(line);
            } catch(IOException e) {
                reader = null;
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // if can not read data
            if(reader == null)
                return null;
            Log.d(TAG, "Full response: " + buffer.toString());
            // parse response
            try {
                JSONObject json = new JSONObject(buffer.toString());
                // getting all "result"
                JSONArray jPlaces = json.getJSONArray(RESULT);
                for(int i = 0; i < jPlaces.length(); i++) {
                    // searching "photos tag"
                    if (!jPlaces.getJSONObject(i).isNull(PHOTOS)) {
                        JSONArray photos = jPlaces.getJSONObject(i)
                                .getJSONArray(PHOTOS);
                        // getting photos references
                        for (int j = 0; j < photos.length(); j++) {
                            photoReferences.add(photos.getJSONObject(j)
                                    .getString(REFERENCE));
                        }
                    }
                }
            } catch (JSONException e) {
                // if any exception
                Log.d(TAG, "Exception when parsing response:\n" + e.toString());
                return null;
            }
        }
        return photoReferences;
    }

    /**
     * Called after execute background task doInBackground.
     *
     * @param result gotten photo references.
     */
    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        // if has listener
        if(mListener != null) {
            // call listener's method
            mListener.onPhotoReferencesGet(result);
        }
    }
}
