package hu.beernotfoundexception.fontastic.domain.control;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import hu.beernotfoundexception.fontastic.domain.presenter.LogDisplay;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;

public interface ControlInterface {

    void onBitmapScanRequest(Bitmap img);

    void onTestRequest(String ip, @Nullable TestProgressListener listener);

    void cancelTest();

    void setLogDisplay(LogDisplay display);

    void setCommunicationInterface(CommunicationInterface communicationInterface);

    public interface TestProgressListener {
        void onStart();

        void onProgress(float p);

        void onFinish();
    }

}
