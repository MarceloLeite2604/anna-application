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
 * Created by Marcelo Leite on 24/04/2017.
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

    private CommunicatorInterface communicatorInterface;

    private CommunicatorParameters communicatorParameters;

    private OperationResultHandler operationResultHandler;

    private OperatorThread operatorThread;

    // private BluetoothConnector bluetoothConnector;

    private BluetoothSocket audioRecorderBluetoothSocket;

    public Communicator(CommunicatorInterface communicatorInterface) {
        this.communicatorInterface = communicatorInterface;
        this.communicatorParameters = communicatorInterface.getCommunicatorParameters();
        this.operationResultHandler = new OperationResultHandler(this);
    }

    public void startExecution() {
        if (operatorThread == null) {
            OperatorThreadParameters operatorThreadParameters = new OperatorThreadParameters(communicatorParameters.getBluetoothSocket(), operationResultHandler, communicatorParameters.getContext());
            this.operatorThread = new OperatorThread(operatorThreadParameters);
        }

        if (!operatorThread.isAlive()) {
            operatorThread.start();
        }
    }

    public void executeCommand(Command command) {
        Log.d(LOG_TAG, "executeCommand (59): Command: " + command);
        Operation operation = new Operation(command);

        OperationExecutorHandler operationExecutorHandler = operatorThread.getOperationExecutorHandler();
        Message commandExecutorMessage = operationExecutorHandler.obtainMessage();
        commandExecutorMessage.what = OperationExecutorHandler.CHECK_COMMAND_TO_EXECUTE;
        commandExecutorMessage.obj = operation;
        operatorThread.getOperationExecutorHandler().sendMessage(commandExecutorMessage);
    }

    private void finishOperatorThreadExecution() {
        Log.d(LOG_TAG, "finishOperatorThreadExecution (71): Finishing operator thread execution.");
        OperationExecutorHandler operationExecutorHandler = operatorThread.getOperationExecutorHandler();
        Message commandExecutorMessage = operationExecutorHandler.obtainMessage();
        commandExecutorMessage.what = OperationExecutorHandler.FINISH_EXECUTION;
        operatorThread.getOperationExecutorHandler().sendMessage(commandExecutorMessage);
    }

    public long getCommunicationDelay() {
        return operatorThread.getCommunicationDelay();
    }


    public void finishExecution() {
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
        return (audioRecorderBluetoothSocket.isConnected());
    }

    /**
     * Disconnects from audio recorder.
     */
    public void disconnectFromAudioRecorder() {
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
