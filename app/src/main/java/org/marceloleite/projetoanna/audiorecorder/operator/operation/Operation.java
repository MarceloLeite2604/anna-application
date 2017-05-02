package org.marceloleite.projetoanna.audiorecorder.operator.operation;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Operation {

    private Command command;
    private ResultType resultType;
    private Throwable throwable;
    private int returnValue;

    public Operation(Command command) {
        this.command = command;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
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

    public int getReturnValue() {
        return returnValue;
    }
}
