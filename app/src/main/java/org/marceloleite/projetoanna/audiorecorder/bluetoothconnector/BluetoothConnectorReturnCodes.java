package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector;

import org.marceloleite.projetoanna.utils.GenericReturnCodes;

/**
 * Return codes used by {@link BluetoothConnector} class to inform its result.
 */
public class BluetoothConnectorReturnCodes extends GenericReturnCodes {

    /**
     * Indicates that used has denied the bluetooth adapter activation.
     */
    static final int BLUETOOTH_ACTIVATION_DENIED = 51;

    /**
     * Indicates that the bluetooth device discovering was cancelled.
     */
    public static final int DISCOVERING_CANCELLED = 52;

    /**
     * Indicates that the pairing attempt failed.
     */
    public static final int PAIRING_FAILED = 53;

    /**
     * Indicates that user has cancelled the bluetooth device selection to connect.
     */
    public static final int DEVICE_SELECTION_CANCELLED = 54;

    /**
     * Indicates that the connection attempt with the audio recorder has failed.
     */
    public static final int CONNECTION_FAILED = 55;

    /**
     * Indicates that the connection attempt was cancelled by the user.
     */
    public static final int CONNECTION_CANCELLED = 56;

}
