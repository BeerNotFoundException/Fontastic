package hu.beernotfoundexception.fontastic.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import hu.beernotfoundexception.fontastic.util.Logger;

public class TcpClient {
    public static final String TAG = TcpClient.class.getSimpleName();

    private final String remoteIp;
    private final int remotePort;

    private final OnMessageReceivedListener mMessageListener;
    private boolean mRun = false;

    private PrintWriter out = null;
    private BufferedReader in = null;

    public TcpClient(String remoteIp, int remotePort, OnMessageReceivedListener messageListener) {
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        mMessageListener = messageListener;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            System.out.println("message: " + message);
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(remoteIp);

            Logger.i(TAG, "Connecting...");

            try (Socket socket = new Socket(serverAddr, remotePort)) {

                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Logger.i(TAG, "Connected!");

                while (mRun) {
                    String serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        mMessageListener.messageReceived(serverMessage);
                        Logger.i(TAG, "Incoming: '" + serverMessage + "'");
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, "Error!", e);
                e.printStackTrace();
            }
        } catch (Exception e) {
            Logger.e(TAG, "SI: Error", e);
        }

    }

    //Interfész.
    //A messageReceived(String message) metódust implementálni kell
    //az asynckTasknál a doInBackground részben.
    public interface OnMessageReceivedListener {
        void messageReceived(String message);
    }

    public static class Builder {
        private String remoteIp;
        private int remotePort;
        private OnMessageReceivedListener messageListener;

        public Builder(String ip, int port) {
            this.remoteIp = ip;
            this.remotePort = port;
        }

        public Builder setMessageListener(OnMessageReceivedListener listener) {
            this.messageListener = listener;
            return this;
        }

        public TcpClient build() {
            return new TcpClient(remoteIp, remotePort, messageListener);
        }
    }
}