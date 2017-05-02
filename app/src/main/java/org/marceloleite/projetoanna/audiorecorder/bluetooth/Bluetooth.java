package org.marceloleite.projetoanna.audiorecorder.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.AlertDialogStartPairing;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.Pairer;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectToDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectToDeviceParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectToDeviceResponse;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.AlertDialogSelectBluetoothDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.SelectDeviceInterface;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Responsible for bluetooth operations such as check bluetooth adapter, pairing with other devices and connect to a service.
 */
public class Bluetooth implements SelectDeviceInterface, AsyncTaskConnectToDeviceParameters, AsyncTaskConnectToDeviceResponse {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Bluetooth.class.getSimpleName();

    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    private Pairer pairer;

    private AudioRecorder audioRecorder;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    private AsyncTaskConnectToDevice asyncTaskConnectToDevice;


    public Bluetooth(AudioRecorder audioRecorder) {
        this.bluetoothDevice = null;
        this.bluetoothSocket = null;
        this.audioRecorder = audioRecorder;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void enableBluetoothResult(int resultCode) {
        switch (resultCode) {
            case AppCompatActivity.RESULT_OK:
                Log.d(LOG_TAG, "enableBluetoothResult, 55: Bluetooth activated.");
                connect();
                break;
            default:
                Log.d(LOG_TAG, "enableBluetoothResult, 59: Bluetooth not activated.");
                audioRecorder.getAudioRecordActivityInterface().updateInterface();
                break;
        }
    }

    public void connect() {

        if (checkBluetoothAdapter() == true) {
            if (checkDeviceToConnect() == true) {
                connectToDevice();
            }
        }
    }

    private void connectToDevice() {

        if (asyncTaskConnectToDevice == null) {
            asyncTaskConnectToDevice = new AsyncTaskConnectToDevice(this, this);
        }

        if (asyncTaskConnectToDevice.getStatus() == AsyncTask.Status.PENDING || asyncTaskConnectToDevice.getStatus() == AsyncTask.Status.FINISHED) {
            Log.d(LOG_TAG, "connect, 77: Connecting to device " + bluetoothDevice.getAddress());
            asyncTaskConnectToDevice.execute();
        } else {
            Log.d(LOG_TAG, "connect, 86: Connection with device concluded.");
            asyncTaskConnectToDevice = null;
            audioRecorder.bluetoothConnectionProcessConcluded();
        }
    }

    private boolean checkBluetoothAdapter() {
        if (checkDeviceHasBluetoothAdapter() == false) {
            String exceptionMessage = "This device does not have a bluetooth adapter.";
            Log.d(LOG_TAG, "connect, 70: " + exceptionMessage);
            // throw new BluetoothException(exceptionMessage);
            return false;
        }

        if (isBluetoothAdapterActivated() == false) {
            requestBluetoothAdapterActivation();
            return false;
        }
        return true;
    }

    private boolean checkDeviceToConnect() {
        if (bluetoothDevice == null) {
            selectBluetoothDeviceToConnect();
            Log.d(LOG_TAG, "connect, 55: No device to connect.");
            return false;
        }
        return true;
    }

    private void selectBluetoothDeviceToConnect() {
        Set<BluetoothDevice> pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();

        if (pairedBluetoothDevices.size() > 0) {
            new AlertDialogSelectBluetoothDevice(this).show();
        } else {
            new AlertDialogStartPairing(this).show();
        }
    }

    private boolean checkDeviceHasBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            String exceptionMessage = "This device does not have a bluetooth adapter.";
            Log.d(LOG_TAG, "connect, 70: " + exceptionMessage);
            // throw new BluetoothException(exceptionMessage);
            return false;
        }
        return true;
    }

    private boolean isBluetoothAdapterActivated() {
        if (bluetoothAdapter == null) {
            String exceptionMessage = "This device does not have a bluetooth adapter.";
            Log.d(LOG_TAG, "connect, 70: " + exceptionMessage);
            // throw new BluetoothException(exceptionMessage);
            return false;
        } else {
            return bluetoothAdapter.isEnabled();
        }
    }

    private void requestBluetoothAdapterActivation() {
        if (isBluetoothAdapterActivated()) {
            Log.d(LOG_TAG, "activateBluetoothAdapter, 124: Bluetooth adapter is already active.");
        } else {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            audioRecorder.getAudioRecordActivityInterface().getActivity().startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    public void startDeviceDiscover() {
        pairer = new Pairer(this);
        pairer.startDeviceDiscovery();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        connect();
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return audioRecorder.getAudioRecordActivityInterface().getActivity();
    }

    @Override
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;

        if (bluetoothDevice != null) {
            Log.d(LOG_TAG, "setBluetoothDevice, 93: Bluetooth device is " + bluetoothDevice.getAddress());
            connectToDevice();
        } else {
            Log.d(LOG_TAG, "setBluetoothDevice, 191: User didn't select any device.");
            audioRecorder.getAudioRecordActivityInterface().updateInterface();
        }
    }

    @Override
    public void connectToDeviceProcessFinished(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        connect();
    }

    public static void fillBluetoothDeviceInformations(View view, BluetoothDevice bluetoothDevice) {
        TextView textViewBluetoothDeviceName = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_name);
        TextView textViewBluetoothDeviceAddress = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_address);
        textViewBluetoothDeviceName.setText(bluetoothDevice.getName());
        textViewBluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public boolean isConnected() {
        if (bluetoothSocket == null) {
            return false;
        } else {
            return bluetoothSocket.isConnected();
        }
    }

    public void disconnectFromDevice() {
        if (this.bluetoothSocket.isConnected() == true) {
            try {
                this.bluetoothSocket.close();
            } catch (IOException ioException) {
                Log.e(LOG_TAG, "disconnectFromDevice, 227: Error while closing socket with device.");
                ioException.printStackTrace();
            }
        }

        this.bluetoothSocket = null;
    }
}
