package org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

/**
 * Created by marcelo on 22/06/17.
 */

public class BluetoothConnectionParameters {

    private AppCompatActivity appCompatActivity;

    private ViewGroup viewGroup;

    public BluetoothConnectionParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }
}
