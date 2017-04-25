package org.marceloleite.projetoanna.audiorecorder.operator.filereceiver;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class FileReceiverException extends Exception {

    public FileReceiverException(String message) {
        super(message);
    }

    public FileReceiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
