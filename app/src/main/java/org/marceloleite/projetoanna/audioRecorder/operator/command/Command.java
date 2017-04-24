package org.marceloleite.projetoanna.audioRecorder.operator.command;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Command {

    private CommandType commandType;
    private CommandResult commandResult;
    private Throwable throwable;
    private int returnValue;

    public Command(CommandType commandType) {
        this.commandType = commandType;
    }

    public void setCommandResult(CommandResult commandResult) {
        this.commandResult = commandResult;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandResult getCommandResult() {
        return commandResult;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getReturnValue() {
        return returnValue;
    }
}
