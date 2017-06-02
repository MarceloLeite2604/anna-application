package org.marceloleite.projetoanna.audiorecorder.operator;

import android.os.Looper;
import android.os.Message;
import android.os.Process;

import org.marceloleite.projetoanna.audiorecorder.commander.CommandResult;
import org.marceloleite.projetoanna.audiorecorder.commander.Commander;
import org.marceloleite.projetoanna.audiorecorder.commander.CommanderException;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.OperatorThreadParameters;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.executor.OperationExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.executor.OperationExecutorInterface;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.ResultType;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.io.File;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class OperatorThread extends Thread implements OperationExecutorInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = OperatorThread.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(OperatorThread.class);
    }

    /**
     * Maximum retry attempts to receive an empty command before send a package to check the connection.
     */
    private static final int MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION = 5;

    private Commander commander;

    private OperationExecutorHandler operationExecutorHandler;

    private OperationResultHandler operationResultHandler;

    private RetryAttempts noOperationRetryAttempts;

    private OperatorThreadParameters operatorThreadParameters;

    public OperatorThread(OperationResultHandler operationResultHandler, OperatorThreadParameters operatorThreadParameters) {
        this.operationResultHandler = operationResultHandler;
        this.operatorThreadParameters = operatorThreadParameters;
    }

    public OperationExecutorHandler getOperationExecutorHandler() {
        return operationExecutorHandler;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Looper.prepare();

        try {
            this.commander = new Commander(operatorThreadParameters.getBluetoothSocket());
            this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
            this.operationExecutorHandler = new OperationExecutorHandler(this);

            sendCheckOperationMessage();

            Looper.loop();
        } catch (CommanderException commanderException) {
            Log.e(OperatorThread.class, LOG_TAG, "run (78): Error while executing operator thread.");
            commanderException.printStackTrace();
        }
    }

    @Override
    public void executeOperation(Operation operation) {
        CommandResult commandResult = null;
        File latestAudioFile = null;
        Throwable throwable = null;

        if (operation != null) {
            Log.d(OperatorThread.class, LOG_TAG, "executeOperation (90): Executing command \"" + operation.getCommand() + "\".");
            switch (operation.getCommand()) {
                case START_AUDIO_RECORD:
                    try {
                        commandResult = commander.startRecord();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case STOP_AUDIO_RECORD:
                    try {
                        commandResult = commander.stopRecord();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case REQUEST_LATEST_AUDIO_FILE:
                    try {
                        latestAudioFile = commander.requestLatestAudioFile();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case DISCONNECT:
                    try {
                        commandResult = commander.disconnect();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case FINISH_EXECUTION:
                    /* TODO: Finish execution. */
                    break;
                default:
                    Log.e(OperatorThread.class, LOG_TAG, "executeOperation (124): Unknown operation \"" + operation.getCommand() + "\".");
                    break;
            }

            if (commandResult != null) {
                operation.setResultType(ResultType.OBJECT_RETURNED);
                operation.setReturnObjectClass(Integer.class);
                operation.setReturnObject(commandResult.getResultValue());
                operation.setExecutionDelay(commandResult.getExecutionDelay());
                this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
            } else {
                if (latestAudioFile != null) {
                    operation.setResultType(ResultType.OBJECT_RETURNED);
                    operation.setReturnObjectClass(File.class);
                    operation.setReturnObject(latestAudioFile);
                    this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
                } else {
                    operation.setResultType(ResultType.EXCEPTION_THROWN);
                    operation.setThrowable(throwable);
                }
            }

            Message commandResultMessage = operationResultHandler.obtainMessage();
            commandResultMessage.what = OperationResultHandler.RECEIVE_COMMAND_RESULT;
            commandResultMessage.obj = operation;
            Log.d(OperatorThread.class, LOG_TAG, "executeOperation (149): Sending the result of command " + operation.getCommand());
            operationResultHandler.sendMessage(commandResultMessage);

        } else

        {
            switch (RetryAttempts.wait(this.noOperationRetryAttempts)) {
                case RetryAttemptsReturnCodes.SUCCESS:
                    //Log.d(LOG_TAG, "executeOperation, 115: Check command, attempt " + this.noOperationRetryAttempts.getTotalAttempts() + " of " + this.noOperationRetryAttempts.getMaximumAttempts() + ".");
                    break;
                case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                    if (commander.checkConnection()) {
                        //Log.d(LOG_TAG, "executeOperation, 115: ReaderWriter checked: Audio recorder is connected.");
                        this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
                    } else {
                        Log.d(OperatorThread.class, LOG_TAG, "executeOperation (164): Lost connection with audio recorder.");
                        Message commandResultMessage = operationResultHandler.obtainMessage();
                        commandResultMessage.what = OperationResultHandler.CONNECTION_LOST;
                        commandResultMessage.obj = operation;
                        operationResultHandler.sendMessage(commandResultMessage);
                    }
                    break;
            }
        }

        sendCheckOperationMessage();

    }

    public long getCommunicationDelay() {
        return commander.getCommunicationDelay();
    }

    @Override
    public void finishExecution() {
        Log.d(OperatorThread.class, LOG_TAG, "finishExecution (184): Operator thread finished.");
        operationExecutorHandler.removeCallbacksAndMessages(null);
        this.commander = null;
        this.noOperationRetryAttempts = null;
        interrupt();
    }

    private void sendCheckOperationMessage() {
        Message checkOperationMessage = this.operationExecutorHandler.obtainMessage();
        checkOperationMessage.what = OperationExecutorHandler.CHECK_COMMAND_TO_EXECUTE;
        this.operationExecutorHandler.sendMessage(checkOperationMessage);
    }
}
