package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.audioRecorder.AudioRecorder;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    private AudioRecorder audioRecorder;

    public ButtonRecordOnClickListener(AudioRecorder audioRecorder) {
        this.audioRecorder = audioRecorder;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (audioRecorder.isRecording()) {
            audioRecorder.stopRecord();
        } else {
            audioRecorder.startAudioRecord();
        }
        button.setEnabled(false);
    }
}