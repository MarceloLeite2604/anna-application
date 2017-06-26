package org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.connector;

/**
 * Implements the process to be executed after the bluetooth connection attempt is concluded.
 */
public interface ConnectWithAudioRecorderInterface {

    /**
     * Returns the parameters required to execute the bluetooth connection task.
     *
     * @return The parameters required to execute the bluetooth connection task.
     */
    ConnectWithAudioRecorderParameters getConnectWithAudioRecorderParameters();

    /**
     * Process to be executed after the bluetooth connection with a remove device is concluded.
     *
     * @param connectWithAudioRecorderResult The result of the audio recorder connection process.
     */
    void connectWithAudioRecorderFinished(ConnectWithAudioRecorderResult connectWithAudioRecorderResult);


}
