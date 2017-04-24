package org.marceloleite.projetoanna.audioRecorder.connection;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class ConnectionException extends Exception {

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(String message) {
        super(message);
    }
}
