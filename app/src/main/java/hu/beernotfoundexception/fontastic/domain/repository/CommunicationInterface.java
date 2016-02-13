package hu.beernotfoundexception.fontastic.domain.repository;

public interface CommunicationInterface {

    void start();

    void stop();

    void setMessageReceivedListener(OnMessageReceivedListener listener);

    void sendMessage(String msg);

    void setExpectingDataStream(String startMark, String endMark);

    boolean isIncomingDataStream();


    interface OnMessageReceivedListener {
        void onMessage(String msg);

        void onByteArray(byte[] bytes);
    }
}
