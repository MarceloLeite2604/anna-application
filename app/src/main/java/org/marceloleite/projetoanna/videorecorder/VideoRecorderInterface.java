package org.marceloleite.projetoanna.videorecorder;

/**
 * Specifies the methods required for create a {@link VideoRecorder} object, so it can interact with
 * the specified object.
 */
public interface VideoRecorderInterface {

    /**
     * Inform the result of the start video recording request.
     *
     * @param result The result of the start video recording request.
     */
    void startVideoRecordingResult(int result);

    /**
     * Inform the result of the stop video recording request.
     *
     * @param result The result of the stop video recording request.
     */
    void stopVideoRecordingResult(int result);

    /**
     * Return the parameters informed to create the {@link VideoRecorder} object.
     *
     * @return The parameters informed to create the {@link VideoRecorder} object.
     */
    VideoRecorderParameters getVideoRecorderParameters();
}
