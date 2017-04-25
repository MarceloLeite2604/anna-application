package org.marceloleite.projetoanna.audioRecorder.connection;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.utils.GenericReturnCodes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Connection {

    private static final ConnectionException CONNECTION_IS_CLOSED_EXCEPTION = new ConnectionException("Bluetooth connection is closed.");

    private BluetoothSocket bluetoothSocket;

    public Connection(BluetoothSocket bluetoothSocket) {

        this.bluetoothSocket = bluetoothSocket;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public byte[] readSocketContent() throws ConnectionException {
        byte[] bytes = null;
        if (isBluetoothConnected()) {
            try {
                int totalBytesAvailable = bluetoothSocket.getInputStream().available();
                if (totalBytesAvailable > 0) {
                    bytes = new byte[totalBytesAvailable];
                    int totalRead = bluetoothSocket.getInputStream().read(bytes);
                    Log.d(MainActivity.LOG_TAG, "readSocketContent, 41: Total of bytes read: " + totalRead);
                }
            } catch (IOException ioException) {
                String exceptionMessage = "Error reading socket content.";
                throw new ConnectionException(exceptionMessage, ioException);
            }
        } else {
            throw CONNECTION_IS_CLOSED_EXCEPTION;
        }
        return bytes;
    }

    public void writeContentOnSocket(byte[] bytes) throws ConnectionException {

        if (isBluetoothConnected()) {
            try {
                Log.d(MainActivity.LOG_TAG, "writeContentOnSocket, 70: Writing " + bytes.length + " byte(s) on socket.");
                bluetoothSocket.getOutputStream().write(bytes);
                bluetoothSocket.getOutputStream().flush();
            } catch (IOException ioException) {
                String exceptionMessage = "Error writing content on socket.";
                throw new ConnectionException(exceptionMessage, ioException);
            }
        } else {
            throw CONNECTION_IS_CLOSED_EXCEPTION;
        }


    }

    public void closeConnection() throws ConnectionException {
        if (isBluetoothConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException ioException) {
                String exceptionMessage = "Error closing socket connection.";
                throw new ConnectionException(exceptionMessage, ioException);
            }
        }
    }

    private boolean isBluetoothConnected() {
        return bluetoothSocket.isConnected();
    }
}
