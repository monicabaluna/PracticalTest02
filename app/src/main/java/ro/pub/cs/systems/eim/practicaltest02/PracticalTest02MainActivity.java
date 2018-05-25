package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText portEditText = null;
    private Button connectButton = null;

    private EditText wordEditText = null;
    private EditText nrLettersEditText = null;
    private Button getWordsButton = null;
    private TextView wordsTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        portEditText = (EditText)findViewById(R.id.port_edit_text);
        connectButton = (Button)findViewById(R.id.start_server_button);

        wordEditText = (EditText)findViewById(R.id.word_edit_text);
        nrLettersEditText = (EditText)findViewById(R.id.nr_letters_edit_text);
        getWordsButton = (Button)findViewById(R.id.start_client_button);
        wordsTextView = (TextView) findViewById(R.id.result_text_view);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String serverPort = portEditText.getText().toString();

            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext() ,"must specify port", Toast.LENGTH_LONG).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
            }
        });

        getWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientPort = portEditText.getText().toString();
                String word = wordEditText.getText().toString();
                String nrLetters = nrLettersEditText.getText().toString();

                if (clientPort == null || word == null || nrLetters == null ||
                        clientPort.isEmpty() || word.isEmpty() || nrLetters.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "incomplete info", Toast.LENGTH_LONG).show();
                    return;
                }

                wordsTextView.setText("");

                clientThread = new ClientThread(Integer.parseInt(clientPort), word, nrLetters, wordsTextView);
                clientThread.start();
            }
        });


    }


    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
