package hu.beernotfoundexception.fontastic.domain.control;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;

import hu.beernotfoundexception.fontastic.domain.processor.ImageProcessor;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;

public class ControlOnBgControl implements ControlInterface {

    final ControlInterface controlInterface;

    public ControlOnBgControl(ImageProcessor imageProcessor) {
        controlInterface = new FontasticControl(imageProcessor);
    }

    @Override
    public void setCommunicationInterface(final CommunicationInterface communicationInterface) {
        controlInterface.setCommunicationInterface(communicationInterface);
    }

    @Override
    public void scanBitmap(final Bitmap img) {
        AsyncTask.execute(new Runnable() {
                              @Override
                              public void run() {
                                  Looper.prepare();
                                  controlInterface.scanBitmap(img);
                              }
                          }
        );
    }

    @Override
    public void startTest(final String ip) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                controlInterface.startTest(ip);
            }
        });
    }

    @Override
    public void cancelPending() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                controlInterface.cancelPending();
            }
        });
    }
}
