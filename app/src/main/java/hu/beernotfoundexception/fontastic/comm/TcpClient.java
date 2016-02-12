package hu.beernotfoundexception.fontastic.comm;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private static final int SERVERPORT = 9999;
    public static String SERVERIP = "192.168.0.105";
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    private PrintWriter out = null;
    private BufferedReader in = null;

    /**
     * Konstruktor. OnMessagedReceived figyeli a bejövő üzeneteket
     */
    public TcpClient(final OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * A beírt üzenetet elküldi
     *
     * @param message kliens által küldött üzenet
     */
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
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCPClient", "Socket: Csatlakozás...");

            try (Socket socket = new Socket(serverAddr, SERVERPORT)) {

                //üzenet küldés
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCPClient", "Socket: Elküldve.");

                //bejövő fogadása
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //a kliens figyeli a bejövő üzeneteket
                while (mRun) {
                    String serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //hívjuk a messageReceived metódust
                        mMessageListener.messageReceived(serverMessage);
                        Log.e("SZERVER VÁLASZ", "Socket: Bejövő: '" + serverMessage + "'");
                    }
                }
            } catch (Exception e) {
                Log.e("TCPError", "Socket: Error ", e);
                e.printStackTrace();
            }
            //lezárjuk a socketet. ezután ebben a példányban már nem lehet újra csatlakozni


        } catch (Exception e) {

            Log.e("TCP SI Error", "SI: Error", e);

        }

    }

    //Interfész.
    //A messageReceived(String message) metódust implementálni kell
    //az asynckTasknál a doInBackground részben.
    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}