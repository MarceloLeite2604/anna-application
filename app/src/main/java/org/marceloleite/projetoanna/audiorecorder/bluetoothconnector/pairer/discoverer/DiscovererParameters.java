package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcelo on 26/06/17.
 */

public class DiscovererParameters {

    private AppCompatActivity appCompatActivity;

    public DiscovererParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
