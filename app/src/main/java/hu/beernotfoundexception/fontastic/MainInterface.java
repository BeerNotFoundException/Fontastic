package hu.beernotfoundexception.fontastic;

public interface MainInterface {

    void onLogMessage(String msg);

    void onDetectionResult(String fontName);

    void onDetectionError(String reason);

    void onConnected();

    void onNoConnection();
}
