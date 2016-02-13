package hu.beernotfoundexception.fontastic.domain.presenter;

import android.graphics.Bitmap;

public interface Presenter {

    void onDetectionResult(String fontName);

    void onDetectionError(String reason);

    void onConnected();

    void onNoConnection();

    void showImage(Bitmap image);
}
