package org.marceloleite.projetoanna;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.marceloleite.projetoanna.bluetooth.Bluetooth;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener {

    private Bluetooth bluetooth;

    public ButtonConnectOnClickListener(AppCompatActivity appCompatActivity) {
        this.bluetooth = new Bluetooth(appCompatActivity);
    }

    @Override
    public void onClick(View view) {
        bluetooth.ConnectToBluetoothServer();
    }
}
