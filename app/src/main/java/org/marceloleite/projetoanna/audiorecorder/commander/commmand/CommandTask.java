package org.marceloleite.projetoanna.audiorecorder.commander.commmand;

import org.marceloleite.projetoanna.audiorecorder.commander.commmand.result.CommandResultType;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommandTask {

    private CommandType commandType;
    private CommandResultType commandResultType;
    private Throwable throwable;
    private int returnValue;

    public CommandTask(CommandType commandType) {
        this.commandType = commandType;
    }

    public void setCommandResultType(CommandResultType commandResultType) {
        this.commandResultType = commandResultType;
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

    public CommandResultType getCommandResultType() {
        return commandResultType;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getReturnValue() {
        return returnValue;
    }
}
