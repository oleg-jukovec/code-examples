package ru.ozi_blog.nv_dev.net;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when a list of
 * Google Api photo references has been gotten.
 */
public interface PhotoReferencesListener {
    /**
     * Called when list of photo references has been gotten from Google.
     * @param photoReferences photo references that has been gotten.
     */
    void onPhotoReferencesGet(final List<String> photoReferences);
}