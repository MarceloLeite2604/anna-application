package org.marceloleite.projetoanna.audiorecorder.communicator.operator.operation;

/**
 * The commands comprehended by the audio recorder.
 */
public enum Command {
    /**
     * Requests the audio recorder to start the recording.
     */
    START_AUDIO_RECORD("START_AUDIO_RECORD"),

    /**
     * Requests the audio recorder to stop the recording.
     */
    STOP_AUDIO_RECORD("STOP_AUDIO_RECORD"),

    /**
     * Requests the audio recorder to disconnect from this application.
     */
    DISCONNECT("DISCONNECT"),

    /**
     * Requests the latest audio file recorded.
     */
    REQUEST_LATEST_AUDIO_FILE("REQUEST_LATEST_AUDIO_FILE"),

    /**
     * Requests the audio recorder to finish its execution.
     */
    FINISH_EXECUTION("FINISH_EXECUTION");

    /**
     * The description of the command.
     */
    private final String title;

    /**
     * Constructor.
     *
     * @param title The description of the command.
     */
    Command(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
