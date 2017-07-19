package org.marceloleite.projetoanna.audiorecorder.communicator.operator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressMonitorAlertDialog;

/**
 * The parameters required to execute the {@link org.marceloleite.projetoanna.audiorecorder.communicator.operator.OperatorThread} object.
 */
public class OperatorThreadParameters {

    private final OperationResultHandler operationResultHandler;

    /**
     * The socket which represents the bluetooth communication with the audio recorder.
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * The application which established the connection with the audio recorder.
     */
    private final AppCompatActivity appCompatActivity;

    private final ProgressMonitorAlertDialog progressMonitorAlertDialog;

    /**
     * Constructor.
     *
     * @param bluetoothSocket   The socket which represents the bluetooth communication with the audio recorder.
     * @param appCompatActivity The appCompatActivity of the application which established the connection with the audio recorder.
     */
    public OperatorThreadParameters(BluetoothSocket bluetoothSocket, OperationResultHandler operationResultHandler, AppCompatActivity appCompatActivity, ProgressMonitorAlertDialog progressMonitorAlertDialog) {
        this.bluetoothSocket = bluetoothSocket;
        this.operationResultHandler = operationResultHandler;
        this.appCompatActivity = appCompatActivity;
        this.progressMonitorAlertDialog = progressMonitorAlertDialog;
    }

    /**
     * Returns the bluetooth socket which defines the connection with audio recorder.
     *
     * @return The bluetooth socket which defines the connection with audio recorder.
     */
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    /**
     * Returns the appCompatActivity of the application which established the connection with the audio recorder.
     *
     * @return The appCompatActivity of the application which established the connection with the audio recorder.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public OperationResultHandler getOperationResultHandler() {
        return operationResultHandler;
    }

    public ProgressMonitorAlertDialog getProgressMonitorAlertDialog() {
        return progressMonitorAlertDialog;
    }
}
