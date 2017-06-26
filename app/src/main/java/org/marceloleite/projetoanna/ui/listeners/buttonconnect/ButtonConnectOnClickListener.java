package org.marceloleite.projetoanna.ui.listeners.buttonconnect;

import android.view.View;
import android.widget.Button;

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
        Log.addClassToLog(LOG_TAG);
    }

    ButtonConnectInterface buttonConnectInterface;

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
