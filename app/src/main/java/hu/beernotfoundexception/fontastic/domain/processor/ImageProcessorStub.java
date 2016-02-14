package hu.beernotfoundexception.fontastic.domain.processor;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import hu.beernotfoundexception.fontastic.domain.control.Fonts;

// TODO: 2/14/2016 Create the real deal
public class ImageProcessorStub implements ImageProcessor {

    @Override
    public void processImage(@NonNull Bitmap image, final @NonNull ImageProcessingListener listener) {
        listener.onStart();
            image.recycle();
            listener.onResult(Fonts.getRandomAcceptedFont());
    }
}
