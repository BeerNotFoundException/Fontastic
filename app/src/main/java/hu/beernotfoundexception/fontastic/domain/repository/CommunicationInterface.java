package hu.beernotfoundexception.fontastic.domain.repository;

public interface CommunicationInterface {

    void start();

    void stop();

    void setConnectionEventListener(ConnectionEventListener listener);

    void sendMessage(String msg);

    void notifyExpectByteArray();

    interface ConnectionEventListener {
        void onMessage(String msg);

        void onByteArray(byte[] bytes);

        void onException(Exception e, boolean isCritical);

        void onDestroy();
    }
}
