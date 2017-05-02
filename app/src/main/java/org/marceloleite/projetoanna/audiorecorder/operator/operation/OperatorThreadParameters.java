package org.marceloleite.projetoanna.audiorecorder.operator.operation;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Created by Marcelo Leite on 02/05/2017.
 */

public interface OperatorThreadParameters {

    Context getContext();

    BluetoothSocket getBluetoothSocket();
}
