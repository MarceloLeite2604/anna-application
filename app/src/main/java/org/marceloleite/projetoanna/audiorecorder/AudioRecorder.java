package org.marceloleite.projetoanna.audiorecorder;

import android.util.Log;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.BluetoothInterface;
import org.marceloleite.projetoanna.audiorecorder.operator.Operator;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Command;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;

import java.io.File;
import java.io.InvalidObjectException;

/**
 * Controls the audio recorder device.
 */
public class AudioRecorder implements BluetoothInterface {

    /**
     * Tag to identify log messages written by this class.
     */
    private static final String LOG_TAG = AudioRecorder.class.getSimpleName();

    /**
     * The audio recorder operator.
     */
    private Operator operator;

    /**
     * The activity which instantiates the audio recorder class.
     */
    private AudioRecordActivityInterface audioRecordActivityInterface;

    /**
     * Controls the bluetooth activation, pairing and connection.
     */
    private Bluetooth bluetooth;

    /**
     * Indicates if audio record device is currently recording.
     */
    private boolean recording;

    private File latestAudioFile;

    /**
     * Instantiates the Audio Recorder class.
     *
     * @param audioRecordActivityInterface The activity which request the Audio Record Instantiation.
     */
    public AudioRecorder(AudioRecordActivityInterface audioRecordActivityInterface) {
        this.audioRecordActivityInterface = audioRecordActivityInterface;
        this.recording = false;
        this.operator = null;
        this.bluetooth = new Bluetooth(this);
    }

    public AudioRecordActivityInterface getAudioRecordActivityInterface() {
        return audioRecordActivityInterface;
    }

    public boolean isConnected() {
        return bluetooth.isConnected();
    }

    public boolean isRecording() {
        return recording;
    }

    public File getLatestAudioFile() {
        return latestAudioFile;
    }

    public void connect() {
        bluetooth.connect();
    }

    public void disconnect() {
        operator.executeCommand(Command.DISCONNECT);
    }

    public void startAudioRecord() {
        if (operator != null) {
            operator.executeCommand(Command.START_AUDIO_RECORD);
        }
    }

    public void stopRecord() {
        if (operator != null) {
            operator.executeCommand(Command.STOP_AUDIO_RECORD);
        }
    }

    public void requestLatestAudioFile() {
        if (operator != null) {
            operator.executeCommand(Command.REQUEST_LATEST_AUDIO_FILE);
        }
    }

    public void enableBluetoothResult(int resultCode) {
        bluetooth.enableBluetoothResult(resultCode);
    }

    @Override
    public void bluetoothConnectionProcessConcluded() {

        if (bluetooth.getBluetoothSocket() != null) {
            operator = new Operator(this, this.bluetooth);
            Toast.makeText(audioRecordActivityInterface.getActivity(), "Connected with \"" + bluetooth.getBluetoothDevice().getName() + "\".", Toast.LENGTH_SHORT).show();
            operator.startExecution();
        } else {
            if (bluetooth.getBluetoothDevice() != null) {
                String toastMessage;
                if (bluetooth.getBluetoothDevice() != null) {
                    toastMessage = "Could not connect to \"" + bluetooth.getBluetoothDevice().getName() + "\".";

                } else {
                    toastMessage = "Could not connect with audio record device.";
                }
                Toast.makeText(audioRecordActivityInterface.getActivity(), toastMessage, Toast.LENGTH_LONG).show();
            }
        }
        audioRecordActivityInterface.updateInterface();
    }

    public void checkOperationResult(Operation operation) {
        if (operation != null) {

            switch (operation.getCommand()) {
                case START_AUDIO_RECORD:
                    checkStartAudioRecordCommandResult(operation);
                    break;
                case STOP_AUDIO_RECORD:
                    checkStopAudioRecordCommandResult(operation);
                    break;
                case DISCONNECT:
                    checkDisconnectCommandResult(operation);
                    break;
                case REQUEST_LATEST_AUDIO_FILE:
                    checkRequestLatestAudioFileResult(operation);
                    break;
                case FINISH_EXECUTION:
                    checkFinishExecutionCommandResult(operation);
                    break;
                default:
                    Log.e(LOG_TAG, "checkOperationResult, 39: Unknown operation \"" + operation.getCommand() + "\".");
                    break;
            }
        }
    }

