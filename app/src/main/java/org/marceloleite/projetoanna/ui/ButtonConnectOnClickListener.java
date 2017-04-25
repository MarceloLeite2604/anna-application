package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener {

    private static final String LOG_TAG = ButtonConnectOnClickListener.class.getSimpleName();

    AudioRecorder audioRecorder;

    public ButtonConnectOnClickListener(AudioRecorder audioRecorder) {
        this.audioRecorder = audioRecorder;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (audioRecorder.isConnected()) {
            audioRecorder.disconnect();
        } else {
            audioRecorder.connect();
        }
        button.setEnabled(false);
    }
}
