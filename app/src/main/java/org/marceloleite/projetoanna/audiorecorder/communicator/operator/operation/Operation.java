package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation;

/**
 * Information about an operation executed on the audio recorder.
 */
public class Operation {


    /**
     * The command to be executed.
     */
    private final Command command;

    /**
     * The type of the operation result.
     */
    private ResultType resultType;

    /**
     * The exception thrown by the operation.
     */
    private Throwable throwable;

    /**
     * The object returned by the operation.
     */
    private Object returnObject;

    /**
     * The class of the object returned by the operation.
     */
    private Class returnObjectClass;

    /**
     * The delay time of the command execution on the audio recorder.
     */
    private Long executionDelay;

    /**
     * Constructor.
     *
     * @param command The command to be executed by the operation.
     */
    public Operation(Command command) {
        this.command = command;
    }

    /**
     * Defines the type of the operation result.
     *
     * @param resultType The type of the operation result.
     */
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * Defines the exception thrown by the operation.
     *
     * @param throwable The exception thrown by the operation.
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Defines the object returned by the operation.
     *
     * @param returnObject The object returned by the operation.
     */
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    /**
     * Defines the class of the object returned by the operation.
     *
     * @param returnObjectClass The class of the object returned by the operation.
     */
    public void setReturnObjectClass(Class returnObjectClass) {
        this.returnObjectClass = returnObjectClass;
    }

    /**
     * Defines the delay time of the command execution on the audio recorder.
     *
     * @param executionDelay The delay time of the command execution on the audio recorder.
     */
    public void setExecutionDelay(Long executionDelay) {
        this.executionDelay = executionDelay;
    }

    /**
     * Returns the command to be executed.
     *
     * @return The command to be executed.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Returns the type of the operation result.
     *
     * @return The type of the operation result.
     */
    public ResultType getResultType() {
        return resultType;
    }

    /**
     * Returns the exception thrown by the operation.
     *
     * @return The exception thrown by the operation.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Returns the object returned by the operation.
     *
     * @return The object returned by the operation.
     */
    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Returns the class of the object returned by the operation.
     *
     * @return The class of the object returned by the operation.
     */
    public Class getReturnObjectClass() {
        return returnObjectClass;
    }

    /**
     * Returns the delay time of the command execution on the audio recorder.
     *
     * @return The delay time of the command execution on the audio recorder.
     */
    public Long getExecutionDelay() {
        return executionDelay;
    }
}
