package org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter;

/**
 * Created by marcelo on 02/06/17.
 */

public class ReadSocketContentResult {

    private int returnCode;
    private byte[] contentRead;

    public ReadSocketContentResult(int returnCode, byte[] contentRead) {
        this.returnCode = returnCode;
        this.contentRead = contentRead;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public byte[] getContentRead() {
        return contentRead;
    }
}
