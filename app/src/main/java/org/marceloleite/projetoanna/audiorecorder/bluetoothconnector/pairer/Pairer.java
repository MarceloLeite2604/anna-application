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
 * Controls the pairing process with bluetooth devices.
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

    /**
     * The alert dialog which informs the user that the pairing with the selected bluetooth device
     * is being executed.
     */
    private AlertDialog alertDialogPairingDevice;

    /**
     * A {@link android.content.BroadcastReceiver} which receives Android information about the
     * pairing process.
     */
    private BroadcastReceiverPairBluetoothDevice broadcastReceiverPairBluetoothDevice;

    /**
     * The parameters informed to construct this object.
     */
    private PairerParameters pairerParameters;

    /**
     * The object which contains the parameters informed to create this object and the method to be
     * executed once the pairing process is concluded.
     */
    private PairerInterface pairerInterface;

    /**
     * The bluetooth device to pair with.
     */
    private BluetoothDevice bluetoothDeviceToPair;

    /**
     * Constructor.
     *
     * @param pairerInterface The object which contains the parameters informed to create this object and the method to be
     *                        executed once the pairing process is concluded.
     */
    public Pairer(PairerInterface pairerInterface) {
        this.pairerInterface = pairerInterface;
        this.pairerParameters = pairerInterface.getPairerParameters();
    }

    /**
     * Starts the pairing process.
     */
    public void startPairingProcess() {
        new AlertDialogStartDiscovering(this);

    }

    /**
     * Start the discovery of visible bluetooth devices to pair with.
     */
    private void startDiscoveringDevices() {
        Discoverer discoverer = new Discoverer(this);
        discoverer.startDeviceDiscover();
    }

    /**
     * Starts the pairing process with the selected device.
     */
    private void pairWithDevice() {
        if (bluetoothDeviceToPair != null) {
            registerBroadcastReceiver();
            this.bluetoothDeviceToPair.createBond();
        }
    }

    /**
     * Registers on Android a broadcast receiver which controls the messages received about the
     * pairing with the selected bluetooth device.
     */
    private void registerBroadcastReceiver() {
        if (broadcastReceiverPairBluetoothDevice == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            broadcastReceiverPairBluetoothDevice = new BroadcastReceiverPairBluetoothDevice(this);
            pairerParameters.getAppCompatActivity().registerReceiver(broadcastReceiverPairBluetoothDevice, intentFilter);
        }
    }

    @Override
    public void pairingStarted() {
        Log.d(LOG_TAG, "pairingStarted (99): ");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pairerParameters.getAppCompatActivity());
        alertDialogBuilder.setTitle("Pairing");
        alertDialogPairingDevice = alertDialogBuilder.create();
        String infromationText = "Pairing with \"" + bluetoothDeviceToPair.getName() + "\".";
        InformationView informationView = new InformationView(alertDialogPairingDevice.getContext(), infromationText);
        alertDialogPairingDevice.setView(informationView);
        alertDialogPairingDevice.show();
    }

    @Override
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

    /**
     * Unregisters the broadcast receiver which controls the messages received about the pairing
     * with the selected bluetooth device.
     */
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
