package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by monica on 25.05.2018.
 */

public class ClientThread extends Thread {
    private Socket socket = null;
    String word = null;
    String nrLetters = null;
    TextView wordsTextView = null;
    String address = null;
    int port;

    public ClientThread(int port, String word, String nrLetters, TextView responseView) {
        this.word = word;
        this.nrLetters = nrLetters;
        this.wordsTextView = responseView;
        this.address = "localhost";
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(word);
            printWriter.println(nrLetters);
            final String words = bufferedReader.readLine();
            if (words != null) {
                wordsTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        wordsTextView.setText(words);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
