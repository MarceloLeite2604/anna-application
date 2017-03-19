package org.marceloleite.projetoanna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "projeto-anna";

    private Button buttonConnect;

    private Button buttonRecord;

    private EditText editTextLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = (Button)findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this));

        buttonRecord = (Button)findViewById(R.id.button_connect);
        editTextLog = (EditText)findViewById(R.id.editText_log);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE ) {
            // TODO: Conclude post bluetooth activation action.
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
