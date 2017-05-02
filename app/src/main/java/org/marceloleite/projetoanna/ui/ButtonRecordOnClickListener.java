package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.camera.CameraController;
import org.marceloleite.projetoanna.camera.CameraPreview;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    private AudioRecorder audioRecorder;

    private CameraController cameraController;

    public ButtonRecordOnClickListener(AudioRecorder audioRecorder, CameraController cameraController) {
        this.cameraController = cameraController;
        this.audioRecorder = audioRecorder;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (audioRecorder.isRecording()) {
            cameraController.stopRecording();
            audioRecorder.stopRecord();
        } else {
            audioRecorder.startAudioRecord();
            cameraController.startRecording();
        }
        button.setEnabled(false);
    }
}
