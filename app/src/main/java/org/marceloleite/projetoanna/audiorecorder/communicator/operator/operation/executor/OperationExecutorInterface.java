package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.executor;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;

/**
 * Describes the methods required to execute the operations with the audio recorder.
 */
public interface OperationExecutorInterface {

    /**
     * Executes an operation with the audio recorder.
     *
     * @param operation The operation to be executed.
     */
    void executeOperation(Operation operation);

    /**
     * Finishes the operation executor.
     */
    void finishExecution();
}
