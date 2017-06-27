package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation;

/**
 * The types of result that an audio recorder operation can return.
 */
public enum ResultType {
    /**
     * Indicates that the operation returned an object.
     */
    OBJECT_RETURNED("Object returned"),

    /**
     * Indicates that the operation threw an exception.
     */
    EXCEPTION_THROWN("Exception thrown");

    /**
     * The description of the result type.
     */
    final String description;

    /**
     * Constructor.
     *
     * @param description The description of the reuslt type.
     */
    ResultType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
