package org.marceloleite.projetoanna.bluetooth.pairer;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class CommunicationException extends Exception {

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
