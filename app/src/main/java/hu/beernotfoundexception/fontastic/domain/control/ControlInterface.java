package hu.beernotfoundexception.fontastic.domain.control;

import android.graphics.Bitmap;

import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;

public interface ControlInterface {

    void scanBitmap(Bitmap img);

    void startTest(String ip);

    void cancelPending();

    void setCommunicationInterface(CommunicationInterface communicationInterface);
}
