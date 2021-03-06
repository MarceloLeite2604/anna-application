package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result;

import android.os.Handler;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Extends the {@link Handler} to controls the result of the operations executed on the audio
 * recorder.
 */
public class OperationResultHandler extends Handler {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = OperationResultHandler.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The code which defines a message requesting to receive a command result.
     */
    public static final int RECEIVE_COMMAND_RESULT = 0xab7f;

    /**
     * The code which defines a message informing that the connection with the audio recorder was lost.
     */
    public static final int CONNECTION_LOST = 0xb82d2;

    /**
     * The object which controls the result of the operations.
     */
    private OperationResultInterface operationResultInterface;

    /**
     * Constructor.
     *
     * @param operationResultInterface The object which controls the result of the operations.
     */
    public OperationResultHandler(OperationResultInterface operationResultInterface) {
        super();
        this.operationResultInterface = operationResultInterface;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case RECEIVE_COMMAND_RESULT:
                Operation operation = (Operation) message.obj;
                operationResultInterface.receiveOperationResult(operation);
                break;
            case CONNECTION_LOST:
                operationResultInterface.connectionLost();
                break;
            default:
                Log.d(LOG_TAG, "handleMessage (49): Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
