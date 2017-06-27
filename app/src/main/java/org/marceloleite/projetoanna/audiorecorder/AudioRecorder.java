package org.marceloleite.projetoanna.audiorecorder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.communicator.Communicator;
import org.marceloleite.projetoanna.audiorecorder.communicator.CommunicatorInterface;
import org.marceloleite.projetoanna.audiorecorder.communicator.CommunicatorParameters;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Command;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;

import java.io.File;


/**
 * Controls the audio recorder device.
 */
public class AudioRecorder implements CommunicatorInterface {

    /**
     * Tag to identify log messages written by this class.
     */
    private static final String LOG_TAG = AudioRecorder.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private AudioRecorderInterface audioRecorderInterface;

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    private Context context;

    /**
     * Controls the bluetooth communication.
     */
    private Communicator communicator;

    /**
     * Indicates if audio record device is currently recording.
     */
    private boolean recording;

    private File latestAudioFile;

    private long startRecordCommandDelay;

    /**
     * Instantiates the Audio Recorder class.
     *
     * @param audioRecorderInterface The activity which request the Audio Record Instantiation.
     */
    public AudioRecorder(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket, Context context, AudioRecorderInterface audioRecorderInterface) {
        this.audioRecorderInterface = audioRecorderInterface;
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothSocket = bluetoothSocket;
        this.context = context;
        this.communicator = new Communicator(this);
        this.communicator.startExecution();
        this.recording = false;
    }

    public boolean isConnected() {
        return communicator.isConnected();
    }

    public boolean isRecording() {
        return recording;
    }

    public File getLatestAudioFile() {
        return latestAudioFile;
    }

    public String getAudioRecorderDeviceName() {
        String deviceName = bluetoothDevice.getName();
        if (deviceName == null) {
            deviceName = bluetoothDevice.getAddress();
        }
        return deviceName;
    }

    public void disconnect() {
        communicator.executeCommand(Command.DISCONNECT);
    }

    public void startAudioRecording() {
        if (communicator != null) {
            communicator.executeCommand(Command.START_AUDIO_RECORD);
        }
    }

    public void stopRecord() {
        communicator.executeCommand(Command.STOP_AUDIO_RECORD);
    }

    public void requestLatestAudioFile() {
        communicator.executeCommand(Command.REQUEST_LATEST_AUDIO_FILE);
    }

    /*public void enableBluetoothActivityResult(int resultCode) {
        bluetoothConnector.requestBluetoothAdapterActivationResult(resultCode);
    }*/

    public long getStartCommandDelay() {
        return startRecordCommandDelay;
    }

    @Override
    public CommunicatorParameters getCommunicatorParameters() {
        return new CommunicatorParameters(bluetoothSocket, context);
    }

    @Override
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
                    Log.e(LOG_TAG, "checkOperationResult (186): Unknown operation \"" + operation.getCommand() + "\".");
                    break;
            }
        }
    }

    private void checkStartAudioRecordCommandResult(Operation operation) {
        int startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
        startRecordCommandDelay = operation.getExecutionDelay();
        long communicationDelay = communicator.getCommunicationDelay() / 1000L;
        Log.d(LOG_TAG, "checkStartAudioRecordCommandResult (196): Execution delay: " + startRecordCommandDelay);
        Log.d(LOG_TAG, "checkStartAudioRecordCommandResult (197): Communication delay: " + communicationDelay);
        startRecordCommandDelay += communicationDelay;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    startAudioRecordResult = (Integer) operation.getReturnObject();
                    switch (startAudioRecordResult) {
                        case GenericReturnCodes.SUCCESS:
                            recording = true;
                            break;
                        case GenericReturnCodes.GENERIC_ERROR:
                            recording = false;
                            break;
                        default:
                            Log.e(LOG_TAG, "checkStartAudioRecordCommandResult (212): Unknown return value received from \"start record\" operation: " + operation.getReturnObject() + ".");
                            recording = false;
                            break;
                    }
                } else {
                    Log.e(LOG_TAG, "checkStartAudioRecordCommandResult (217): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                    startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkStartAudioRecordCommandResult (223): Start audio operation returned an exception.");
                throwable.printStackTrace();
                recording = false;
                startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
                break;
        }
        audioRecorderInterface.startAudioRecordingResult(startAudioRecordResult);
    }

    private void checkStopAudioRecordCommandResult(Operation operation) {
        int stopAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    stopAudioRecordResult = (Integer) operation.getReturnObject();
                    switch (stopAudioRecordResult) {
                        case GenericReturnCodes.SUCCESS:
                            recording = false;
                            break;
                        case GenericReturnCodes.GENERIC_ERROR:
                            Log.e(LOG_TAG, "checkStopAudioRecordCommandResult (245): Error while stopping audio record.");
                            recording = true;
                            break;
                        default:
                            Log.e(LOG_TAG, "checkStopAudioRecordCommandResult (249): Unknown return value received from \"stop record\" operation: " + operation.getReturnObject() + ".");
                            break;
                    }
                } else {
                    Log.e(LOG_TAG, "checkStopAudioRecordCommandResult (253): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkStopAudioRecordCommandResult (258): Stop audio operation returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(context, "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                recording = true;
                break;
        }
        audioRecorderInterface.stopAudioRecordingResult(stopAudioRecordResult);
    }

    private void checkDisconnectCommandResult(Operation operation) {
        int result = GenericReturnCodes.GENERIC_ERROR;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    result = (Integer) operation.getReturnObject();
                } else {
                    Log.e(LOG_TAG, "checkDisconnectCommandResult (275): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkDisconnectCommandResult (280): Disconnect command returned an exception.");
                throwable.printStackTrace();
                result = GenericReturnCodes.GENERIC_ERROR;
                break;
        }

        communicator.finishCommunication();
        audioRecorderInterface.disconnectFromAudioRecorderResult(result);
    }

    private void checkRequestLatestAudioFileResult(Operation operation) {
        Log.d(LOG_TAG, "checkRequestLatestAudioFileResult (292): Received latest audio file.");
        int result = GenericReturnCodes.GENERIC_ERROR;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                Log.d(LOG_TAG, "checkRequestLatestAudioFileResult (297): Class returned: " + returnObjectClass.getName());
                if (returnObjectClass == File.class) {
                    latestAudioFile = (File) operation.getReturnObject();
                    result = GenericReturnCodes.SUCCESS;
                    Log.d(LOG_TAG, "checkRequestLatestAudioFileResult (301): Latest audio file: " + latestAudioFile);
                } else {
                    result = GenericReturnCodes.GENERIC_ERROR;
                    Log.e(LOG_TAG, "checkRequestLatestAudioFileResult (304): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(LOG_TAG, "checkRequestLatestAudioFileResult (309): Request latest audio file returned an exception.");
                throwable.printStackTrace();
                result = GenericReturnCodes.GENERIC_ERROR;
                Toast.makeText(context, "Error while disconnecting from " + getAudioRecorderDeviceName() + ".", Toast.LENGTH_LONG).show();
                break;
        }

        audioRecorderInterface.requestLatestAudioFileResult(result);
    }

    private void checkFinishExecutionCommandResult(Operation operation) {
        Log.e(LOG_TAG, "checkFinishExecutionCommandResult (320): \"" + operation.getCommand() + "\" not implemented yet.");
    }

    public void connectionLost() {
        Log.d(LOG_TAG, "connectionLost (324): Lost connection with audio recorder.");
    }
}
