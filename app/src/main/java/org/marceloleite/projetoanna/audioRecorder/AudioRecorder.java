package org.marceloleite.projetoanna.audioRecorder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.commander.Commander;
import org.marceloleite.projetoanna.audioRecorder.commander.CommanderException;
import org.marceloleite.projetoanna.audioRecorder.operator.command.Command;
import org.marceloleite.projetoanna.audioRecorder.operator.command.CommandType;
import org.marceloleite.projetoanna.audioRecorder.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.bluetooth.BluetoothException;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class AudioRecorder {

    private Commander commander;

    private MainActivity mainActivity;

    private Bluetooth bluetooth;

    private boolean recording;

    private boolean connected;

    public AudioRecorder(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.recording = false;
        this.commander = null;
        this.bluetooth = new Bluetooth(this);
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public AppCompatActivity getMainActivity() {
        return mainActivity;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isRecording() {
        return recording;
    }

    public void connect() {
        bluetooth.connectToBluetoothService();
    }

    public void disconnect() {
        commander.executeCommand(CommandType.DISCONNECT);
    }

    public void startAudioRecord() {
        if (commander != null) {
            commander.executeCommand(CommandType.START_AUDIO_RECORD);
        }
    }

    public void stopRecord() {
        if (commander != null) {
            commander.executeCommand(CommandType.STOP_AUDIO_RECORD);
        }
    }

    public void enableBluetoothResult(int resultCode) {
        bluetooth.enableBluetoothResult(resultCode);
    }

    public void bluetoothConnectionProcessConcluded() {

        if (bluetooth.getBluetoothSocket() != null) {
            commander = new Commander(this, bluetooth.getBluetoothSocket());
            connected = true;
            Toast.makeText(mainActivity, "Connected with \"" + bluetooth.getBluetoothDevice().getName() + "\".", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetooth.getBluetoothDevice() != null) {
                showConnectionError();
            }
        }
        mainActivity.updateInterface();
    }

    public void checkCommandResult(Command command) {
        if (command != null) {
            switch (command.getCommandType()) {
                case START_AUDIO_RECORD:
                    checkStartAudioRecordCommandResult(command);
                    break;
                case STOP_AUDIO_RECORD:
                    checkStopAudioRecordCommandResult(command);
                    break;
                case DISCONNECT:
                    checkDisconnectCommandResult(command);
                    break;
                case FINISH_EXECUTION:
                    checkFinishExecutionCommandResult(command);
                    break;
                default:
                    Log.e(MainActivity.LOG_TAG, "checkCommandResult, 39: Unknown command \"" + command.getCommandType().getTitle() + "\".");
                    break;
            }
        }
    }

    private void checkStartAudioRecordCommandResult(Command command) {
        switch (command.getCommandResult()) {
            case VALUE_RETURNED:
                switch (command.getReturnValue()) {
                    case GenericReturnCodes.SUCCESS:
                        Toast.makeText(mainActivity, "Audio record started.", Toast.LENGTH_SHORT).show();
                    case GenericReturnCodes.GENERIC_ERROR:
                        Toast.makeText(mainActivity, "Could not start audio record.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e(MainActivity.LOG_TAG, "startAudioRecordCommandResult, 111: Unknown return value received from \"start record\" command: " + command.getReturnValue() + ".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = command.getThrowable();
                Log.e(MainActivity.LOG_TAG, "startAudioRecordCommandResult, 116: Start audio command returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(mainActivity, "Could not start audio record.", Toast.LENGTH_SHORT).show();
        }
        mainActivity.updateInterface();
    }

    private void checkStopAudioRecordCommandResult(Command command) {
        switch (command.getCommandResult()) {
            case VALUE_RETURNED:
                switch (command.getReturnValue()) {
                    case GenericReturnCodes.SUCCESS:
                        Toast.makeText(mainActivity, "Audio record finished.", Toast.LENGTH_SHORT).show();
                    case GenericReturnCodes.GENERIC_ERROR:
                        Toast.makeText(mainActivity, "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e(MainActivity.LOG_TAG, "checkStopAudioRecordCommandResult, 76:  Unknown return value received from \"stop record\" command: " + command.getReturnValue() + ".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = command.getThrowable();
                Log.e(MainActivity.LOG_TAG, "checkStopAudioRecordCommandResult, 86:  Stop audio command returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(mainActivity, "Could not stop audio record.", Toast.LENGTH_SHORT).show();
        }
        mainActivity.updateInterface();
    }

    private void checkDisconnectCommandResult(Command command) {
        Log.e(MainActivity.LOG_TAG, "checkDisconnectCommandResult, 169: \"" + command.getCommandType().getTitle() + "\" not implemented yet.");
    }

    private void checkFinishExecutionCommandResult(Command command) {
        Log.e(MainActivity.LOG_TAG, "checkDisconnectCommandResult, 173: \"" + command.getCommandType().getTitle() + "\" not implemented yet.");
    }

    private void showConnectionError() {
        String toastMessage;
        if (bluetooth.getBluetoothDevice() != null) {
            toastMessage = "Could not connect to \"" + bluetooth.getBluetoothDevice().getName() + "\".";

        } else {
            toastMessage = "Could not connect wiht audio record device.";
        }
        Toast.makeText(mainActivity, toastMessage, Toast.LENGTH_LONG).show();
    }
}
