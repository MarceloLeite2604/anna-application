package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    private AudioRecorder audioRecorder;

    private VideoRecorder videoRecorder;

    public ButtonRecordOnClickListener(AudioRecorder audioRecorder, VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
        this.audioRecorder = audioRecorder;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        /* if (audioRecorder.isRecording()) */
        if (videoRecorder.isRecording()) {
            videoRecorder.stopRecord();
            //audioRecorder.stopRecord();
        } else {
            // audioRecorder.startAudioRecord();
            videoRecorder.startRecord();
        }
        //button.setEnabled(false);
    }
}
