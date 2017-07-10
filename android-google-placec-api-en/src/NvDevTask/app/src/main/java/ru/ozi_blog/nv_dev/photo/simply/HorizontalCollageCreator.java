package ru.ozi_blog.nv_dev.photo.simply;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * This class is intended for creation horizontal collages.
 * (All photos are in one horizontal line).
 */
class HorizontalCollageCreator extends SimpleCollageCreator {
    /**
     * The constructor calls SimpleCollageCreator()
     *
     * @param maxPhotoWidth maximum separate photo width.
     * @param maxPhotoHeight maximum separate photo height.
     * @param photoCount count of photos for creating a collage.
     * @param backGroundColor background color of a collage.
     * @param margins size of margins around photos that will be
     *                painted in background color.
     */
    HorizontalCollageCreator(final int maxPhotoWidth, final int maxPhotoHeight,
                                    final int photoCount, final int backGroundColor,
                                    final int margins) {
        super(maxPhotoWidth, maxPhotoHeight, photoCount, backGroundColor, margins);
    }
    /**
     * Returns a vertical collage from the list of photos.
     *
     * @param photos photos that will be used in creation.
     * @return collage of photos.
     * @throws IllegalArgumentException when photos.size() != {@link #needPhotos()}.
     */
    @Override
    public Bitmap createCollage(final List<Bitmap> photos)
            throws IllegalArgumentException {
        // checking the photos list
        if(needPhotos() != photos.size())
            throw new IllegalArgumentException();
        // initial ranges for collage size
        int collageWidth  = getMargins();
        int collageHeight = getMargins();
        // calculation of collage size
        for(Bitmap photo : photos) {
            if(collageHeight < photo.getHeight() + getMargins() * 2)
                collageHeight = photo.getHeight() + getMargins() * 2;

            collageWidth += photo.getWidth() + getMargins();
        }
        // creation image
        Bitmap collage = Bitmap.createBitmap(collageWidth, collageHeight, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(collage);
        // setting background color
        comboImage.drawColor(getBackGroundColor());
        // drawing photos into the collage image
        int currentWidth = getMargins();
        for(Bitmap photo : photos) {
            int heightPadding = (collageHeight - getMargins() * 2 - photo.getHeight()) / 2;

            comboImage.drawBitmap(photo, currentWidth, getMargins() + heightPadding, null);
            currentWidth += photo.getWidth() + getMargins();
        }

        return collage;
    }
}
