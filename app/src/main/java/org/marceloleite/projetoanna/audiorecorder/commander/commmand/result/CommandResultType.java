package org.marceloleite.projetoanna.audiorecorder.commander.commmand.result;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public enum CommandResultType {
    VALUE_RETURNED("VALUE_RETURNED"),
    EXCEPTION_THROWN("EXCEPTION_THROWN");

    String title;

    CommandResultType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
