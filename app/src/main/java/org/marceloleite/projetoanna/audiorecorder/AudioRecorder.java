package org.marceloleite.projetoanna.audiorecorder;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.commander.Commander;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandTask;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandType;
import org.marceloleite.projetoanna.audiorecorder.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.bluetooth.Bluetooth;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class AudioRecorder {

    private static final String LOG_TAG = AudioRecorder.class.getSimpleName();

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
                String toastMessage;
                if (bluetooth.getBluetoothDevice() != null) {
                    toastMessage = "Could not connect to \"" + bluetooth.getBluetoothDevice().getName() + "\".";

                } else {
                    toastMessage = "Could not connect wiht audio record device.";
                }
                Toast.makeText(mainActivity, toastMessage, Toast.LENGTH_LONG).show();
            }
        }
        mainActivity.updateInterface();
    }

    public void checkCommandResult(CommandTask commandTask) {
        if (commandTask != null) {
            switch (commandTask.getCommandType()) {
                case START_AUDIO_RECORD:
                    checkStartAudioRecordCommandResult(commandTask);
                    break;
                case STOP_AUDIO_RECORD:
                    checkStopAudioRecordCommandResult(commandTask);
                    break;
                case DISCONNECT:
                    checkDisconnectCommandResult(commandTask);
                    break;
                case FINISH_EXECUTION:
                    checkFinishExecutionCommandResult(commandTask);
                    break;
                default:
                    Log.e(LOG_TAG, "checkCommandResult, 39: Unknown commandTask \"" + commandTask.getCommandType() + "\".");
                    break;
            }
        }
    }

    private void checkStartAudioRecordCommandResult(CommandTask commandTask) {
        switch (commandTask.getCommandResultType()) {
            case VALUE_RETURNED:
                switch (commandTask.getReturnValue()) {
                    case GenericReturnCodes.SUCCESS:
                        Toast.makeText(mainActivity, "Audio record started.", Toast.LENGTH_SHORT).show();
                        recording = true;
                        break;
                    case GenericReturnCodes.GENERIC_ERROR:
                        Toast.makeText(mainActivity, "Could not start audio record.", Toast.LENGTH_SHORT).show();
                        recording = false;
                        break;
                    default:
                        Log.e(LOG_TAG, "startAudioRecordCommandResult, 111: Unknown return value received from \"start record\" commandTask: " + commandTask.getReturnValue() + ".");
                        break;
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = commandTask.getThrowable();
                Log.e(LOG_TAG, "startAudioRecordCommandResult, 116: Start audio commandTask returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(mainActivity, "Could not start audio record.", Toast.LENGTH_SHORT).show();
                recording = false;
                break;
        }
        mainActivity.updateInterface();
    }

    private void checkStopAudioRecordCommandResult(CommandTask commandTask) {
        switch (commandTask.getCommandResultType()) {
            case VALUE_RETURNED:
                switch (commandTask.getReturnValue()) {
                    case GenericReturnCodes.SUCCESS:
                        Toast.makeText(mainActivity, "Audio record finished.", Toast.LENGTH_SHORT).show();
                        recording = false;
                        break;
                    case GenericReturnCodes.GENERIC_ERROR:
                        Toast.makeText(mainActivity, "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                        recording = true;
                        break;
                    default:
                        Log.e(LOG_TAG, "checkStopAudioRecordCommandResult, 76:  Unknown return value received from \"stop record\" commandTask: " + commandTask.getReturnValue() + ".");
                        break;
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = commandTask.getThrowable();
                Log.e(LOG_TAG, "checkStopAudioRecordCommandResult, 86:  Stop audio commandTask returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(mainActivity, "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                recording = true;
                break;
        }
        mainActivity.updateInterface();
    }

    private void checkDisconnectCommandResult(CommandTask commandTask) {
        Log.e(LOG_TAG, "checkDisconnectCommandResult, 169: \"" + commandTask.getCommandType() + "\" not implemented yet.");
    }

    private void checkFinishExecutionCommandResult(CommandTask commandTask) {
        Log.e(LOG_TAG, "checkDisconnectCommandResult, 173: \"" + commandTask.getCommandType() + "\" not implemented yet.");
    }

}
