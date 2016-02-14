package hu.beernotfoundexception.fontastic.domain.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import hu.beernotfoundexception.fontastic.bus.Broadcast;
import hu.beernotfoundexception.fontastic.bus.event.ui.ProcessFinishedEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent;
import hu.beernotfoundexception.fontastic.bus.event.ui.ShowBitmapEvent;
import hu.beernotfoundexception.fontastic.comm.TcpCommunicationClient;
import hu.beernotfoundexception.fontastic.domain.processor.ImageProcessor;
import hu.beernotfoundexception.fontastic.domain.repository.Commands;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;
import hu.beernotfoundexception.fontastic.domain.repository.ResponseParser;
import hu.beernotfoundexception.fontastic.util.Logger;

import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessFinishedEvent.FinishMode;
import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent.ProgressType.OverallProgress;
import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent.ProgressType.ScanFinish;
import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent.ProgressType.ScanProgress;
import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent.ProgressType.ScanStart;
import static hu.beernotfoundexception.fontastic.bus.event.ui.ProcessProgressEvent.ProgressType.Start;

public class FontasticControl implements ControlInterface {

    public static final String TAG = FontasticControl.class.getSimpleName();
    private final ImageProcessor imageProcessor;

    private CommunicationInterface communicationInterface;
    private int startImages = 0;

    public FontasticControl(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    @Override
    public void setCommunicationInterface(CommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    @Override
    public void scanBitmap(final Bitmap img) {
        Logger.i(TAG, "Scan request: " + img.toString());
        imageProcessor.processImage(img, new ImageProcessor.ImageProcessingListener() {
            @Override
            public void onStart() {
                Broadcast.postUi(new ProcessProgressEvent(Start,
                        "Bitmap parsing started", 0));
            }

            @Override
            public void onProgressUpdate(final float percent) {
                Broadcast.postUi(new ProcessProgressEvent(
                        ScanProgress, null, percent));
            }

            @Override
            public void onResult(final String fontName) {
                Broadcast.postUi(new ProcessFinishedEvent(
                        FinishMode.Done, null, fontName));
            }

            @Override
            public void onError(final Exception e) {
                Broadcast.postUi(new ProcessFinishedEvent(
                        FinishMode.Done, null, e));
            }
        });
    }

    @Override
    public void startTest(String ip) {
        Logger.i(TAG, "Connecting to " + ip);

        Broadcast.postUi(new ProcessProgressEvent(Start,
                "TEST mode started.", 0));

        this.setCommunicationInterface(new TcpCommunicationClient(ip));

        communicationInterface.setConnectionEventListener(
                new CommunicationInterface.ConnectionEventListener() {

                    private void getImage() {
                        communicationInterface.notifyExpectByteArray();
                        communicationInterface.sendMessage(Commands.CLIENT_REQ_NEW);
                    }

                    @Override
                    public void onMessage(final String msg) {
                        switch (ResponseParser.getResponseType(msg)) {

                            case SERVER_HELLO:
                                communicationInterface.sendMessage(Commands.CLIENT_HELLO);
                                break;
                            case SERVER_SEND_ID:
                                communicationInterface.sendMessage(Commands.CLIENT_SEND_ID);
                                break;
                            case SERVER_ID_ACK:
                                startImages = ResponseParser.getRemainingFromMessage(msg);
                                getImage();
                                break;
                            case SERVER_FONT_ACK:
                                int imagesLeft = ResponseParser.getRemainingFromMessage(msg);
                                Broadcast.postUi(new ProcessProgressEvent(
                                        OverallProgress,
                                        "Font acknowledged. Remaining: "
                                                + imagesLeft,
                                        (startImages - imagesLeft) / startImages));
                                if (ResponseParser.getRemainingFromMessage(msg) > 0)
                                    getImage();
                                break;
                            case SERVER_FONT_ACK_EOC:
                                communicationInterface.stop();
                                Broadcast.postUi(new ProcessFinishedEvent(
                                        FinishMode.Done,
                                        "All images processed.", null));
                                break;
                            case SERVER_UNDEFINED:
                                Logger.i(TAG, "Unknown message: " + msg);
                            case SERVER_INVALID:
                                Broadcast.postUi(new ProcessFinishedEvent(
                                        FinishMode.Error,
                                        "Abnormal response from server. Stopped.",
                                        null));
                                cancelPending();
                                break;
                        }
                    }

                    @Override
                    public void onDestroy() {
                        Broadcast.postUi(new ProcessFinishedEvent(FinishMode.Interrupted, null, null));
                    }

                    @Override
                    public void onByteArray(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageProcessor.processImage(bmp, new ImageProcessor.ImageProcessingListener() {
                            @Override
                            public void onStart() {
                                Broadcast.postUi(new ProcessProgressEvent(ScanStart, null, 0));
                            }

                            @Override
                            public void onProgressUpdate(float percent) {
                                Broadcast.postUi(
                                        new ProcessProgressEvent(ScanProgress, null, percent));
                            }

                            @Override
                            public void onResult(String fontName) {
                                communicationInterface.sendMessage(fontName);
                                Broadcast.postUi(new ProcessProgressEvent(ScanFinish, fontName, 0));
                                Logger.i(TAG, "Detection ready, font is: " + fontName);
                            }

                            @Override
                            public void onError(Exception e) {
                                communicationInterface.sendMessage(Fonts.getRandomAcceptedFont());
                                Logger.i(TAG, "Detection error. Condolences sent.");
                            }
                        });
                        Broadcast.postUi(new ShowBitmapEvent(bmp));
                    }

                    @Override
                    public void onException(Exception e, boolean isCritical) {
                        Logger.e(TAG, "TESTING error", e);
                        Broadcast.postUi(new ProcessFinishedEvent(
                                FinishMode.Error, "Exception while testing.", e));
                    }
                });

        communicationInterface.start();
    }

    @Override
    public void cancelPending() {
        if (communicationInterface != null) {
            Logger.i(TAG, "Stopping...");
            communicationInterface.stop();
            Logger.i(TAG, "Stopped.");
            Broadcast.postUi(new ProcessFinishedEvent(FinishMode.Interrupted, "Cancelled.", null));
        } else {
            Broadcast.postUi(new ProcessFinishedEvent(FinishMode.Interrupted, "Nothing to stop.", null));
        }
    }
}