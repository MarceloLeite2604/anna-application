package org.marceloleite.projetoanna.utils.progressmonitor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 18/07/17.
 */

public abstract class AsyncTaskMonitorProgressOld extends AsyncTask</*ProgressReporterOld*/Void, ProgressReport, /*ProgressReporterOld*/Void> {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskMonitorProgressOld.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Default value to update the progress (in miliseconds).
     */
    private static final int DEFAULT_PROGRESS_REPORT_INTERVAL = 500;

    private static final String DEFAULT_INITIAL_MESSAGE = "Please wait.";

    private Integer progressReportInterval;

    private ProgressMonitorAlertDialog progressMonitorAlertDialog;

    private AppCompatActivity appCompatActivity;

    private boolean monitoringConcluded;

    public AsyncTaskMonitorProgressOld(AppCompatActivity appCompatActivity) {
        super();
        progressReportInterval = DEFAULT_PROGRESS_REPORT_INTERVAL;
        this.appCompatActivity = appCompatActivity;
        this.monitoringConcluded = false;
    }

    public AsyncTaskMonitorProgressOld(AppCompatActivity appCompatActivity, int progressReportInterval) {
        super();
        this.progressReportInterval = progressReportInterval;
        this.appCompatActivity = appCompatActivity;
        this.monitoringConcluded = false;
    }

    /*@Override
    protected ProgressReporterOld doInBackground(ProgressReporterOld... progressReporterOlds) {
        Log.d(LOG_TAG, "doInBackground (57): Starting progress monitor task.");
        ProgressReporterOld progressReporterOld = progressReporterOlds[0];

        createProgressMonitorAlertDialog();
        Log.d(LOG_TAG, "doInBackground (62): Showing dialog.");

        while (!monitoringConcluded) {
            try {
                Thread.sleep(progressReportInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException("Exception while waiting for file monitoring a progress.", e);
            }
            publishProgress(progressReporterOld.reportProgress());
        }

        progressMonitorAlertDialog.dismiss();

        Log.d(LOG_TAG, "doInBackground (72): Progress monitor task concluded.");
        return progressReporterOld;
    }*/

    @Override
    protected void onProgressUpdate(ProgressReport... progressReports) {
        ProgressReport progressReport = progressReports[0];
        Log.d(LOG_TAG, "onProgressUpdate (78): Updating progress information.");
        monitoringConcluded = (progressReport.getPercentageConcluded() == 1.0);
        this.progressMonitorAlertDialog.updateProgressInformations(progressReports[0]);
    }

    private void createProgressMonitorAlertDialog() {
        final Thread createProgressMonitorAlertDialogRunnable = new Thread() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "run (91): Creating progress monitor alert dialog.");
                progressMonitorAlertDialog = new ProgressMonitorAlertDialog(appCompatActivity, DEFAULT_INITIAL_MESSAGE);
                progressMonitorAlertDialog.show();
                Log.d(LOG_TAG, "run (91): Progress monitor alert dialog created.");
            }
        };

        Thread createProgressMonitorAlertDialogThread = new Thread() {
            public void run() {
                appCompatActivity.runOnUiThread(createProgressMonitorAlertDialogRunnable);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        createProgressMonitorAlertDialogThread.start();
        Log.d(LOG_TAG, "createProgressMonitorAlertDialog (100): Thread started.");
        try {
            createProgressMonitorAlertDialogRunnable.join();
            createProgressMonitorAlertDialogThread.join();
            Log.d(LOG_TAG, "createProgressMonitorAlertDialog (103): Thread concluded.");
        } catch (InterruptedException e) {
            throw new RuntimeException("Exception while waiting for \"createProgressMonitorAlertDialog\" thread to conclude.", e);
        }


    }
}
