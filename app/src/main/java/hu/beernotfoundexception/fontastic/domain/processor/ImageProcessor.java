package hu.beernotfoundexception.fontastic.domain.processor;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * The image processor interface.
 */
public interface ImageProcessor {

    /**
     * The interface Image processing callback.
     */
    interface ImageProcessingListener {

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
     * @param image    the input
     * @param listener the listener, does not need to run on UI
     */
    void processImage(@NonNull Bitmap image, @NonNull ImageProcessingListener listener);
}
