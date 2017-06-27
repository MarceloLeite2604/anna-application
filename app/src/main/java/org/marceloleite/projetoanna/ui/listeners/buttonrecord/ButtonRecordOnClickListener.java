package org.marceloleite.projetoanna.ui.listeners.buttonrecord;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link android.view.View.OnClickListener} for the button which controls the audio and video recording for the application.
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

    /**
     * The object which contains the methods used by this class to control the audio and video recording.
     */
    private ButtonRecordInterface buttonRecordInterface;

    /**
     * Constructor.
     *
     * @param buttonRecordInterface The object which contains the methods used by this class to control the audio and video recording.
     */
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
