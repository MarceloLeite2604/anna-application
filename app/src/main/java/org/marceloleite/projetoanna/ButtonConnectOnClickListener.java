package org.marceloleite.projetoanna;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener {

    Bluetooth bluetooth;

    public ButtonConnectOnClickListener(MainActivity mainActivity) {
        this.bluetooth = mainActivity.getBluetooth();
    }

    @Override
    public void onClick(View view) {
        bluetooth.ConnectToBluetoothServer();
    }
}
