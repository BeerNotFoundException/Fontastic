package hu.beernotfoundexception.fontastic.comm;

import android.os.AsyncTask;

import hu.beernotfoundexception.fontastic.domain.Constants;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;
import hu.beernotfoundexception.fontastic.util.Logger;

public class TcpCommunicationClient implements CommunicationInterface {

    public static final String TAG = TcpCommunicationClient.class.getSimpleName();

    public ConnectTask conctTask = null;
    ConnectionEventListener mConnectionEventListener;
    private TcpClient tcpClient = null;
    private String remoteIp;
    private boolean isIncomingDataStream = false;

    public TcpCommunicationClient(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    @Override
    public void start() {
        conctTask = new ConnectTask();
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stop() {
        destroy();
    }

    @Override
    public void sendMessage(String message) {
        if (tcpClient != null) {
            Logger.i(TAG, "TcpClient sending message: " + message);
            tcpClient.sendMessage(message);
        }
    }

    public void destroy() {
        try {
            Logger.i(TAG, "Destroying");
            tcpClient.stopClient();
            if (conctTask != null) {
                conctTask.cancel(true);
                conctTask = null;
            }
            if (mConnectionEventListener != null) {
                mConnectionEventListener.onDestroy();
            }
        } catch (Exception e) {
            Logger.e(TAG, "Destroy", e);
        }
    }

    private void handleMessage(String message) {
        if (isIncomingDataStream) {
            handleBytes(message);
        } else {
            if (message.contains("CRITICAL")) {
                mConnectionEventListener.onException(
                        new Exception(message),
                        !message.startsWith("NON-"));
            } else
                mConnectionEventListener.onMessage(message);
        }
    }

    private void handleBytes(String byteString) {
        isIncomingDataStream = false;
        Logger.i(TAG, "Handling bytes, length " + byteString.length());
        mConnectionEventListener.onByteArray(DataStreamDecoder.trimToData(byteString));
    }

    @Override
    public void setConnectionEventListener(ConnectionEventListener listener) {
        mConnectionEventListener = listener;
    }

    public void notifyExpectByteArray() {
        isIncomingDataStream = true;
    }

    public class ConnectTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            TcpClient.Builder tcpBuilder = new TcpClient.Builder(remoteIp, Constants.REMOTE_PORT)
                    .setMessageListener(new TcpClient.CommunicationListener() {
                        @Override
                        public void messageReceived(String message) {
                            try {
                                publishProgress(message);
                                if (message != null) {
                                    Logger.i(TAG, tcpClient.getRemoteIp() + " incoming: " + message);
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, "Receive error", e);
                                mConnectionEventListener.onException(e, false);
                            }
                        }

                        @Override
                        public void onCommunicationError(Exception e, boolean isCritical) {
                            publishProgress((isCritical ? "" : "NON-")
                                    + "CRITICAL communication error: "
                                    + e.getMessage());
                        }
                    });

            tcpClient = tcpBuilder.build();

            tcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            handleMessage(values[0]);
        }
    }
}
