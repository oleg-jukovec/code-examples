package ru.ozi_blog.nv_dev.net;

import android.graphics.Bitmap;

/**
 * Interface definition for a callback to be invoked when an image is downloaded.
 */
public interface PhotoDownloadListener {
    /**
     * Called when an photo has been downloaded.
     *
     * @param photo photo that has been downloaded.
     */
    void onPhotoDownloaded(final Bitmap photo);
}
