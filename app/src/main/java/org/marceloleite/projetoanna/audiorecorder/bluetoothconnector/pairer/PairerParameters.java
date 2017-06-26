package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelo on 26/06/17.
 */

public class PairerParameters {

    private AppCompatActivity appCompatActivity;

    public PairerParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
