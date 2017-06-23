package org.marceloleite.projetoanna.audiorecorder;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.BluetoothConnectReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.BluetoothConnectionInterface;
import org.marceloleite.projetoanna.audiorecorder.operator.Operator;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Command;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.utils.Log;

import java.io.File;


/**
 * Controls the audio recorder device.
 */
public class AudioRecorder implements BluetoothConnectionInterface {

    /**
     * Tag to identify log messages written by this class.
     */
    private static final String LOG_TAG = AudioRecorder.class.getSimpleName();

    /**
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(AudioRecorder.class);
    }

    /**
     * The audio recorder operator.
     */
    private Operator operator;

    /**
     * The activity which instantiates the audio recorder class.
     */
    private AudioRecorderActivityInterface audioRecorderActivityInterface;

    /**
     * Controls the bluetooth activation, pairing and connection.
     */
    private Bluetooth bluetooth;

    /**
     * Indicates if audio record device is currently recording.
     */
    private boolean recording;

    private File latestAudioFile;

    private long startRecordCommandDelay;

    private long stopRecordCommandDelay;

    /**
     * Instantiates the Audio Recorder class.
     *
     * @param audioRecorderActivityInterface The activity which request the Audio Record Instantiation.
     */
    public AudioRecorder(AudioRecorderActivityInterface audioRecorderActivityInterface) {
        this.audioRecorderActivityInterface = audioRecorderActivityInterface;
        this.recording = false;
        this.operator = null;
        this.bluetooth = new Bluetooth(this);
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

    public String getAudioRecorderDeviceName() {
        BluetoothDevice bluetoothDevice = bluetooth.getBluetoothDevice();
        String result = null;

        if (bluetoothDevice != null) {
            result = bluetoothDevice.getName();
            if (result == null) {
                result = bluetoothDevice.getAddress();
            }
        }
        return result;
    }

    public void connectWithAudioRecorder() {
        bluetooth.connectWithAudioRecorder();
    }

    public void bluetoothConnectionResult(int result) {
        switch (result) {
            case BluetoothConnectReturnCodes.SUCCESS:
                operator = new Operator(this, bluetooth);
                operator.startExecution();
                break;
            case BluetoothConnectReturnCodes.GENERIC_ERROR:
                Log.e(AudioRecorder.class, LOG_TAG, "bluetoothConnectionResult (110): Error while connecting with audio recorder device.");
                break;
            default:
                Log.e(AudioRecorder.class, LOG_TAG, "bluetoothConnectionResult (113): Unknown result received from \"connectWithAudioRecorder\" method: " + result);
                break;
        }

        audioRecorderActivityInterface.connectWithAudioRecorderResult(result);
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return audioRecorderActivityInterface.getActivity();
    }

    public void disconnectFromAudioRecorder() {
        operator.executeCommand(Command.DISCONNECT);
    }

    public void startAudioRecording() {
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

    public void enableBluetoothActivityResult(int resultCode) {
        bluetooth.requestBluetoothAdapterActivationResult(resultCode);
    }

    public long getStartCommandDelay() {
        /*Log.d(LOG_TAG, "getStartCommandDelay: Start record command delay: " + startRecordCommandDelay);
        long communicationDelay = (operator.getCommunicationDelay()/1000L);
        Log.d(LOG_TAG, "getStartCommandDelay: Original communication delay: " + communicationDelay);
        communicationDelay *= 2;
        Log.d(LOG_TAG, "getStartCommandDelay: Communication delay: " + communicationDelay);*/
        return startRecordCommandDelay;
    }

    public long getStopCommandDelay() {
        Log.d(AudioRecorder.class, LOG_TAG, "getStopCommandDelay (161): Stop record command delay: " + stopRecordCommandDelay);
        Log.d(AudioRecorder.class, LOG_TAG, "getStopCommandDelay (162): Communication delay: " + (operator.getCommunicationDelay()/1000L));
        return stopRecordCommandDelay + (operator.getCommunicationDelay()/1000L);
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
                    Log.e(AudioRecorder.class, LOG_TAG, "checkOperationResult (186): Unknown operation \"" + operation.getCommand() + "\".");
                    break;
            }
        }
    }

