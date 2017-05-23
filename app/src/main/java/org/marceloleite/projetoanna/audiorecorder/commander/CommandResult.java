package org.marceloleite.projetoanna.audiorecorder.commander;

/**
 * Created by marcelo on 22/05/17.
 */

public class CommandResult {
    private int resultValue;

    private long executionDelay;

    public CommandResult(int resultValue, long executionDelay) {
        this.resultValue = resultValue;
        this.executionDelay = executionDelay;
    }

    public int getResultValue() {
        return resultValue;
    }

    public long getExecutionDelay() {
        return executionDelay;
    }
}
