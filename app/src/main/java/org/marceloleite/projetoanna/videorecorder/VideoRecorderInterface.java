package org.marceloleite.projetoanna.videorecorder;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 15/05/2017.
 */

public interface VideoRecorderInterface {

    void startVideoRecordingResult(int result);

    void stopVideoRecordingResult(int result);

    AppCompatActivity getAppCompatActivity();
}