    private void checkStartAudioRecordCommandResult(Operation operation) {
        int startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
        startRecordCommandDelay = operation.getExecutionDelay();
        long communicationDelay = operator.getCommunicationDelay()/1000L;
        Log.d(AudioRecorder.class, LOG_TAG, "checkStartAudioRecordCommandResult (196): Execution delay: " + startRecordCommandDelay);
        Log.d(AudioRecorder.class, LOG_TAG, "checkStartAudioRecordCommandResult (197): Communication delay: " + communicationDelay);
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
                            Log.e(AudioRecorder.class, LOG_TAG, "checkStartAudioRecordCommandResult (212): Unknown return value received from \"start record\" operation: " + operation.getReturnObject() + ".");
                            recording = false;
                            break;
                    }
                } else {
                    Log.e(AudioRecorder.class, LOG_TAG, "checkStartAudioRecordCommandResult (217): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                    startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(AudioRecorder.class, LOG_TAG, "checkStartAudioRecordCommandResult (223): Start audio operation returned an exception.");
                throwable.printStackTrace();
                recording = false;
                startAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
                break;
        }
        audioRecorderActivityInterface.startAudioRecordingResult(startAudioRecordResult);
    }

    private void checkStopAudioRecordCommandResult(Operation operation) {
        int stopAudioRecordResult = GenericReturnCodes.GENERIC_ERROR;
        stopRecordCommandDelay = operation.getExecutionDelay();
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
                            Log.e(AudioRecorder.class, LOG_TAG, "checkStopAudioRecordCommandResult (245): Error while stopping audio record.");
                            recording = true;
                            break;
                        default:
                            Log.e(AudioRecorder.class, LOG_TAG, "checkStopAudioRecordCommandResult (249): Unknown return value received from \"stop record\" operation: " + operation.getReturnObject() + ".");
                            break;
                    }
                } else {
                    Log.e(AudioRecorder.class, LOG_TAG, "checkStopAudioRecordCommandResult (253): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(AudioRecorder.class, LOG_TAG, "checkStopAudioRecordCommandResult (258): Stop audio operation returned an exception.");
                throwable.printStackTrace();
                Toast.makeText(audioRecorderActivityInterface.getActivity(), "Could not stop audio record.", Toast.LENGTH_SHORT).show();
                recording = true;
                break;
        }
        audioRecorderActivityInterface.stopAudioRecordingResult(stopAudioRecordResult);
    }

    private void checkDisconnectCommandResult(Operation operation) {
        int result = GenericReturnCodes.GENERIC_ERROR;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                if (returnObjectClass == Integer.class) {
                    result = (Integer) operation.getReturnObject();
                } else {
                    Log.e(AudioRecorder.class, LOG_TAG, "checkDisconnectCommandResult (275): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(AudioRecorder.class, LOG_TAG, "checkDisconnectCommandResult (280): Disconnect command returned an exception.");
                throwable.printStackTrace();
                result = GenericReturnCodes.GENERIC_ERROR;
                break;
        }

        operator.finishExecution();
        bluetooth.disconnectFromDevice();
        audioRecorderActivityInterface.disconnectFromAudioRecorderResult(result);
    }

    private void checkRequestLatestAudioFileResult(Operation operation) {
        Log.d(AudioRecorder.class, LOG_TAG, "checkRequestLatestAudioFileResult (292): Received latest audio file.");
        int result = GenericReturnCodes.GENERIC_ERROR;
        switch (operation.getResultType()) {
            case OBJECT_RETURNED:
                Class returnObjectClass = operation.getReturnObjectClass();
                Log.d(AudioRecorder.class, LOG_TAG, "checkRequestLatestAudioFileResult (297): Class returned: " + returnObjectClass.getName());
                if (returnObjectClass == File.class) {
                    latestAudioFile = (File) operation.getReturnObject();
                    result = GenericReturnCodes.SUCCESS;
                    Log.d(AudioRecorder.class, LOG_TAG, "checkRequestLatestAudioFileResult (301): Latest audio file: " + latestAudioFile);
                } else {
                    result = GenericReturnCodes.GENERIC_ERROR;
                    Log.e(AudioRecorder.class, LOG_TAG, "checkRequestLatestAudioFileResult (304): Unknown object \"" + returnObjectClass.getName() + "\" return from operation \"" + operation.getCommand() + "\".");
                }
                break;
            case EXCEPTION_THROWN:
                Throwable throwable = operation.getThrowable();
                Log.e(AudioRecorder.class, LOG_TAG, "checkRequestLatestAudioFileResult (309): Request latest audio file returned an exception.");
                throwable.printStackTrace();
                result = GenericReturnCodes.GENERIC_ERROR;
                Toast.makeText(audioRecorderActivityInterface.getActivity(), "Error while disconnecting from " + bluetooth.getBluetoothDevice().getName() + ".", Toast.LENGTH_SHORT).show();
                break;
        }

        audioRecorderActivityInterface.requestLatestAudioFileResult(result);
    }

    private void checkFinishExecutionCommandResult(Operation operation) {
        Log.e(AudioRecorder.class, LOG_TAG, "checkFinishExecutionCommandResult (320): \"" + operation.getCommand() + "\" not implemented yet.");
    }

    public void connectionLost() {
        Log.d(AudioRecorder.class, LOG_TAG, "connectionLost (324): Lost connection with audio recorder.");
    }
}
