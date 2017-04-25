package org.marceloleite.projetoanna.audiorecorder.operator;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class OperatorException extends Exception {

    public OperatorException(String message) {
        super(message);
    }

    public OperatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
