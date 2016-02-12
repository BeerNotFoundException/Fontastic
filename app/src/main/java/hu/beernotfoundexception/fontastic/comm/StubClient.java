package hu.beernotfoundexception.fontastic.comm;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class StubClient {
    private static final String TEAM_ID = "TEST1"; //TODO: változtatni az éles verzióhoz
    public connectTask conctTask = null;
    private Handler handler = new Handler();
    private String msgResponse = "";
    private TcpClient mTcpClient = null;
    private boolean ready = false;
    private String status = "";

    private ArrayList<String> arrayList;
    private String password, trainer, probe;

    public StubClient() {
        Log.d("BeeKeyboardClient", "Konstruktor meghívva, csatlakozás, handshake.");
        conctTask = new connectTask();
    }

    public void Start() {
        conctTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void Auth(boolean isItYou) {
        sendMessage((isItYou) ? "ACCEPT" : "REJECT");
    }

    private void sendMessage(String message) {
        if (mTcpClient != null) {
            Log.d("Bee", "mTcpClient not null, sendMessage" + message);
            mTcpClient.sendMessage(message);
        }
    }

    public void Destroy() {
        try {
            System.out.println("Destroy.");
            mTcpClient.sendMessage("BYE");
            mTcpClient.stopClient();
            conctTask.cancel(true);
            conctTask = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class connectTask extends AsyncTask<String, String, TcpClient> {
        public AsyncResponse delegate = null;

        @Override
        protected TcpClient doInBackground(String... message) {
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    try {
                        publishProgress(message);
                        if (message != null) {
                            System.out.println("FromSocket> " + message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mTcpClient.run();
            if (mTcpClient != null) {
                mTcpClient.sendMessage("Init");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String tmpMsg = values[0];
            if (tmpMsg.endsWith("WAITING FOR REQUEST")) tmpMsg = "WAITING";
            if (tmpMsg.startsWith("PASSWORD")) tmpMsg = "PASS";
            if (tmpMsg.startsWith("GOODBYE")) tmpMsg = "BYE";
            switch (tmpMsg) {
                case "BSP 1.0 SERVER HELLO":
                    sendMessage("BSP 1.0 CLIENT HELLO");
                    break;
                case "SEND YOUR ID":
                    sendMessage(TEAM_ID);
                    break;
                case "WAITING":
                    sendMessage("RQSTDATA");
                    break;
                case "PASS":
                    delegate.msgIn("PASS", values[0].substring(9));
                    status = "RQSTTRAIN";
                    sendMessage(status);
                    break;
                case "bye":
                    delegate.msgIn("BYE", "");
                    break;
                default:
                    if (status.equals("RQSTTRAIN")) {
                        delegate.msgIn("TRAIN", values[0]);
                        status = "RQSTTEST";
                        sendMessage(status);
                        break;
                    }
                    if (status.equals("RQSTTEST")) {
                        if (values[0].startsWith("GOODBYE")) status = "";
                        else {
                            delegate.msgIn("TEST", values[0]);
                            break;
                        }
                    }

            }
        }
    }
}