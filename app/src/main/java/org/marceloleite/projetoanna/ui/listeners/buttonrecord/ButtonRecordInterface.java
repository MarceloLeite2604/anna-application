package org.marceloleite.projetoanna.ui.listeners.buttonrecord;

/**
 * Specifies the methods executed from {@link ButtonRecordOnClickListener} when its associated button is pressed.
 */
public interface ButtonRecordInterface {

    /**
     * Returns in the application is recording an audio and video.
     *
     * @return True if an audio an video is being recorded. False otherwise.
     */
    boolean isRecording();

    /**
     * Finishes the audio and video recording process.
     */
    void finishRecording();

    /**
     * Starts the audio and video recording process.
     */
    void startRecording();
}
