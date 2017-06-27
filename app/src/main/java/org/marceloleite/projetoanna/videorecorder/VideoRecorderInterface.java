package org.marceloleite.projetoanna.videorecorder;

/**
 * Created by Marcelo Leite on 15/05/2017.
 */

public interface VideoRecorderInterface {

    void startVideoRecordingResult(int result);

    void stopVideoRecordingResult(int result);

    VideoRecorderParameters getVideoRecorderParameters();
}