    private void checkStartAudioRecordCommandResult(Operation operation) {
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    Integer returnObject = (Integer) operation.getReturnObject();
                    switch (returnObject) {
                        case GenericReturnCodes.SUCCESS:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Audio record started.", Toast.LENGTH_SHORT).show();
                            recording = true;
                            break;
                        case GenericReturnCodes.GENERIC_ERROR:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Could not start audio record.", Toast.LENGTH_SHORT).show();
                            recording = false;
                            break;
                        default:
                            Log.e(LOG_TAG, "startAudioRecordCommandResult, 111: Unknown return value received from \"start record\" operation: " + operation.getReturnObject() + ".");
                            break;
                    }
                } else {
                    Log.e(LOG_TAG, "checkStartAudioRecordCommandResult, 164: Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "startAudioRecordCommandResult, 116: Start audio operation returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(audioRecordActivityInterface.getActivity(), "Could not start audio record.", Toast.LENGTH_SHORT).show();
                recording = false;
                break;
        }
        audioRecordActivityInterface.updateInterface();
    }

    private void checkStopAudioRecordCommandResult(Operation operation) {
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    Integer returnObject = (Integer) operation.getReturnObject();
                    switch (returnObject) {
                        case GenericReturnCodes.SUCCESS:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Audio record finished.", Toast.LENGTH_SHORT).show();
                            recording = false;
                            break;
                        case GenericReturnCodes.GENERIC_ERROR:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                            recording = true;
                            break;
                        default:
                            Log.e(LOG_TAG, "checkStopAudioRecordCommandResult, 76:  Unknown return value received from \"stop record\" operation: " + operation.getReturnObject() + ".");
                            break;
                    }
                } else {
                    Log.e(LOG_TAG, "checkStopAudioRecordCommandResult, 198: Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkStopAudioRecordCommandResult, 86:  Stop audio operation returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(audioRecordActivityInterface.getActivity(), "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                recording = true;
                break;
        }
        audioRecordActivityInterface.updateInterface();
    }

    private void checkDisconnectCommandResult(Operation operation) {
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    Integer returnObject = (Integer) operation.getReturnObject();
                    switch (returnObject) {
                        case GenericReturnCodes.SUCCESS:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Disconnected from " + bluetooth.getBluetoothDevice().getName() + ".", Toast.LENGTH_SHORT).show();
                            break;
                        case GenericReturnCodes.GENERIC_ERROR:
                            Toast.makeText(audioRecordActivityInterface.getActivity(), "Error while disconnecting from " + bluetooth.getBluetoothDevice().getName() + ".", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.e(LOG_TAG, "checkDisconnectCommandResult, 204: Unknown return value received from \"disconnect\" operation: " + operation.getReturnObject() + ".");
                            break;
                    }
                } else {
                    Log.e(LOG_TAG, "checkDisconnectCommandResult, 219: Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkDisconnectCommandResult, 210: Disconnect command returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(audioRecordActivityInterface.getActivity(), "Error while disconnecting from " + bluetooth.getBluetoothDevice().getName() + ".", Toast.LENGTH_SHORT).show();
                break;
        }

        operator.finishExecution();
        bluetooth.disconnectFromDevice();
        audioRecordActivityInterface.updateInterface();
    }

    private void checkRequestLatestAudioFileResult(Operation operation) {
        Log.d(LOG_TAG, "checkRequestLatestAudioFileResult, 257: Received latest audio file.");
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                Log.d(LOG_TAG, "checkRequestLatestAudioFileResult, 260: Class returned: " + returnObjectClass.getName());
                if (returnObjectClass == File.class) {
                    latestAudioFile = (File) operation.getReturnObject();
                    Log.d(LOG_TAG, "checkRequestLatestAudioFileResult, 264: Latest audio file: " + latestAudioFile);
                } else {
                    Log.e(LOG_TAG, "checkDisconnectCommandResult, 219: Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkDisconnectCommandResult, 210: Disconnect command returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(audioRecordActivityInterface.getActivity(), "Error while disconnecting from " + bluetooth.getBluetoothDevice().getName() + ".", Toast.LENGTH_SHORT).show();
                break;
        }

        audioRecordActivityInterface.receiveLatestAudioFileConcluded();
    }

    private void checkFinishExecutionCommandResult(Operation operation) {
        Log.e(LOG_TAG, "checkDisconnectCommandResult, 173: \"" + operation.getCommand() + "\" not implemented yet.");
    }

    public void connectionLost() {
        Log.d(LOG_TAG, "connectionLost, 284: Lost connection with audio recorder.");
    }
}
