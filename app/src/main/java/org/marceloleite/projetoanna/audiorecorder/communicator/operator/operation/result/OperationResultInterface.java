package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.result;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;

/**
 * Specifies the methods required to receive the result of the operations executed on the audio
 * recorder.
 */
public interface OperationResultInterface {

    /**
     * Receives the result of an operation executed on the audio recorder.
     *
     * @param operation The result of the operation.
     */
    void receiveOperationResult(Operation operation);

    /**
     * Method executed when the connection with the audio recorder was lost while an operation was
     * being executed.
     */
    void connectionLost();
}
