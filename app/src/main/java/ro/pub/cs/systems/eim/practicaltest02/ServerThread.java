package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cz.msebera.android.httpclient.client.ClientProtocolException;

/**
 * Created by monica on 25.05.2018.
 */

public class ServerThread extends Thread {

    private ServerSocket serverSocket = null;

    public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
        }
    }

    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[Server thread] Waiting for client");
                Socket socket = serverSocket.accept();

                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolexception) {
            Log.e(Constants.TAG, "exception");
        } catch (IOException exception) {
            Log.e(Constants.TAG, "exception");
        }
    }
}
