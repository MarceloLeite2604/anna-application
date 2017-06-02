package org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ReaderWriter {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ReaderWriter.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(ReaderWriter.class);
    }

    private static final ReaderWriterException CONNECTION_IS_CLOSED_EXCEPTION = new ReaderWriterException("Bluetooth connection is closed.");

    private BluetoothSocket bluetoothSocket;

    private InputStream bluetoothSocketInputStream;

    private OutputStream bluetoothSocketOuptutStream;

    public ReaderWriter(BluetoothSocket bluetoothSocket) throws IOException {

        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothSocketInputStream = bluetoothSocket.getInputStream();
        this.bluetoothSocketOuptutStream = bluetoothSocket.getOutputStream();
    }

    public byte[] readSocketContent() throws ReaderWriterException {
        byte[] bytes = null;
        byte[] previousBytes = null;
        boolean doneReading = false;
        if (isBluetoothConnected()) {
            while (!doneReading) {
                try {
                    int totalBytesAvailable = bluetoothSocketInputStream.available();
                    if (totalBytesAvailable > 0) {
                        /*Log.d(LOG_TAG, "readSocketContent, 37: Total of bytes available on socket: " + totalBytesAvailable);*/
                        if (bytes == null) {
                            bytes = new byte[totalBytesAvailable];
                        } else {
                            previousBytes = ByteBuffer.allocate(bytes.length).put(bytes).array();
                            bytes = new byte[totalBytesAvailable];
                        }

                        int totalRead = bluetoothSocketInputStream.read(bytes);
                        /*Log.d(LOG_TAG, "readSocketContent, 47: Total of bytes read from socket: " + totalRead);*/

                        if (previousBytes != null) {
                            bytes = ByteBuffer.allocate(previousBytes.length + bytes.length).put(previousBytes).put(bytes).array();
                        }
                        /*Log.d(LOG_TAG, "readSocketContent, 52: Total of bytes on buffer: " + bytes.length);*/

                    } else {
                        doneReading = true;
                    }
                } catch (IOException ioException) {
                    String exceptionMessage = "Error reading socket content.";
                    throw new ReaderWriterException(exceptionMessage, ioException);
                }
            }
        } else {
            throw CONNECTION_IS_CLOSED_EXCEPTION;
        }
        /*if (bytes != null) {
            Log.d(LOG_TAG, "readSocketContent, 67: Total os bytes on buffer: " + bytes.length);
        }*/
        return bytes;
    }

    public void writeContentOnSocket(byte[] bytes) throws ReaderWriterException {

        if (isBluetoothConnected()) {
            try {
                /*Log.d(LOG_TAG, "writeContentOnSocket, 70: Writing " + bytes.length + " byte(s) on socket.");*/
                bluetoothSocketOuptutStream.write(bytes);
                bluetoothSocketOuptutStream.flush();
            } catch (IOException ioException) {
                String exceptionMessage = "Error writing content on socket.";
                throw new ReaderWriterException(exceptionMessage, ioException);
            }
        } else {
            throw CONNECTION_IS_CLOSED_EXCEPTION;
        }


    }

    public void closeConnection() throws ReaderWriterException {
        if (isBluetoothConnected()) {
            try {
                bluetoothSocket.close();
            } catch (IOException ioException) {
                String exceptionMessage = "Error closing socket connection.";
                throw new ReaderWriterException(exceptionMessage, ioException);
            }
        }
    }

    private boolean isBluetoothConnected() {
        return bluetoothSocket.isConnected();
    }
}
