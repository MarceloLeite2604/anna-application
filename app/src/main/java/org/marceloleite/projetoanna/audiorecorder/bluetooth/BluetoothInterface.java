package org.marceloleite.projetoanna.audiorecorder.bluetooth;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 02/05/2017.
 */

public interface BluetoothInterface {

    void connectWithAudioRecorderResult(int result);

    AppCompatActivity getAppCompatActivity();
}
