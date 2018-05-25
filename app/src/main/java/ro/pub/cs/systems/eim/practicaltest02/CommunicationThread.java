package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by monica on 25.05.2018.
 */

public class CommunicationThread extends Thread {

    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        try {
            Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }


            String word = bufferedReader.readLine();
            String nrLettersString = bufferedReader.readLine();
            if (word == null || word.isEmpty() || nrLettersString == null || nrLettersString.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word / nr letters!");
                return;
            }

            Log.d(Constants.TAG, "received word " + word);

            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?anagram=" + word
                    + "&minLetters=" + nrLettersString);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpGet, responseHandler);
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }
            Log.d(Constants.TAG, "[COMMUNICATION] queried page");
            Document document = Jsoup.parse(pageSourceCode);
            Element element = document.child(0);
            Log.d(Constants.TAG, element.ownText());

            Elements elements = element.getElementsByTag("string");
            for (Element string: elements) {
                String stringData = string.ownText();
                Log.d(Constants.TAG, stringData);
                result += stringData + " ";
            }


            printWriter.println(result);

            // - get the PrintWriter object in order to write on the socket (use Utilities.getWriter())
            // - print a line containing the text in the serverTextEditText edit text

            socket.close();
            Log.v(Constants.TAG, "Connection closed");
        } catch (Exception exception) {
            Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
        }
    }

}
