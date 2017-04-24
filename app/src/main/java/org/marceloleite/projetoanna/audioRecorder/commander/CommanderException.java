package org.marceloleite.projetoanna.audioRecorder.commander;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommanderException extends Exception {

    public CommanderException(String message) {
        super(message);
    }

    public CommanderException(String message, Throwable cause) {
        super(message, cause);
    }
}
