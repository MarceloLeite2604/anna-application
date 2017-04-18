package org.marceloleite.projetoanna.bluetooth.connection;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.bluetooth.ConnectionException;
import org.marceloleite.projetoanna.bluetooth.utils.GenericReturnCodes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class Connection {

    private static final ConnectionException CONNECTION_IS_CLOSED_EXCEPTION = new ConnectionException("Bluetooth connection is closed.");

    private BluetoothSocket bluetoothSocket;

    private InputStream inputStream;

    private OutputStream outputStream;

    public Connection(BluetoothSocket bluetoothSocket) throws ConnectionException {

        this.bluetoothSocket = bluetoothSocket;
        try {
            this.inputStream = bluetoothSocket.getInputStream();
            this.outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException ioException) {
            String exceptionMessage = "Error creating Connection object.";
            throw new ConnectionException(exceptionMessage, ioException);
        }

    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public ReadSocketContentResult readSocketContent() throws ConnectionException {

        if (isBluetoothConnected()) {

            try {
                ReadSocketContentResult readSocketContentResult = null;
                int totalBytesAvailable = inputStream.available();
                if (totalBytesAvailable > 0) {
                    byte[] readedBytes = new byte[totalBytesAvailable];
                    inputStream.read(readedBytes);
                    readSocketContentResult = new ReadSocketContentResult(ConnectionReturnCodes.SUCCESS, readedBytes);
                } else {
                    readSocketContentResult = new ReadSocketContentResult(ConnectionReturnCodes.NO_CONTENT_TO_READ, null);
                }
                return readSocketContentResult;
            } catch (IOException ioException) {
                String exceptionMessage = "Error reading socket content.";
                throw new ConnectionException(exceptionMessage, ioException);
            }
        } else {
            throw CONNECTION_IS_CLOSED_EXCEPTION;
        }


    }

    public void writeContentOnSocket(byte[] bytes) throws ConnectionException {

        if (isBluetoothConnected()) {
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(bytes);
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
