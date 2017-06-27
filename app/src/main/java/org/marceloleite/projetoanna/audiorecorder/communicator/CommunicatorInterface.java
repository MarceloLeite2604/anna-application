package org.marceloleite.projetoanna.audiorecorder.communicator;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;

/**
 * Establishes the methods required to create a {@link Communicator} object and the methods required to communicate with it.
 */
public interface CommunicatorInterface {

    /**
     * Returns the parameters informed to create the {@link Communicator} object.
     *
     * @return The parameters informed to create the {@link Communicator} object.
     */
    CommunicatorParameters getCommunicatorParameters();

    /**
     * Informs the result of an operation executed on the audio recorder.
     *
     * @param operation The operation executed on the audio recorder with its result.
     */
    void checkOperationResult(Operation operation);

    /**
     * Informs that the connection with the audio recorder was lost.
     */
    void connectionLost();
}
