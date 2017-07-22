package org.marceloleite.projetoanna.utils.progressmonitor;

import android.app.AlertDialog;
import android.content.Context;

import org.marceloleite.projetoanna.ui.ProgressMonitoringView;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 18/07/17.
 */
public class ProgressMonitorAlertDialog extends AlertDialog {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ProgressMonitorAlertDialog.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }


    private ProgressMonitoringView progressMonitoringView;


    public ProgressMonitorAlertDialog(Context context, String title, String initialMessage) {
        super(context);
        setTitle(title);
        setCancelable(false);
        this.progressMonitoringView = new ProgressMonitoringView(context, initialMessage);
        setView(progressMonitoringView);
    }

    public void updateProgressInformations(ProgressReport progressReport) {
        progressMonitoringView.updateProgressInformations(progressReport);
    }
}
