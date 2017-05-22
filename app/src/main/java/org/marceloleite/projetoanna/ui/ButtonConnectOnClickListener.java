package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener {

    private static final String LOG_TAG = ButtonConnectOnClickListener.class.getSimpleName();

    MainActivity mainActivity;

    public ButtonConnectOnClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (mainActivity.getAudioRecorder().isConnected()) {
            mainActivity.startDisconnectionFromAudioRecorder();
        } else {
            mainActivity.startConnectionWithAudioRecorder();
        }
        button.setEnabled(false);
    }
}
