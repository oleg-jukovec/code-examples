package ru.ozi_blog.nv_dev.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An asynchronous task that using Google Places Api Web Service to getting
 * a photo by it Google photo reference.
 */
public final class GetPhotoTask extends AsyncTask<String, Integer, Bitmap> {
    // base url for Google Places Api Web Service
    private static final String  PHOTO_API_BASE_URL
            = "https://maps.googleapis.com/maps/api/place/photo?";
    // max photo width parameter for query
    private static final String  MAX_WIDTH_PARAM  = "maxwidth";
    // max photo height parameter for query
    private static final String  MAX_HEIGHT_PARAM = "maxheight";
    // photo reference parameter for query
    private static final String  REFERENCE_PARAM  = "photoreference";
    // Google Api Places key parameter for query
    private static final String  KEY_PARAM        = "key";
    // tag for logging
    private static final String  TAG = GetPhotoTask.class.getSimpleName();
    // max height of a photo that has been gotten
    private final int                   mMaxHeight;
    // max width of a photo that has been gotten
    private final int                   mMaxWidth;
    // listener that called when photo has been downloaded
    private final PhotoDownloadListener mListener;
    // Google Places API key
    private final String                mApiKey;

    /**
     * Constructor that creates task.
     *
     * @param maxHeight maximum height of a photo that has been gotten.
     * @param maxWidth maximum width of a photo that has been gotten.
     * @param apiKey a Google Places API key.
     * @param listener listener that called when photo has been downloaded.
     */
    public GetPhotoTask(final int maxHeight, final int maxWidth, final String apiKey,
                        final PhotoDownloadListener listener) {
        mMaxHeight = maxHeight;
        mMaxWidth  = maxWidth;
        mApiKey    = apiKey;
        mListener  = listener;
    }

    /**
     * Background thread which download a photo by his photo reference
     * using Google Places Api Web Service.
     *
     * @param photoReferences references of photos, used only first item
     *                        in array.
     * @return downloaded image.
     */
    @Override
    protected Bitmap doInBackground(String... photoReferences) {
        // making query for Google Places Api Web Service
        Uri uri = Uri.parse(PHOTO_API_BASE_URL).buildUpon()
                .appendQueryParameter(MAX_WIDTH_PARAM,
                        Integer.toString(mMaxWidth))
                .appendQueryParameter(MAX_HEIGHT_PARAM,
                        Integer.toString(mMaxHeight))
                .appendQueryParameter(REFERENCE_PARAM, photoReferences[0])
                .appendQueryParameter(KEY_PARAM, mApiKey)
                .build();
        // converting Uri to URL
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.d(TAG, "Can not create url:\n" + e.toString());
        }
        if(url != null) {
            Log.d(TAG, "Photo url: " + url.toString());
        } else {
            // if can not convert
            return null;
        }
        // getting image
        Bitmap bitmap = null;
        try{
            // starting image download
            bitmap = downloadImage(url);
        }catch(Exception e){
            Log.d(TAG, "Can not download image:\n" + e.toString());
        }
        // return downloaded image
        return bitmap;
    }

    /**
     * Called after execute background task doInBackground.
     *
     * @param result a downloaded image.
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        // if has listener
        if(mListener != null) {
            // call listener's method
            mListener.onPhotoDownloaded(result);
        }
    }

    /**
     * Supporting method that helps download a photo.
     *
     * @param url URL of a photo.
     * @return photo that downloaded.
     * @throws IOException when can not download photo.
     */
    private Bitmap downloadImage(final URL url) throws IOException {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            // creating an http connection to communicate with url
            HttpURLConnection urlConnection
                    = (HttpURLConnection) url.openConnection();
            // connecting to url
            urlConnection.connect();
            // stream for reading data from url
            inputStream = urlConnection.getInputStream();
            // creating a bitmap from the stream returned from the url
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch(Exception e) {
            Log.d(TAG, "Download exception " + e.toString());
            throw e;
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return bitmap;
    }
}
