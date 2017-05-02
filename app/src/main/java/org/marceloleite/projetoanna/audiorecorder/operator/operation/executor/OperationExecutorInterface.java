package org.marceloleite.projetoanna.audiorecorder.operator.operation.executor;

import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public interface OperationExecutorInterface {

    void executeOperation(Operation operation);

    void finishExecution();
}
