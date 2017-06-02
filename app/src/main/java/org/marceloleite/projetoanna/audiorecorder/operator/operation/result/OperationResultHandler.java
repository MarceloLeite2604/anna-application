package org.marceloleite.projetoanna.audiorecorder.operator.operation.result;

import android.os.Handler;
import android.os.Message;

import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 24/04/2017.
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
        Log.addClassToLog(OperationResultHandler.class);
    }

    public static final int RECEIVE_COMMAND_RESULT = 0xab7f;

    public static final int CONNECTION_LOST = 0xb82d2;

    private OperationResultInterface operationResultInterface;

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
                Log.d(OperationResultHandler.class, LOG_TAG, "handleMessage (49): Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
