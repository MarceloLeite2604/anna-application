package org.marceloleite.projetoanna.audiorecorder.bluetooth.senderreceiver;

import org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.DataPackage;

/**
 * The content returned from {@link SenderReceiver#receivePackage()} method.
 */

public class ReceivePackageResult {

    private int returnCode;
    private DataPackage dataPackage;

    public ReceivePackageResult(int returnCode, DataPackage dataPackage) {
        this.returnCode = returnCode;
        this.dataPackage = dataPackage;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }
}
