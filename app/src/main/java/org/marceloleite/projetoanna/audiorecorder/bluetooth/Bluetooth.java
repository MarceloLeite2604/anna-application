package org.marceloleite.projetoanna.audiorecorder.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.AlertDialogStartPairing;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.Pairer;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDeviceParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDeviceResult;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.AlertDialogSelectBluetoothDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.SelectBluetoothDeviceInterface;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Responsible for bluetooth operations such as check bluetooth adapter, pairing with other devices and connectWithAudioRecorder to a service.
 */
public class Bluetooth implements SelectBluetoothDeviceInterface, AsyncTaskConnectWithDeviceResult {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Bluetooth.class.getSimpleName();

    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    private Pairer pairer;

    private BluetoothInterface bluetoothInterface;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    private AsyncTaskConnectWithDevice asyncTaskConnectWithDevice;


    public Bluetooth(BluetoothInterface bluetoothInterface) {
        this.bluetoothDevice = null;
        this.bluetoothSocket = null;
        this.bluetoothInterface = bluetoothInterface;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void connectWithAudioRecorder() {
        if (checkDeviceHasBluetoothAdapter()) {
            if (isBluetoothAdapterActivated()) {
                checkDeviceToConnect();
            } else {
                requestBluetoothAdapterActivation();
            }
        } else {
            Log.e(LOG_TAG, "connectWithAudioRecorder, 84: This device does not have a bluetooth adapter.");
            bluetoothInterface.connectWithAudioRecorderResult(BluetoothConnectReturnCodes.GENERIC_ERROR);
        }
    }

    private void connectWithDevice() {

        AppCompatActivity appCompatActivity = bluetoothInterface.getAppCompatActivity();
        AsyncTaskConnectWithDeviceParameters asyncTaskConnectWithDeviceParameters = new AsyncTaskConnectWithDeviceParameters(bluetoothDevice, appCompatActivity);
        asyncTaskConnectWithDevice = new AsyncTaskConnectWithDevice(asyncTaskConnectWithDeviceParameters, this);
        asyncTaskConnectWithDevice.execute();
    }

    @Override
    public void connectWithDeviceProcessFinished(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        int result;
        if (bluetoothSocket != null) {
            result = BluetoothConnectReturnCodes.SUCCESS;
        } else {
            result = BluetoothConnectReturnCodes.GENERIC_ERROR;
        }
        bluetoothInterface.connectWithAudioRecorderResult(result);
    }

    private void checkDeviceToConnect() {
        if (bluetoothDevice == null) {
            selectBluetoothDeviceToConnect();
        } else {
            bluetoothDeviceSelected(bluetoothDevice);
        }
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
            Log.e(LOG_TAG, "isBluetoothAdapterActivated, 135: This device does not have a bluetooth adapter.");
            return false;
        }
        return true;
    }

    private boolean isBluetoothAdapterActivated() {
        if (bluetoothAdapter == null) {
            Log.e(LOG_TAG, "isBluetoothAdapterActivated, 144: This device does not have a bluetooth adapter.");
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
            bluetoothInterface.getAppCompatActivity().startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    public void requestBluetoothAdapterActivationResult(int resultCode) {
        switch (resultCode) {
            case AppCompatActivity.RESULT_OK:
                Log.d(LOG_TAG, "enableBluetoothActivityResult, 55: Bluetooth activated.");
                checkDeviceToConnect();
                break;
            default:
                Log.d(LOG_TAG, "enableBluetoothActivityResult, 59: Bluetooth not activated.");
                bluetoothInterface.connectWithAudioRecorderResult(BluetoothConnectReturnCodes.CONNECTION_CANCELLED);
                break;
        }
    }

    public void startDeviceDiscover() {
        pairer = new Pairer(this);
        pairer.startDeviceDiscovery();
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return bluetoothInterface.getAppCompatActivity();
    }

    @Override
    public void bluetoothDeviceSelected(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;

        if (bluetoothDevice != null) {
            Log.d(LOG_TAG, "bluetoothDeviceSelected, 93: Bluetooth device is " + bluetoothDevice.getAddress());
            connectWithDevice();
        } else {
            Log.d(LOG_TAG, "bluetoothDeviceSelected, 191: User didn't select any device.");
            bluetoothInterface.connectWithAudioRecorderResult(BluetoothConnectReturnCodes.CONNECTION_CANCELLED);
        }
    }

    public static void fillBluetoothDeviceInformations(View view, BluetoothDevice bluetoothDevice) {
        TextView textViewBluetoothDeviceName = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_name);
        TextView textViewBluetoothDeviceAddress = (TextView) view.findViewById(R.id.text_view_bluetooth_device_row_address);
        textViewBluetoothDeviceName.setText(bluetoothDevice.getName());
        textViewBluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
    }

    public boolean isConnected() {
        if (bluetoothSocket == null) {
            return false;
        } else {
            return bluetoothSocket.isConnected();
        }
    }

    public void disconnectFromDevice() {
        if (this.bluetoothSocket.isConnected()) {
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
