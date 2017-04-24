package org.marceloleite.projetoanna.audioRecorder.communication;

import org.marceloleite.projetoanna.audioRecorder.datapackage.DataPackage;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ReceivePackageResult {

    private DataPackage dataPackage;
    private int returnValue;

    public ReceivePackageResult(int returnValue, DataPackage dataPackage) {
        this.dataPackage = dataPackage;
        this.returnValue = returnValue;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public int getReturnValue() {
        return returnValue;
    }
}
