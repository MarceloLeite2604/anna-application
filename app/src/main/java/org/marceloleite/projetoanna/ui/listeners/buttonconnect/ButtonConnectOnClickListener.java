package org.marceloleite.projetoanna.ui.listeners.buttonconnect;

import android.view.View;
import android.widget.Button;

import org.marceloleite.projetoanna.utils.Log;

/**
 * A {@link android.view.View.OnClickListener} for the button which controls the connection with an audio recorder.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The object which contains the methods used by this class to control the connection with an audio recorder.
     */
    private ButtonConnectInterface buttonConnectInterface;

    /**
     * Constructor.
     *
     * @param buttonConnectInterface The object which contains the methods used by this class to control the connection with an audio recorder.
     */
    public ButtonConnectOnClickListener(ButtonConnectInterface buttonConnectInterface) {
        this.buttonConnectInterface = buttonConnectInterface;
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (buttonConnectInterface.isConnected()) {
            buttonConnectInterface.disconnectFromAudioRecorder();
        } else {
            buttonConnectInterface.connectWithAudioRecorder();
        }
        button.setEnabled(false);
    }
}
