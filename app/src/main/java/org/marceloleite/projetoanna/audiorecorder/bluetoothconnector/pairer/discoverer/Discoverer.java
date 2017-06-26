package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.broadcastreceiver.BroadcastReceiverDiscoverBluetoothDevices;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.broadcastreceiver.DiscoverBluetoothDevicesInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching.AlertDialogSearchingDevices;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching.SearchingDevicesInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.searching.SearchingDevicesParameters;

/**
 * Controls the bluetooth device discovering process.
 */
public class Discoverer implements DiscoverBluetoothDevicesInterface, SearchingDevicesInterface {

    private AlertDialogSearchingDevices alertDialogSearchingDevices;

    private DiscovererInterface discovererInterface;

    private DiscovererParameters discovererParameters;

    private BroadcastReceiverDiscoverBluetoothDevices broadcastReceiverDiscoverBluetoothDevices;

    public Discoverer(DiscovererInterface discovererInterface) {
        this.discovererInterface = discovererInterface;
        this.discovererParameters = discovererInterface.getDiscovererParameters();
        this.alertDialogSearchingDevices = new AlertDialogSearchingDevices(this);
        this.broadcastReceiverDiscoverBluetoothDevices = new BroadcastReceiverDiscoverBluetoothDevices(this);
    }

    private void registerBroadcastReceiver() {
        if (broadcastReceiverDiscoverBluetoothDevices == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            broadcastReceiverDiscoverBluetoothDevices = new BroadcastReceiverDiscoverBluetoothDevices(this);
            discovererParameters.getAppCompatActivity().registerReceiver(broadcastReceiverDiscoverBluetoothDevices, intentFilter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (broadcastReceiverDiscoverBluetoothDevices != null) {
            discovererParameters.getAppCompatActivity().unregisterReceiver(broadcastReceiverDiscoverBluetoothDevices);
            broadcastReceiverDiscoverBluetoothDevices = null;
        }
    }

    @Override
    public void discoverDevicesStarted() {
        alertDialogSearchingDevices.discoveryStarted();
    }

    @Override
    public void discoverDevicesFinished() {
        alertDialogSearchingDevices.discoveryFinished();
    }

    @Override
    public void deviceFound(BluetoothDevice bluetoothDevice) {
        alertDialogSearchingDevices.addDevice(bluetoothDevice);
    }

    @Override
    public void startDeviceDiscover() {
        registerBroadcastReceiver();
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @Override
    public void cancelDeviceDiscover() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        unregisterBroadcastReceiver();
    }

    @Override
    public SearchingDevicesParameters getSearchingDevicesParameters() {
        return new SearchingDevicesParameters(discovererParameters.getAppCompatActivity());
    }

    @Override
    public void deviceSelected(BluetoothDevice bluetoothDeviceSelected) {
        cancelDeviceDiscover();
        DiscoveringResult discoveringResult;
        if (bluetoothDeviceSelected != null) {
            discoveringResult = new DiscoveringResult(BluetoothConnectorReturnCodes.SUCCESS, bluetoothDeviceSelected);
        } else {
            discoveringResult = new DiscoveringResult(BluetoothConnectorReturnCodes.DISCOVERING_CANCELLED, null);
        }
        discovererInterface.discoveringResult(discoveringResult);
    }
}
