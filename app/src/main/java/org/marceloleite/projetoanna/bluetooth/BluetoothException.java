package org.marceloleite.projetoanna.bluetooth;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class BluetoothException extends Exception {

    public BluetoothException(String message, Throwable cause) {
        super(message, cause);
    }

    public BluetoothException(String message) {
        super(message);
    }
}
