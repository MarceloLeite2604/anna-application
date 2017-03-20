package org.marceloleite.projetoanna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "projeto-anna";

    private Button buttonConnect;

    private Button buttonRecord;

    private Bluetooth bluetooth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (bluetooth == null) {
            bluetooth = new Bluetooth(this);
        }

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this));

        buttonRecord = (Button) findViewById(R.id.button_record);

        buttonRecord.setOnClickListener(new ButtonRecordOnClickListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.d(MainActivity.LOG_TAG, "onActivityResult, 42: Bluetooth activated.");
                    bluetooth.ConnectToBluetoothServer();
                    break;
                default:
                    Log.d(MainActivity.LOG_TAG, "onActivityResult, 47: Bluetooth not activated.");
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public void connectionStablished() {
        buttonRecord.setEnabled(true);
    }

    public void disconnected() {
        buttonRecord.setEnabled(false);
    }

}
