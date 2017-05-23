package org.marceloleite.projetoanna.audiorecorder.operator.operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Operation {

    private Command command;
    private ResultType resultType;
    private Throwable throwable;
    private Object returnObject;
    private Class returnObjectClass;
    private Long executionDelay;

    public Operation(Command command) {
        this.command = command;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public void setReturnObjectClass(Class returnObjectClass) {
        this.returnObjectClass = returnObjectClass;
    }

    public void setExecutionDelay(Long executionDelay) {
        this.executionDelay = executionDelay;
    }

    public Command getCommand() {
        return command;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public Class getReturnObjectClass() {
        return returnObjectClass;
    }

    public Long getExecutionDelay() {
        return executionDelay;
    }
}
