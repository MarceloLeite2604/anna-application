package org.marceloleite.projetoanna.audiorecorder.communicator.commander;

/**
 * A result of a command executed on the audio recorder.
 */
public class CommandResult {

    /**
     * The command result value returned from audio recorder.
     */
    private final int returnedValue;

    /**
     * The delay of the command execution on audio recorder (in microseconds).
     */
    private final long executionDelay;

    /**
     * Constructor.
     *
     * @param returnedValue  The command result value returned from audio recorder.
     * @param executionDelay The delay of the command execution on audio recorder (in microseconds).
     */
    CommandResult(int returnedValue, long executionDelay) {
        this.returnedValue = returnedValue;
        this.executionDelay = executionDelay;
    }

    /**
     * Returns the command result value returned from audio recorder.
     *
     * @return The command result value returned from audio recorder.
     */
    public int getReturnedValue() {
        return returnedValue;
    }

    /**
     * Returns the delay of the command execution on audio recorder (in microseconds).
     *
     * @return The delay of the command execution on audio recorder (in microseconds).
     */
    public long getExecutionDelay() {
        return executionDelay;
    }
}
