package ru.ozi_blog.nv_dev.photo;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Interface define basic behavion for creating collage that can be used
 * by {@link ru.ozi_blog.nv_dev.photo.CollageMaker}.
 */
public interface CollageCreator {
    /**
     * Returns maximum height of a separate photo.
     *
     * @return maximum height of a separate photo.
     */
    int getMaxPhotoHeight();

    /**
     * Returns maximum width of a separate photo.
     *
     * @return maximum height of a separate photo.
     */
    int getMaxPhotoWidth();

    /**
     * Returns number of photos that need for collage creating.
     *
     * @return number of photos that need for collage creating.
     */
    int needPhotos();

    /**
     * Returns a collage from photos.
     *
     * @param photos photos that will be used in creation.
     * @return collage a collage from photos.
     * @throws IllegalArgumentException when photos.size() != {@link #needPhotos()}.
     */
    Bitmap createCollage(final List<Bitmap> photos) throws IllegalArgumentException;
}