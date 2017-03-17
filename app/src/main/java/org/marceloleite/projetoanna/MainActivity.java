package org.marceloleite.projetoanna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    private Button buttonConnect;

    private Button buttonRecord;

    private EditText editTextLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = (Button)findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
                }

            }
        });


        buttonRecord = (Button)findViewById(R.id.button_connect);
        editTextLog = (EditText)findViewById(R.id.editText_log);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == ENABLE_BLUETOOTH_REQUEST_CODE ) {
            // TODO: Conclude post bluetooth activation action.
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
