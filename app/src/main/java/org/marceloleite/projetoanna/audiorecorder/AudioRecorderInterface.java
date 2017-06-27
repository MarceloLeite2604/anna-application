package org.marceloleite.projetoanna.audiorecorder;

/**
 * Establishes the method used by the audio recorder to inform the result of its commands.
 */
public interface AudioRecorderInterface {

    /**
     * Result received from the audio recorder after request its disconnection.
     *
     * @param result The result received from the audio recorder.
     *               {@link AudioRecorderReturnCodes#SUCCESS} if audio recorder disconnected from
     *               audio recorder successfully or {@link AudioRecorderReturnCodes#GENERIC_ERROR}
     *               if an error occurred.
     */
    void disconnectFromAudioRecorderResult(int result);

    /**
     * Informs the result received from the audio recorder after request the latest audio file
     * recorded.
     *
     * @param result The result received from the audio recorder.
     *               {@link AudioRecorderReturnCodes#SUCCESS} if file was received successfully or
     *               {@link AudioRecorderReturnCodes#GENERIC_ERROR} if an error occurred.
     */
    void requestLatestAudioFileResult(int result);

    /**
     * Informs the result received from the audio recorder after request to start audio recording.
     *
     * @param result The result received from the audio recorder.
     *               {@link AudioRecorderReturnCodes#SUCCESS} if the recording was started
     *               successfully or {@link AudioRecorderReturnCodes#GENERIC_ERROR} if an error
     *               occurred.
     */
    void startAudioRecordingResult(int result);

    /**
     * Informs the result received from the audio recorder after request to stop audio recording.
     *
     * @param result The result received from the audio recorder.
     *               {@link AudioRecorderReturnCodes#SUCCESS} if the recording was stopped
     *               successfully or {@link AudioRecorderReturnCodes#GENERIC_ERROR} if an error
     *               occurred.
     */
    void stopAudioRecordingResult(int result);
}
