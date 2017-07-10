package ru.ozi_blog.nv_dev.photo.simply;

import android.util.Log;

import ru.ozi_blog.nv_dev.photo.CollageCreator;
/**
 * Simple Factory Method that demonstrate creation of different collage creators.
 */
public final class CollageCreatorFactory {
    // default maximum photo height
    private final static int MAX_PHOTO_HEIGHT = 600;
    // default maximum photo width
    private final static int MAX_PHOTO_WIDTH  = 600;
    // default maximum photo count
    private final static int MAX_PHOTO_COUNT  = 4;
    // background color that can be set by client
    private final int mBackgroundColor;
    // margins
    private final int mMargins;

    // tag for logging
    private static final String  TAG = CollageCreatorFactory.class.getSimpleName();
    /**
     * Types of collages that can be created.
     */
    public enum CollageType {
        /**
         * Vertical collage.
         */
        VERTICAL,
        /**
         * Horizontal collage.
         */
        HORIZONTAL,
        /**
         * Rectangle collage.
         */
        RECTANGLE
    }

    /**
     * Constructor that set some parameters of CollageCreators that will be created.
     *
     * @param margins margins off photos.
     * @param backgroundColor collage background color.
     */
    public CollageCreatorFactory(int margins, int backgroundColor) {
        mMargins = margins;
        mBackgroundColor = backgroundColor;
    }

    /**
     * Factory Method that create CollageCreator Object depending on argument.
     *
     * @param type type of creating CollageCreator.
     * @return CollageCreator Object.
     */
    public CollageCreator getCreator(CollageType type) {
        CollageCreator creator = null;
        switch (type) {
            case VERTICAL:
                creator = new VerticalCollageCreator(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT,
                        MAX_PHOTO_COUNT, mBackgroundColor, mMargins);
                Log.d(TAG, "Vertical CollageCreator has been created.");
                break;
            case HORIZONTAL:
                creator = new HorizontalCollageCreator(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT,
                        MAX_PHOTO_COUNT, mBackgroundColor, mMargins);
                Log.d(TAG, "Horizontal CollageCreator has been created.");
                break;
            case RECTANGLE:
                creator = new RectangleCollageCreator(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT,
                        MAX_PHOTO_COUNT, mBackgroundColor, mMargins);
                Log.d(TAG, "Rectangle CollageCreator has been created.");
                break;
            default:
                throw new IllegalArgumentException();
        }
        return creator;
    }
}
