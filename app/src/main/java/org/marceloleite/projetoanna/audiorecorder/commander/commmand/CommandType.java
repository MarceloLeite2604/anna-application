package org.marceloleite.projetoanna.audiorecorder.commander.commmand;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public enum CommandType {
    START_AUDIO_RECORD("START_AUDIO_RECORD"),
    STOP_AUDIO_RECORD("STOP_AUDIO_RECORD"),
    DISCONNECT("DISCONNECT"),
    FINISH_EXECUTION("FINISH_EXECUTION");

    private String title;

    CommandType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
