package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.executor;

import android.os.Handler;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link Handler} to execute operations with the audio recorder.
 */
public class OperationExecutorHandler extends Handler {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = OperationExecutorHandler.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Message code to check a command to execute.
     */
    public static final int CHECK_COMMAND_TO_EXECUTE = 0x391f;

    /**
     * Message code to finish its execution.
     */
    public static final int FINISH_EXECUTION = 0x9403;

    /**
     * An object which executes the operations requested.
     */
    private OperationExecutorInterface operationExecutorInterface;

    /**
     * Constructor.
     *
     * @param operationExecutorInterface An object which executes the operations requested.
     */
    public OperationExecutorHandler(OperationExecutorInterface operationExecutorInterface) {
        super();
        this.operationExecutorInterface = operationExecutorInterface;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CHECK_COMMAND_TO_EXECUTE:
                Operation operation = (Operation) message.obj;
                operationExecutorInterface.executeOperation(operation);
                break;
            case FINISH_EXECUTION:
                operationExecutorInterface.finishExecution();
                break;
            default:
                Log.d(LOG_TAG, "handleMessage (49): Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
