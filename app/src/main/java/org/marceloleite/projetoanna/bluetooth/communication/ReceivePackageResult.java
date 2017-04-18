package org.marceloleite.projetoanna.bluetooth.communication;

import org.marceloleite.projetoanna.bluetooth.btpackage.BTPackage;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ReceivePackageResult {

    private BTPackage btPackage;
    private int returnValue;

    public ReceivePackageResult(int returnValue, BTPackage btPackage) {
        this.btPackage = btPackage;
        this.returnValue = returnValue;
    }

    public BTPackage getBtPackage() {
        return btPackage;
    }

    public int getReturnValue() {
        return returnValue;
    }
}
