package org.marceloleite.projetoanna.audiorecorder.communicator;

import android.bluetooth.BluetoothSocket;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.OperatorThread;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Command;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.OperatorThreadParameters;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.executor.OperationExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result.OperationResultInterface;
import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;

/**
 * Communicates with the audio recorder.
 */
public class Communicator implements OperationResultInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Communicator.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which receives the communication results.
     */
    private CommunicatorInterface communicatorInterface;

    /**
     * The parameters informed to construct this object.
     */
    private CommunicatorParameters communicatorParameters;

    /**
     * A {@link android.os.Handler} object to control the operation results received from the audio recorder.
     */
    private OperationResultHandler operationResultHandler;

    /**
     * A {@link Thread} object to control the emission of the operations to the audio recorder.
     */
    private OperatorThread operatorThread;

    /**
     * The bluetooth socket which represents the bluetooth connection with the audio recorder.
     */
    private BluetoothSocket audioRecorderBluetoothSocket;

    /**
     * Constructor.
     *
     * @param communicatorInterface The parameters informed to construct this object.
     */
    public Communicator(CommunicatorInterface communicatorInterface) {
        this.communicatorInterface = communicatorInterface;
        this.communicatorParameters = communicatorInterface.getCommunicatorParameters();
        this.audioRecorderBluetoothSocket = communicatorParameters.getBluetoothSocket();
        this.operationResultHandler = new OperationResultHandler(this);
    }

    /**
     * Starts the communication with the audio recorder.
     */
    public void startExecution() {
        if (operatorThread == null) {
            OperatorThreadParameters operatorThreadParameters = new OperatorThreadParameters(communicatorParameters.getBluetoothSocket(), operationResultHandler, communicatorParameters.getAppCompatActivity());
            this.operatorThread = new OperatorThread(operatorThreadParameters);
        }

        if (!operatorThread.isAlive()) {
            operatorThread.start();
        }
    }

    /**
     * Executes a command on the audio recorder.
     *
     * @param command The command to be executed on the audio recorder.
     */
    public void executeCommand(Command command) {
        Log.d(LOG_TAG, "executeCommand (59): Command: " + command);
        Operation operation = new Operation(command);

        OperationExecutorHandler operationExecutorHandler = operatorThread.getOperationExecutorHandler();
        Message commandExecutorMessage = operationExecutorHandler.obtainMessage();
        commandExecutorMessage.what = OperationExecutorHandler.CHECK_COMMAND_TO_EXECUTE;
        commandExecutorMessage.obj = operation;
        operatorThread.getOperationExecutorHandler().sendMessage(commandExecutorMessage);
    }

    /**
     * Finishes the execution of the {@link Communicator#operatorThread} object.
     */
    private void finishOperatorThreadExecution() {
        Log.d(LOG_TAG, "finishOperatorThreadExecution (71): Finishing operator thread execution.");
        OperationExecutorHandler operationExecutorHandler = operatorThread.getOperationExecutorHandler();
        Message commandExecutorMessage = operationExecutorHandler.obtainMessage();
        commandExecutorMessage.what = OperationExecutorHandler.FINISH_EXECUTION;
        operatorThread.getOperationExecutorHandler().sendMessage(commandExecutorMessage);
    }

    /**
     * Returns the delay time calculated for the communication with the audio recorder (in milliseconds).
     *
     * @return The delay time calculated for the communication with the audio recorder (in milliseconds).
     */
    public long getCommunicationDelay() {
        return operatorThread.getCommunicationDelay();
    }

    /**
     * Finishes the communication with the audio recorder.
     */
    public void finishCommunication() {
        finishOperatorThreadExecution();
        disconnectFromAudioRecorder();
    }

    @Override
    public void receiveOperationResult(Operation operation) {
        communicatorInterface.checkOperationResult(operation);
    }

    @Override
    public void connectionLost() {
        operatorThread.finishExecution();
        communicatorInterface.connectionLost();
    }

    /**
     * Checks if bluetooth device is connected.
     *
     * @return True if bluetooth device is connected. False otherwise.
     */
    public boolean isConnected() {
        return (audioRecorderBluetoothSocket != null && audioRecorderBluetoothSocket.isConnected());
    }

    /**
     * Disconnects from audio recorder.
     */
    private void disconnectFromAudioRecorder() {
        if (isConnected()) {
            try {
                this.audioRecorderBluetoothSocket.close();
            } catch (IOException ioException) {
                Log.e(LOG_TAG, "disconnect (207): Error while closing socket with audio recorder.");
                ioException.printStackTrace();
            }
        }
        audioRecorderBluetoothSocket = null;
    }
}
