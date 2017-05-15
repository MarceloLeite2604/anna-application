package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

import java.io.File;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    private MainActivity mainActivity;

    public ButtonRecordOnClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        AudioRecorder audioRecorder = mainActivity.getAudioRecorder();
        VideoRecorder videoRecorder = mainActivity.getVideoRecorder();
        if (audioRecorder.isRecording() && videoRecorder.isRecording()) {
            mainActivity.stopRecording();
        } else {
            mainActivity.startRecording();
        }
        button.setEnabled(false);
    }
}
