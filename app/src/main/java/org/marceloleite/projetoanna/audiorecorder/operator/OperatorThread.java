package org.marceloleite.projetoanna.audiorecorder.operator;

import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.commander.Commander;
import org.marceloleite.projetoanna.audiorecorder.commander.CommanderException;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.OperatorThreadParameters;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.executor.OperationExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.executor.OperationExecutorInterface;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.audiorecorder.operator.operation.ResultType;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.io.File;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class OperatorThread extends Thread implements OperationExecutorInterface {

    /**
     * Maximum retry attempts to receive an empty command before send a package to check the connection.
     */
    private static final int MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION = 5;

    private static final String LOG_TAG = OperatorThread.class.getSimpleName();

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

        this.commander = new Commander(operatorThreadParameters.getBluetoothSocket());
        this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
        this.operationExecutorHandler = new OperationExecutorHandler(this);

        sendCheckOperationMessage();

        Looper.loop();
    }

    @Override
    public void executeOperation(Operation operation) {
        Integer returnValue = null;
        File latestAudioFile = null;
        Throwable throwable = null;

        if (operation != null) {
            Log.d(LOG_TAG, "checkCommandToExecute, 70: Executing command \"" + operation.getCommand() + "\".");
            switch (operation.getCommand()) {
                case START_AUDIO_RECORD:
                    try {
                        returnValue = commander.startRecord();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case STOP_AUDIO_RECORD:
                    try {
                        returnValue = commander.stopRecord();
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
                        returnValue = commander.disconnect();
                    } catch (CommanderException commanderException) {
                        throwable = commanderException;
                    }
                    break;
                case FINISH_EXECUTION:
                    /* TODO: Finish execution. */
                    break;
                default:
                    Log.e(LOG_TAG, "executeInterfaceCommand, 37: Unknown operation \"" + operation.getCommand() + "\".");
                    break;
            }

            if (returnValue != null) {
                operation.setResultType(ResultType.OBJECT_RETURNED);
                operation.setReturnObjectClass(Integer.class);
                operation.setReturnObject(returnValue);
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
            Log.d(LOG_TAG, "executeOperation, 105: Sending the result of command " + operation.getCommand());
            operationResultHandler.sendMessage(commandResultMessage);

        } else

        {
            switch (RetryAttempts.wait(this.noOperationRetryAttempts)) {
                case RetryAttemptsReturnCodes.SUCCESS:
                    Log.d(LOG_TAG, "executeOperation, 115: Check command, attempt " + this.noOperationRetryAttempts.getTotalAttempts() + " of " + this.noOperationRetryAttempts.getMaximumAttempts() + ".");
                    break;
                case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                    if (commander.checkConnection()) {
                        Log.d(LOG_TAG, "executeOperation, 115: ReaderWriter checked: Audio recorder is connected.");
                        this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
                    } else {
                        Log.d(LOG_TAG, "executeOperation, 118: Lost connection with audio recorder.");
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

    @Override
    public void finishExecution() {
        Log.d(LOG_TAG, "finishExecution, 134: Operator thread finished.");
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
