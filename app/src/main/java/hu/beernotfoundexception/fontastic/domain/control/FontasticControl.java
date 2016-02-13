package hu.beernotfoundexception.fontastic.domain.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import hu.beernotfoundexception.fontastic.comm.TcpCommunicationClient;
import hu.beernotfoundexception.fontastic.domain.Constants;
import hu.beernotfoundexception.fontastic.domain.presenter.LogDisplay;
import hu.beernotfoundexception.fontastic.domain.presenter.Presenter;
import hu.beernotfoundexception.fontastic.domain.repository.Commands;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;
import hu.beernotfoundexception.fontastic.domain.repository.ResponseParser;

public class FontasticControl implements ControlInterface {

    public static final String TAG = FontasticControl.class.getSimpleName();

    private final Presenter presenter;

    private LogDisplay logDisplay;

    private CommunicationInterface communicationInterface;
    private int startImages = 0;

    public FontasticControl(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLogDisplay(LogDisplay logDisplay) {
        this.logDisplay = logDisplay;
    }

    @Override
    public void setCommunicationInterface(CommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    @Override
    public void onBitmapScanRequest(Bitmap img) {
        log("Scan request: " + img.toString());
    }

    @Override
    public void onTestRequest(String ip, final TestProgressListener listener) {
        log("Connecting to " + ip);

        this.setCommunicationInterface(new TcpCommunicationClient(ip));

        if (listener != null) {
            listener.onStart();
        }
        communicationInterface.setMessageReceivedListener(
                new CommunicationInterface.OnMessageReceivedListener() {
                    @Override
                    public void onMessage(String msg) {
                        switch (ResponseParser.getResponseType(msg)) {

                            case SERVER_HELLO:
                                communicationInterface.sendMessage(Commands.CLIENT_HELLO);
                                break;
                            case SERVER_SEND_ID:
                                communicationInterface.sendMessage(Commands.CLIENT_SEND_ID);
                                break;
                            case SERVER_ID_ACK:
                                startImages = ResponseParser.getRemainingFromMessage(msg);
                                if (listener != null) {
                                    listener.onProgress(0);
                                }
                            case SERVER_FONT_ACK:
                                if (listener != null) {
                                    listener.onProgress((startImages - ResponseParser.getRemainingFromMessage(msg)) / startImages);
                                }
                                if (ResponseParser.getRemainingFromMessage(msg) > 0)
                                    communicationInterface.setExpectingDataStream(
                                            Constants.FILE_START,
                                            Constants.FILE_END);
                                communicationInterface.sendMessage(Commands.CLIENT_REQ_NEW);
                                break;
                            case SERVER_FONT_ACK_EOC:
                                if (listener != null) {
                                    listener.onFinish();
                                }
                                communicationInterface.stop();
                                break;
                            case SERVER_UNDEFINED:
                                log("Unknown message: " + msg);
                                break;
                        }
                    }

                    @Override
                    public void onByteArray(byte[] bytes) {
                        presenter.showImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                });

        communicationInterface.start();
    }

    @Override
    public void cancelTest() {
        if (communicationInterface != null) {
            log("Stopping...");
            communicationInterface.stop();
            log("Stopped.");
        }
    }

    private void log(String s) {
        if (logDisplay != null) {
            logDisplay.logMessage(s);
        } else
            Log.i(TAG, s);
    }
}
