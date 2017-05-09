package org.marceloleite.projetoanna.audiorecorder.operator.operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public enum ResultType {
    OBJECT_RETURNED("OBJECT_RETURNED"),
    EXCEPTION_THROWN("EXCEPTION_THROWN");

    String title;

    ResultType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
