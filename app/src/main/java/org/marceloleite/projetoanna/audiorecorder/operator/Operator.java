package org.marceloleite.projetoanna.audiorecorder.operator;

import android.bluetooth.BluetoothSocket;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Command;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.OperatorThreadParameters;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.executor.OperationExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.result.OperationResultInterface;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Operator implements OperationResultInterface, OperatorThreadParameters {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Operator.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(Operator.class);
    }

    private OperationResultHandler operationResultHandler;

    private AudioRecorder audioRecorder;

    private OperatorThread operatorThread;

    private Bluetooth bluetooth;

    public Operator(AudioRecorder audioRecorder, Bluetooth bluetooth) {
        this.audioRecorder = audioRecorder;
        this.operationResultHandler = new OperationResultHandler(this);
        this.bluetooth = bluetooth;
    }

    public void startExecution() {
        if (operatorThread == null) {
            this.operatorThread = new OperatorThread(operationResultHandler, this);
        }

        if (!operatorThread.isAlive()) {
            operatorThread.start();
        }
    }

    public void executeCommand(Command command) {
        Log.d(Operator.class, LOG_TAG, "executeCommand (59): Command: " + command);
        Operation operation = new Operation(command);

        OperationExecutorHandler operationExecutorHandler = operatorThread.getOperationExecutorHandler();
        Message commandExecutorMessage = operationExecutorHandler.obtainMessage();
        commandExecutorMessage.what = OperationExecutorHandler.CHECK_COMMAND_TO_EXECUTE;
        commandExecutorMessage.obj = operation;
        Log.d(Operator.class, LOG_TAG, "executeCommand (66): Sending message to execute command \"" + command + "\".");
        operatorThread.getOperationExecutorHandler().sendMessage(commandExecutorMessage);
    }

    public void finishOperatorThreadExecution() {
        Log.d(Operator.class, LOG_TAG, "finishOperatorThreadExecution (71): Finishing operator thread execution.");
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
    }

    @Override
    public void receiveOperationResult(Operation operation) {
        this.audioRecorder.checkOperationResult(operation);
    }

    @Override
    public void connectionLost() {
        operatorThread.finishExecution();
        audioRecorder.connectionLost();
    }

    @Override
    public BluetoothSocket getBluetoothSocket() {
        return bluetooth.getBluetoothSocket();
    }
}
