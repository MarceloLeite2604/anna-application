package org.marceloleite.projetoanna.audiorecorder.communicator.operator;

import android.os.Looper;
import android.os.Message;
import android.os.Process;

import org.marceloleite.projetoanna.audiorecorder.communicator.commander.CommandResult;
import org.marceloleite.projetoanna.audiorecorder.communicator.commander.Commander;
import org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver.RequestLatestAudioFileResult;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.ResultType;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.executor.OperationExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.executor.OperationExecutorInterface;
import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result.OperationResultHandler;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.file.FileType;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttempts;
import org.marceloleite.projetoanna.utils.retryattempts.RetryAttemptsReturnCodes;

import java.io.File;

/**
 * A thread which controls the operations executed with the audio recorder.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Maximum retry attempts to receive an empty command before send a package to check the connection.
     */
    private static final int MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION = 5;

    /**
     * Sends commands and receives files from audio recorder.
     */
    private Commander commander;

    /**
     * A handler to control the operations requested to be executed on the audio recorder.
     */
    private OperationExecutorHandler operationExecutorHandler;

    /**
     * A controller to check how many times the loop controller did not received an operation.
     */
    private RetryAttempts noOperationRetryAttempts;

    /**
     * The parameters informed to execute this thread.
     */
    private OperatorThreadParameters operatorThreadParameters;

    /**
     * Constructor.
     *
     * @param operatorThreadParameters The parameters informed to run this thread.
     */
    public OperatorThread(OperatorThreadParameters operatorThreadParameters) {
        this.operatorThreadParameters = operatorThreadParameters;
    }

    /**
     * Returns the handler to control the operations requested to be executed on the audio recorder.
     *
     * @return The handler to control the operations requested to be executed on the audio recorder.
     */
    public OperationExecutorHandler getOperationExecutorHandler() {
        return operationExecutorHandler;
    }

    @Override
    public void run() {
        //Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Looper.prepare();

        this.commander = new Commander(operatorThreadParameters.getAppCompatActivity(), operatorThreadParameters.getProgressMonitorAlertDialog(), operatorThreadParameters.getBluetoothSocket());
        this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
        this.operationExecutorHandler = new OperationExecutorHandler(this);

        sendCheckOperationMessage();

        Looper.loop();
    }

    @Override
    public void executeOperation(Operation operation) {
        CommandResult commandResult = null;
        File latestAudioFile = null;
        Throwable throwable = null;

        if (operation != null) {
            Log.d(LOG_TAG, "executeCommand (90): Executing command \"" + operation.getCommand() + "\".");
            switch (operation.getCommand()) {
                case START_AUDIO_RECORD:
                    commandResult = commander.startRecord();
                    break;
                case STOP_AUDIO_RECORD:
                    commandResult = commander.stopRecord();
                    break;
                case REQUEST_LATEST_AUDIO_FILE:
                    RequestLatestAudioFileResult requestLatestAudioFileResult = commander.requestLatestAudioFile();
                    if (requestLatestAudioFileResult.getReturnCode() == GenericReturnCodes.SUCCESS) {
                        latestAudioFile = requestLatestAudioFileResult.getAudioFile();
                    } else {
                        latestAudioFile = null;
                        throwable = new RuntimeException("Error while receiving latest audio file.");
                    }

                    break;
                case DISCONNECT:
                    commandResult = commander.disconnect();
                    break;
                case FINISH_EXECUTION:
                    /* TODO: Finish execution. */
                    break;
                default:
                    Log.e(LOG_TAG, "executeCommand (124): Unknown operation \"" + operation.getCommand() + "\".");
                    throw new RuntimeException("Unknown operation \"" + operation.getCommand() + "\".");
            }

            if (commandResult != null) {
                operation.setResultType(ResultType.OBJECT_RETURNED);
                operation.setReturnObjectClass(Integer.class);
                operation.setReturnObject(commandResult.getReturnedValue());
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

            Message commandResultMessage = operatorThreadParameters.getOperationResultHandler().obtainMessage();
            commandResultMessage.what = OperationResultHandler.RECEIVE_COMMAND_RESULT;
            commandResultMessage.obj = operation;
            Log.d(LOG_TAG, "executeCommand (149): Sending the result of command " + operation.getCommand());
            operatorThreadParameters.getOperationResultHandler().sendMessage(commandResultMessage);

        } else

        {
            switch (noOperationRetryAttempts.waitForNextAttempt()) {
                case RetryAttemptsReturnCodes.SUCCESS:
                    break;
                case RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED:
                    if (commander.checkConnection()) {
                        this.noOperationRetryAttempts = new RetryAttempts(MAXIMUM_ATTEMPTS_BEFORE_CHECK_CONNECTION);
                    } else {
                        Log.d(LOG_TAG, "executeCommand (164): Lost connection with audio recorder.");
                        Message commandResultMessage = operatorThreadParameters.getOperationResultHandler().obtainMessage();
                        commandResultMessage.what = OperationResultHandler.CONNECTION_LOST;
                        commandResultMessage.obj = null;
                        operatorThreadParameters.getOperationResultHandler().sendMessage(commandResultMessage);
                    }
                    break;
            }
        }
        sendCheckOperationMessage();
    }

    /**
     * Returns the communication delay measured.
     *
     * @return The communication delay measured.
     */
    public long getCommunicationDelay() {
        return commander.getCommunicationDelay();
    }

    @Override
    public void finishExecution() {
        Log.d(LOG_TAG, "finishCommunication (184): Communicator thread finished.");
        operationExecutorHandler.removeCallbacksAndMessages(null);
        this.commander = null;
        this.noOperationRetryAttempts = null;
        interrupt();
    }

    /**
     * Sends to the operation execution handler a message to check the operation to be executed.
     */
    private void sendCheckOperationMessage() {
        Message checkOperationMessage = this.operationExecutorHandler.obtainMessage();
        checkOperationMessage.what = OperationExecutorHandler.CHECK_COMMAND_TO_EXECUTE;
        this.operationExecutorHandler.sendMessage(checkOperationMessage);
    }
}
