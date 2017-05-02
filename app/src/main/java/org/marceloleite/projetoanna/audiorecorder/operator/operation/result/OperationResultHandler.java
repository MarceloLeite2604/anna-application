package org.marceloleite.projetoanna.audiorecorder.operator.operation.result;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class OperationResultHandler extends Handler {

    private static final String LOG_TAG = OperationResultHandler.class.getSimpleName();

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
                Log.d(LOG_TAG, "handleMessage, 33: Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
