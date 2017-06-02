package org.marceloleite.projetoanna.audiorecorder.operator.operation.executor;

import android.os.Handler;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 24/04/2017.
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
        Log.addClassToLog(OperationExecutorHandler.class);
    }

    public static final int CHECK_COMMAND_TO_EXECUTE = 0x391f;

    public static final int FINISH_EXECUTION = 0x9403;

    private OperationExecutorInterface operationExecutorInterface;

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
                Log.d(OperationExecutorHandler.class, LOG_TAG, "handleMessage (49): Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
