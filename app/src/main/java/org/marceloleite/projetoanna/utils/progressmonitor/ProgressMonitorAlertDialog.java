package org.marceloleite.projetoanna.utils.progressmonitor;

import android.app.AlertDialog;
import android.content.Context;

import org.marceloleite.projetoanna.ui.InformationView;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 18/07/17.
 */

class ProgressMonitorAlertDialog extends AlertDialog {

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


    private InformationView informationView;


    ProgressMonitorAlertDialog(Context context, String title) {
        super(context);
        setTitle(title);
        setCancelable(false);
        this.informationView = new InformationView(context);
        this.informationView.showProgressBar(true);
        setView(informationView);
    }

    void updateProgressInformations(ProgressReport progressReport) {
        int percentage = (int) progressReport.getPercentageConcluded();
        informationView.setInformationText(progressReport.getMessage());
        informationView.setProgressBarValue(percentage);
    }
}
