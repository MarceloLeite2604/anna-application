package org.marceloleite.projetoanna.audiorecorder.communicator;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * The parameters required to construct a {@link Communicator} object.
 */
public class CommunicatorParameters {

    /**
     * The socket which represents the bluetooth connection with the audio recorder.
     */
    private final BluetoothSocket bluetoothSocket;

    /**
     * The context of the application being executed.
     */
    private final Context context;

    /**
     * Constructor.
     *
     * @param bluetoothSocket The socket which represents the bluetooth connection with the audio recorder.
     * @param context         The context of the application being executed.
     */
    public CommunicatorParameters(BluetoothSocket bluetoothSocket, Context context) {
        this.bluetoothSocket = bluetoothSocket;
        this.context = context;
    }

    /**
     * Returns the socket which represents the bluetooth connection with the audio recorder.
     *
     * @return The socket which represents the bluetooth connection with the audio recorder.
     */
    BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    /**
     * Returns the context of the application being executed.
     *
     * @return The context of the application being executed.
     */
    Context getContext() {
        return context;
    }
}
