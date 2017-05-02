package org.marceloleite.projetoanna.audiorecorder.operator.operation.result;

import org.marceloleite.projetoanna.audiorecorder.operator.operation.Operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public interface OperationResultInterface {

    void receiveOperationResult(Operation operation);

    void connectionLost();
}
