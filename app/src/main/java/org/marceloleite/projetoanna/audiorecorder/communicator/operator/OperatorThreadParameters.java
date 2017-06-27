package org.marceloleite.projetoanna.audiorecorder.communicator.operator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result.OperationResultHandler;

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
     * The context of the application which established the connection with the audio recorder.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param bluetoothSocket The socket which represents the bluetooth communication with the audio recorder.
     * @param context         The context of the application which established the connection with the audio recorder.
     */
    public OperatorThreadParameters(BluetoothSocket bluetoothSocket, OperationResultHandler operationResultHandler, Context context) {
        this.bluetoothSocket = bluetoothSocket;
        this.operationResultHandler = operationResultHandler;
        this.context = context;
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
     * Returns the context of the application which established the connection with the audio recorder.
     *
     * @return The context of the application which established the connection with the audio recorder.
     */
    public Context getContext() {
        return context;
    }

    public OperationResultHandler getOperationResultHandler() {
        return operationResultHandler;
    }
}
