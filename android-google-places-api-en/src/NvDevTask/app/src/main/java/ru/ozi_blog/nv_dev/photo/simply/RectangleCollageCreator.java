package ru.ozi_blog.nv_dev.photo.simply;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * This class is intended for creation rectangle collages.
 * (All photos are in two vertical line).
 */
class RectangleCollageCreator extends SimpleCollageCreator {
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
    RectangleCollageCreator(final int maxPhotoWidth, final int maxPhotoHeight,
                                   final int photoCount, final int backGroundColor,
                                   final int margins) {
        super(maxPhotoWidth, maxPhotoHeight, photoCount, backGroundColor, margins);
    }
    /**
     * Returns a rectangle collage from the list of photos.
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
        // calculation sizes of lines
        int firstLineHeight  = 0;
        int firstLineWidth   = 0;
        int secondLineHeight = 0;
        int secondLineWidth  = 0;
        int middleNumber = photos.size() / 2;
        for(int i = 0; i < middleNumber; i++) {
            if(firstLineHeight < photos.get(i).getHeight())
                firstLineHeight = photos.get(i).getHeight();
            firstLineWidth  += photos.get(i).getWidth();
            if(secondLineHeight < photos.get(middleNumber + i).getHeight())
                secondLineHeight = photos.get(middleNumber + i).getHeight();
            secondLineWidth += photos.get(middleNumber + i).getWidth();
        }
        // calculation collage sizes
        int photosMaxWidth  = (firstLineWidth > secondLineWidth)
                ? firstLineWidth : secondLineWidth;
        int photosMaxHeight = firstLineHeight + secondLineHeight;
        int collageWidth  = photosMaxWidth + getMargins()
                + getMargins() * middleNumber;
        int collageHeight = photosMaxHeight + getMargins() * 3;
        // creation image
        Bitmap collage = Bitmap.createBitmap(collageWidth, collageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(collage);
        // setting background color
        comboImage.drawColor(getBackGroundColor());
        // drawing photos into first line at collage
        int beginHeight = getMargins();
        int beginWidth  = getMargins();
        int widthPadding = (photosMaxWidth - firstLineWidth) / 2;
        for(int i = 0; i < middleNumber; i++) {
            int heightPadding = (firstLineHeight - photos.get(i).getHeight()) / 2;
            comboImage.drawBitmap(photos.get(i), beginWidth + widthPadding,
                    beginHeight + heightPadding, null);
            beginWidth += photos.get(i).getWidth() + getMargins();
        }
        // drawing photos into second line at collage
        beginHeight = getMargins() * 2 + firstLineHeight;
        beginWidth  = getMargins();
        widthPadding = (photosMaxWidth - secondLineWidth) / 2;
        for(int i = 0; i < middleNumber; i++) {
            int heightPadding = (secondLineHeight
                    - photos.get(middleNumber + i).getHeight()) / 2;
            comboImage.drawBitmap(photos.get(middleNumber + i),
                    beginWidth + widthPadding, beginHeight + heightPadding, null);
            beginWidth += photos.get(middleNumber + i).getWidth() + getMargins();
        }

        return collage;
    }
}
