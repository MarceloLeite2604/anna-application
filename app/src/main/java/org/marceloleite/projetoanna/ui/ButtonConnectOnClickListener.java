package org.marceloleite.projetoanna.ui;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 17/03/2017.
 */

public class ButtonConnectOnClickListener implements View.OnClickListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ButtonConnectOnClickListener.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(ButtonConnectOnClickListener.class);
    }

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
