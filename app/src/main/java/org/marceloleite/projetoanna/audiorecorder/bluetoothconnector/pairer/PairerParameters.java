package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.support.v7.app.AppCompatActivity;

/**
 * The parameters required to construct a {@link Pairer} object.
 */
public class PairerParameters {

    /**
     * The activity in execution.
     */
    private final AppCompatActivity appCompatActivity;

    /**
     * Constructor.
     *
     * @param appCompatActivity The activity in execution.
     */
    public PairerParameters(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * Returns the activity in execution.
     *
     * @return The activity in execution.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }
}
