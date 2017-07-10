package ru.ozi_blog.nv_dev.photo;

import android.graphics.Bitmap;

/**
 * Interface definition for a callback to be invoked when a collage has
 * been created.
 */
public interface CollageListener {
    /**
     * Called when a collage can not has been created.
     */
    void onCollageMakeFail();

    /**
     * Called when a collage has been created.
     *
     * @param collage collage that has been created.
     */
    void onCollageMake(Bitmap collage);
}
