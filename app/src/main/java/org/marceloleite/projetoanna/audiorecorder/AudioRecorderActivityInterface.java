package org.marceloleite.projetoanna.audiorecorder;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 29/04/2017.
 */

public interface AudioRecorderActivityInterface {

    AppCompatActivity getActivity();

    void connectWithAudioRecorderResult(int result);

    void disconnectFromAudioRecorderResult(int result);

    void requestLatestAudioFileResult(int result);

    void startAudioRecordingResult(int result);

    void stopAudioRecordingResult(int result);
}
