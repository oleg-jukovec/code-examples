package ru.ozi_blog.nv_dev.photo.simply;

import ru.ozi_blog.nv_dev.photo.CollageCreator;

/**
 * This abstract class implements CollageCreator interface and provides
 * basic functionality for very simple creators of a collages.
 */
public abstract class SimpleCollageCreator implements CollageCreator {
    // maximum separate photo width
    private int mMaxPhotoWidth;
    // maximum separate photo height
    private int mMaxPhotoHeight;
    // count of photos for creating a collage
    private int mPhotoCount;
    // background color of a collage
    private int mBackGroundColor;
    // margins size around photos
    private int mMargins;
    /**
     * Constructor set class fields.
     *
     * @param maxPhotoWidth maximum separate photo width.
     * @param maxPhotoHeight maximum separate photo height.
     * @param photoCount count of photos that need for creating a collage.
     * @param backGroundColor background color of a collage.
     * @param margins size of margins around photos that will be
     *                painted in background color.
     */
    SimpleCollageCreator(final int maxPhotoWidth, final int maxPhotoHeight,
                         final int photoCount, final int backGroundColor,
                         final int margins) {
        mMaxPhotoHeight = maxPhotoHeight;
        mMaxPhotoWidth  = maxPhotoWidth;
        mPhotoCount     = photoCount;
        mBackGroundColor = backGroundColor;
        mMargins = margins;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPhotoHeight() {
        return mMaxPhotoHeight;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPhotoWidth() {
        return mMaxPhotoWidth;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int needPhotos() {
        return mPhotoCount;
    }

    /**
     * Returns a background color of the collage.
     *
     * @return background color of the collage.
     */
    public int getBackGroundColor() {
        return mBackGroundColor;
    }

    /**
     * Sets a background color of the collage.
     * @param backGroundColor background color of the collage.
     */
    public void setBackGroundColor(final int backGroundColor) {
        mBackGroundColor = backGroundColor;
    }

    /**
     * Returns margins size around photos.
     * @return margins size around photos.
     */
    public int getMargins() {
        return mMargins;
    }

    /**
     * Sets margins size around photos.
     * @param margin margins size around photos.
     */
    public void setMargins(final int margin) {
        mMargins = margin;
    }
}
