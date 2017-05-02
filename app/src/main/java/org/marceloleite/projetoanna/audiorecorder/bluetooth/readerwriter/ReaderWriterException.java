package org.marceloleite.projetoanna.audiorecorder.bluetooth.readerwriter;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ReaderWriterException extends Exception {

    public ReaderWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReaderWriterException(String message) {
        super(message);
    }
}
