package hu.beernotfoundexception.fontastic.comm;

import android.os.AsyncTask;

import hu.beernotfoundexception.fontastic.domain.Constants;
import hu.beernotfoundexception.fontastic.domain.repository.CommunicationInterface;
import hu.beernotfoundexception.fontastic.util.Logger;

public class TcpCommunicationClient implements CommunicationInterface {

    public static final String TAG = TcpCommunicationClient.class.getSimpleName();

    public ConnectTask conctTask = null;
    OnMessageReceivedListener receivedListener;
    private TcpClient tcpClient = null;
    private String remoteIp;
    private boolean isIncomingDataStream = false;
    private DataStreamEnclosing mDataStreamEnclosing;

    public TcpCommunicationClient(String remoteIp) {
        conctTask = new ConnectTask();
        this.remoteIp = remoteIp;
    }

    @Override
    public void start() {
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
            conctTask.cancel(true);
            conctTask = null;
        } catch (Exception e) {
            Logger.e(TAG, "Destroy", e);
        }
    }

    private void handleMessage(String message) {
        if (isIncomingDataStream) {
            handleByte(message.getBytes()[0]);
        } else {
            receivedListener.onMessage(message);
        }
    }

    private void handleByte(byte b) {
        Logger.i(TAG, "Handling byte: " + b);
        if (!mDataStreamEnclosing.isDataFinished(b))
            mDataStreamEnclosing.addByte(b);
        else
            receivedListener.onByteArray(mDataStreamEnclosing.byteOutputStream.toByteArray());
    }

    @Override
    public void setMessageReceivedListener(OnMessageReceivedListener listener) {
        receivedListener = listener;
    }

    @Override
    public boolean isIncomingDataStream() {
        return false;
    }

    @Override
    public void setExpectingDataStream(String startMark, String endMark) {
        isIncomingDataStream = true;
        mDataStreamEnclosing = new DataStreamEnclosing(startMark, endMark);
    }

    public class ConnectTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... message) {
            TcpClient.Builder tcpBuilder = new TcpClient.Builder(remoteIp, Constants.REMOTE_PORT)
                    .setMessageListener(new TcpClient.OnMessageReceivedListener() {
                        @Override
                        public void messageReceived(String message) {
                            try {
                                publishProgress(message);
                                if (message != null) {
                                    Logger.i(TAG, tcpClient.getRemoteIp() + " incoming: " + message);
                                }
                            } catch (Exception e) {
                                Logger.e(TAG, "Rcv error", e);
                            }
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
