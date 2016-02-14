package hu.beernotfoundexception.fontastic.domain.presenter;

import android.graphics.Bitmap;

import hu.beernotfoundexception.fontastic.domain.control.ControlInterface;

public interface Presenter {

    void onDetectionResult(String fontName);

    void onDetectionError(String reason);

    void onConnected();

    void onInterrupted();

    void onConnectionError();

    void showImage(Bitmap image);

    void setControlInterface(ControlInterface controlInterface);
}
