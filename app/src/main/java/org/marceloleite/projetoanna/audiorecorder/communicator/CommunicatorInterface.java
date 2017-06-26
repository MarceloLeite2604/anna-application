package org.marceloleite.projetoanna.audiorecorder.communicator;

import android.bluetooth.BluetoothSocket;

import org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation.Operation;

/**
 * Created by marcelo on 26/06/17.
 */

public interface CommunicatorInterface {

    CommunicatorParameters getCommunicatorParameters();

    void checkOperationResult(Operation operation);

    void connectionLost();
}
