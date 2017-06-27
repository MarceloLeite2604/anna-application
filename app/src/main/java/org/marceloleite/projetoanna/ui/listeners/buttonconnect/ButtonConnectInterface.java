package org.marceloleite.projetoanna.ui.listeners.buttonconnect;

/**
 * Specifies the methods executed from {@link ButtonConnectOnClickListener} when its associated button is pressed.
 */
public interface ButtonConnectInterface {

    /**
     * Requests the connection with an audio recorder.
     */
    void connectWithAudioRecorder();

    /**
     * Returns if the application is connected with an audio recorder.
     *
     * @return True if the application is connected. False otherwise.
     */
    boolean isConnected();

    /**
     * Requests the disconnection from the current audio recorder connected.
     */
    void disconnectFromAudioRecorder();
}
