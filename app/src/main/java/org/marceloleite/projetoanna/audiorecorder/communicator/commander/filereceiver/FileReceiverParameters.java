package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import android.content.Context;

import org.marceloleite.projetoanna.audiorecorder.communicator.senderreceiver.SenderReceiver;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressMonitorAlertDialog;

/**
 * Created by marcelo on 18/07/17.
 */

public class FileReceiverParameters {

    private Context context;
    private SenderReceiver senderReceiver;

    public FileReceiverParameters(Context context, SenderReceiver senderReceiver) {
        this.context = context;
        this.senderReceiver = senderReceiver;
    }

    public Context getContext() {
        return context;
    }

    public SenderReceiver getSenderReceiver() {
        return senderReceiver;
    }
}
