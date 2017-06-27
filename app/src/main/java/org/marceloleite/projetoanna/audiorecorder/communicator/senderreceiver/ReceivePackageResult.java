package org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver;

import org.marceloleite.projetoanna.audiorecorder.communicator.datapackage.DataPackage;

/**
 * The content returned from {@link SenderReceiver#receivePackage()} method.
 */

public class ReceivePackageResult {

    /**
     * The code returned from {@link SenderReceiver#receivePackage()} method.
     */
    private final int returnCode;

    /**
     * The data package received.
     */
    private final DataPackage dataPackage;

    /**
     * Object constructor.
     *
     * @param returnCode  The code returned from {@link SenderReceiver#receivePackage()} method.
     * @param dataPackage The data package received.
     */
    ReceivePackageResult(int returnCode, DataPackage dataPackage) {
        this.returnCode = returnCode;
        this.dataPackage = dataPackage;
    }

    /**
     * Returns the code returned from {@link SenderReceiver#receivePackage()} method.
     *
     * @return The code returned from {@link SenderReceiver#receivePackage()} method.
     */
    @SuppressWarnings("unused")
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Returns the data package received.
     *
     * @return The data package received.
     */
    @SuppressWarnings("unused")
    public DataPackage getDataPackage() {
        return dataPackage;
    }
}
