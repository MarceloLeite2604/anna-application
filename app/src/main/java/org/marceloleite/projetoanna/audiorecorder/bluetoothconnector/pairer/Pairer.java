package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.IntentFilter;

import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.broadcastreceiver.BroadcastReceiverPairBluetoothDevice;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.broadcastreceiver.PairBluetoothDeviceInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.Discoverer;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.DiscovererInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.DiscovererParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.DiscoveringResult;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start.AlertDialogStartDiscovering;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start.StartDiscoveringInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.pairer.discoverer.start.StartDiscoveringParameters;
import org.marceloleite.projetoanna.ui.InformationView;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class Pairer implements StartDiscoveringInterface, DiscovererInterface, PairBluetoothDeviceInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Pairer.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private AlertDialog alertDialogPairingDevice;

    private BroadcastReceiverPairBluetoothDevice broadcastReceiverPairBluetoothDevice;

    private PairerParameters pairerParameters;

    private PairerInterface pairerInterface;

    private BluetoothDevice bluetoothDeviceToPair;

    public Pairer(PairerInterface pairerInterface) {
        this.pairerInterface = pairerInterface;
        this.pairerParameters = pairerInterface.getPairerParameters();
    }

    public void startPairingProcess() {
        new AlertDialogStartDiscovering(this);

    }

    private void startDiscoveringDevices() {
        Discoverer discoverer = new Discoverer(this);
        discoverer.startDeviceDiscover();
    }

    private void pairWithDevice() {
        if (bluetoothDeviceToPair != null) {
            registerBroadcastReceiver();
            this.bluetoothDeviceToPair.createBond();
        }
    }

    private void registerBroadcastReceiver() {
        if (broadcastReceiverPairBluetoothDevice == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            broadcastReceiverPairBluetoothDevice = new BroadcastReceiverPairBluetoothDevice(this);
            pairerParameters.getAppCompatActivity().registerReceiver(broadcastReceiverPairBluetoothDevice, intentFilter);
        }
    }

    public void pairingStarted() {
        Log.d(LOG_TAG, "pairingStarted (99): ");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pairerParameters.getAppCompatActivity());
        alertDialogBuilder.setTitle("Pairing");
        alertDialogPairingDevice = alertDialogBuilder.create();
        InformationView informationView = new InformationView(alertDialogPairingDevice.getContext());
        informationView.setInformationText("Pairing with \"" + bluetoothDeviceToPair.getName() + "\".");
        alertDialogPairingDevice.setView(informationView);
        alertDialogPairingDevice.show();
    }

    public void pairingFinished(boolean devicePaired) {
        Log.d(LOG_TAG, "pairingFinished (111): ");
        alertDialogPairingDevice.dismiss();
        unregisterBroadcastReceiver();
        PairingResult pairingResult;
        if (devicePaired) {
            pairingResult = new PairingResult(BluetoothConnectorReturnCodes.SUCCESS, bluetoothDeviceToPair);
        } else {
            pairingResult = new PairingResult(BluetoothConnectorReturnCodes.PAIRING_FAILED, bluetoothDeviceToPair);
        }
        pairerInterface.pairingResult(pairingResult);
    }

    private void unregisterBroadcastReceiver() {
        if (broadcastReceiverPairBluetoothDevice != null) {
            pairerParameters.getAppCompatActivity().unregisterReceiver(broadcastReceiverPairBluetoothDevice);
            broadcastReceiverPairBluetoothDevice = null;
        }
    }

    @Override
    public DiscovererParameters getDiscovererParameters() {
        return new DiscovererParameters(pairerParameters.getAppCompatActivity());
    }

    @Override
    public void discoveringResult(DiscoveringResult discoveringResult) {
        if (discoveringResult.getReturnCode() == BluetoothConnectorReturnCodes.SUCCESS) {
            bluetoothDeviceToPair = discoveringResult.getBluetoothDevice();
            Log.d(LOG_TAG, "bluetoothDeviceSelected (179): BluetoothConnector device is " + bluetoothDeviceToPair.getAddress());
            pairWithDevice();
        } else {
            Log.d(LOG_TAG, "bluetoothDeviceSelected (182): User didn't select any device.");
            PairingResult pairingResult = new PairingResult(BluetoothConnectorReturnCodes.DISCOVERING_CANCELLED, null);
            pairerInterface.pairingResult(pairingResult);
        }
    }

    @Override
    public void startDiscoveryResult(int optionSelected) {
        if (optionSelected == DialogInterface.BUTTON_POSITIVE) {
            startDiscoveringDevices();
        }
    }

    @Override
    public StartDiscoveringParameters getStartDiscoveryParameters() {
        return new StartDiscoveringParameters(pairerParameters.getAppCompatActivity());
    }

}
