package hu.beernotfoundexception.fontastic.domain.processor;

import android.graphics.Bitmap;

/**
 * The image processor interface.
 */
public interface ImageProcessor {

    /**
     * The interface Image processing callback.
     */
    interface ImageProcessingCallback {

        /**
         * On start.
         */
        void onStart();

        /**
         * On progress update.
         *
         * @param percent the percent
         */
        void onProgressUpdate(float percent);

        /**
         * On result.
         *
         * @param fontName the font name
         */
        void onResult(String fontName);

        /**
         * On error.
         *
         * @param e the e
         */
        void onError(Exception e);

    }

    /**
     * Process image.
     *
     * @param image    the image
     * @param callback the callback
     */
    void processImage(Bitmap image, ImageProcessingCallback callback);
}
