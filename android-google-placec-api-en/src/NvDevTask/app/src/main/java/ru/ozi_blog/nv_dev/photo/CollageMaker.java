package ru.ozi_blog.nv_dev.photo;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ru.ozi_blog.nv_dev.net.GetPhotoTask;
import ru.ozi_blog.nv_dev.net.GetPhotoReferencesTask;
import ru.ozi_blog.nv_dev.net.PhotoReferencesListener;
import ru.ozi_blog.nv_dev.net.PhotoDownloadListener;

/**
 * This class is intended for creation a collage through
 * {@link ru.ozi_blog.nv_dev.photo.CollageCreator}. Object of the class download
 * photos using Google Places Api Web Services that nearby of preassigned location
 * by background tasks and create collage from them. Created collage can
 * be obtained with {@link ru.ozi_blog.nv_dev.photo.CollageListener}.
 */
public class CollageMaker implements PhotoReferencesListener,
        PhotoDownloadListener {
    // Google Places Api key
    private final String            mApiKey;
    // task for downloading photo
    private List<GetPhotoTask>      mDownloadPhotoTasks;
    // downloaded photos
    private List<Bitmap>            mPhotos;
    // the location near which the search is performed
    private Location                mLocation;
    // radius for the photos search
    private int                     mRadius;
    // flag that indicates if photo download started
    private boolean                 mDownloadStarted;
    // listener for getting collage
    private CollageListener         mListener;
    // creator that used for collage creation
    private CollageCreator          mCreator;

    // tag for logging
    private static final String  TAG = CollageMaker.class.getSimpleName();

    /**
     * Constructor creates object with certain arguments.
     *
     * @param creator that will be used for collage creation.
     * @param location near which the search is performed.
     * @param listener for getting collage.
     * @param radius for the photos search.
     * @param apiKey Google Places Api key.
     */
    public CollageMaker(final CollageCreator creator, final Location location,
                        final CollageListener listener, final int radius,
                        final String apiKey) {
        mCreator  = creator;
        mLocation = location;
        mListener = listener;
        mRadius = radius;
        mApiKey = apiKey;
        // creating lists
        mPhotos = new LinkedList<>();
        mDownloadPhotoTasks = new LinkedList<>();
        // download not started
        mDownloadStarted = false;
    }

    /**
     * The method start making collage. Result can be obtained with
     * {@link ru.ozi_blog.nv_dev.photo.CollageListener}.
     */
    public void makeCollage() {
        // if download not start
        if(!mDownloadStarted) {
            // start a task
            mDownloadStarted = true;
            mDownloadPhotoTasks.clear();
            // getting photo references nearby mLocation
            new GetPhotoReferencesTask(mRadius, mApiKey, this)
                    .execute(mLocation);
        } else {
            // if creating has been started
            mListener.onCollageMakeFail();
        }
    }

    /**
     * Returns true if creation of the collage has been started
     * or false.
     *
     * @return true if creation of the collage has been started
     * or false.
     */
    public boolean isCreationStarted() {
        return mDownloadStarted;
    }
    /**
     * Called when photos references has been gotten.
     *
     * @param photoReferences that has been gotten.
     */
    @Override
    public void onPhotoReferencesGet(List<String> photoReferences) {
        // if photos references exist and their count suitable for creator
        if (photoReferences != null
                && photoReferences.size() >= mCreator.needPhotos()) {
            // print debug photo references
            for (String photoReference : photoReferences)
              Log.d(TAG, "Gotten photo reference: " + photoReference);
            // creating photo download tasks
            // random number generator
            Random random = new Random();
            // while count of tasks less than needed by creator
            for (int count = 0; count != mCreator.needPhotos(); count++) {
                // get number of photo reference
                int number = random.nextInt(photoReferences.size());
                // create download task
                GetPhotoTask task = new GetPhotoTask(
                        mCreator.getMaxPhotoHeight(),
                        mCreator.getMaxPhotoWidth(), mApiKey, this);
                mDownloadPhotoTasks.add(task);
                // run download task
                task.execute(photoReferences.get(number));
                // remove reference from task
                photoReferences.remove(number);
            }
        } else {
            // if photo references list does not exist or their size is not enough.
            Log.d(TAG, "Download photos starting error: not enough photo references.");
            mListener.onCollageMakeFail();
        }
    }

    /**
     * Called when a separate photo has been downloaded.
     *
     * @param photo that has been downloaded.
     */
    @Override
    public void onPhotoDownloaded(Bitmap photo) {
        // if photo gotten when download started - that OK
        if(mDownloadStarted) {
            Log.d(TAG, "Photo gotten.");
            // add photo to list
            mPhotos.add(photo);
            // if photo references count suitable
            if(mPhotos.size() == mCreator.needPhotos()) {
                Log.d(TAG, "Collage creation.");
                // try to create collage from photos
                try {
                    mListener.onCollageMake(mCreator.createCollage(mPhotos));
                } catch(Exception e) {
                    Log.d(TAG, "Collage can not has been created.");
                    // if any exceptions - can not create collage
                    mListener.onCollageMakeFail();
                }
                mDownloadStarted = false;
            }
        } else {
            // if photo gotten when download not started
            for(GetPhotoTask task : mDownloadPhotoTasks)
                task.cancel(false);
            Log.d(TAG, " wrong get photo sequence");
        }
    }
}
