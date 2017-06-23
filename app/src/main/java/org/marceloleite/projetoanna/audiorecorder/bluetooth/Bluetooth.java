package org.marceloleite.projetoanna.audiorecorder.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDeviceParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.connector.AsyncTaskConnectWithDeviceResult;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.AlertDialogStartPairing;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.pairer.Pairer;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.AlertDialogSelectBluetoothDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.selectdevice.SelectBluetoothDeviceInterface;
import org.marceloleite.projetoanna.utils.Log;

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

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The UUID used to identify the audio recorder bluetooth service.
     */
    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("f5934b96-0110-11e6-8d22-5e5517507c66");

    /**
     * The code used to identify the intent to request the bluetooth activation.
     */
    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0x869a;

    /**
     * Executes the bluetooth pairing with a device.
     */
    private Pairer pairer;

    /**
     * The objects which contains the bluetooth connection paramters and the method to be executed after the connection attempt concludes.
     */
    private BluetoothConnectionInterface bluetoothConnectionInterface;

    /**
     * The device's bluetooth adapter.
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * The device which a bluetooth connection is stablished.
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * The bluetooth socket which represents the connection between this application and the remote device.
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * An asynchronous task to establish the connection with the remove device.
     */
    private AsyncTaskConnectWithDevice asyncTaskConnectWithDevice;

    /**
     * Object constructor.
     *
     * @param bluetoothConnectionInterface
     */
    public Bluetooth(BluetoothConnectionInterface bluetoothConnectionInterface) {
        this.bluetoothDevice = null;
        this.bluetoothSocket = null;
        this.bluetoothConnectionInterface = bluetoothConnectionInterface;
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
            Log.e(LOG_TAG, "connectWithAudioRecorder (82): This device does not have a bluetooth adapter.");
            bluetoothConnectionInterface.bluetoothConnectionResult(BluetoothConnectReturnCodes.GENERIC_ERROR);
        }
    }

    /**
     * Connects with the device.
     */
    private void connectWithDevice() {
        AppCompatActivity appCompatActivity = bluetoothConnectionInterface.getBluetoothConnectionParameters().getAppCompatActivity();
        ViewGroup viewGroup = bluetoothConnectionInterface.getBluetoothConnectionParameters().getViewGroup();
        AsyncTaskConnectWithDeviceParameters asyncTaskConnectWithDeviceParameters = new AsyncTaskConnectWithDeviceParameters(bluetoothDevice, appCompatActivity, viewGroup);
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
        bluetoothConnectionInterface.bluetoothConnectionResult(result);
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
            Log.e(Bluetooth.class, LOG_TAG, "checkDeviceHasBluetoothAdapter (127): This device does not have a bluetooth adapter.");
            return false;
        }
        return true;
    }

    private boolean isBluetoothAdapterActivated() {
        if (bluetoothAdapter == null) {
            Log.e(Bluetooth.class, LOG_TAG, "isBluetoothAdapterActivated (135): This device does not have a bluetooth adapter.");
            return false;
        } else {
            return bluetoothAdapter.isEnabled();
        }
    }

    private void requestBluetoothAdapterActivation() {
        if (isBluetoothAdapterActivated()) {
            Log.d(Bluetooth.class, LOG_TAG, "requestBluetoothAdapterActivation (144): Bluetooth adapter is already active.");
        } else {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothConnectionInterface.getAppCompatActivity().startActivityForResult(enableBluetoothIntent, Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    public void requestBluetoothAdapterActivationResult(int resultCode) {
        switch (resultCode) {
            case AppCompatActivity.RESULT_OK:
                Log.d(Bluetooth.class, LOG_TAG, "requestBluetoothAdapterActivationResult (154): Bluetooth activated.");
                checkDeviceToConnect();
                break;
            default:
                Log.d(Bluetooth.class, LOG_TAG, "requestBluetoothAdapterActivationResult (158): Bluetooth not activated.");
                bluetoothConnectionInterface.bluetoothConnectionResult(BluetoothConnectReturnCodes.CONNECTION_CANCELLED);
                break;
        }
    }

    public void startDeviceDiscover() {
        pairer = new Pairer(this);
        pairer.startDeviceDiscovery();
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return bluetoothConnectionInterface.getAppCompatActivity();
    }

    @Override
    public void bluetoothDeviceSelected(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;

        if (bluetoothDevice != null) {
            Log.d(Bluetooth.class, LOG_TAG, "bluetoothDeviceSelected (179): Bluetooth device is " + bluetoothDevice.getAddress());
            connectWithDevice();
        } else {
            Log.d(Bluetooth.class, LOG_TAG, "bluetoothDeviceSelected (182): User didn't select any device.");
            bluetoothConnectionInterface.bluetoothConnectionResult(BluetoothConnectReturnCodes.CONNECTION_CANCELLED);
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
                Log.e(Bluetooth.class, LOG_TAG, "disconnectFromDevice (207): Error while closing socket with device.");
                ioException.printStackTrace();
            }
        }

        this.bluetoothSocket = null;
    }
}
