package org.marceloleite.projetoanna.ui.listeners.buttonrecord;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.Main;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ButtonRecordOnClickListener.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private ButtonRecordInterface buttonRecordInterface;

    public ButtonRecordOnClickListener(ButtonRecordInterface buttonRecordInterface) {
        this.buttonRecordInterface = buttonRecordInterface;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (buttonRecordInterface.isRecording()) {
            buttonRecordInterface.finishRecording();
        } else {
            buttonRecordInterface.startRecording();
        }
        button.setEnabled(false);
    }
}
