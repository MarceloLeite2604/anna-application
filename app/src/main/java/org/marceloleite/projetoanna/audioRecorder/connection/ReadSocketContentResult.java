package org.marceloleite.projetoanna.audioRecorder.connection;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ReadSocketContentResult {

    private int returnValue;
    private byte[] bytes;

    public ReadSocketContentResult(int returnValue, byte[] bytes) {
        this.returnValue = returnValue;
        this.bytes = bytes;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
