package org.marceloleite.projetoanna.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReport;

public class ProgressMonitoringView extends LinearLayout {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ProgressMonitoringView.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private ProgressBar progressBar;

    private TextView textViewMessage;

    private TextView textViewPercentage;

    public ProgressMonitoringView(Context context) {
        super(context);
    }

    public ProgressMonitoringView(@NonNull Context context, String initialMessage) {
        this(context, initialMessage, 0);
    }

    /**
     * Constructor.
     *
     * @param context The context of the application which the linear layout should be created.
     */
    public ProgressMonitoringView(@NonNull Context context, String initialMessage, int initialPercentage) {
        super(context);

        inflate(context, R.layout.progress_monitoring, this);
        textViewMessage = findViewById(R.id.progress_monitoring_text);
        Log.d(LOG_TAG, "ProgressMonitoringView (51): " + textViewMessage);
        progressBar = findViewById(R.id.progress_monitoring_progressbar);
        Log.d(LOG_TAG, "ProgressMonitoringView (51): " + progressBar);
        textViewPercentage = findViewById(R.id.progress_monitoring_progressbar_percentage);
        setMessage(initialMessage);
        setPercentage(initialPercentage);
    }

    private void setMessage(String text) {
        textViewMessage.setText(text);
    }

    private void setPercentage(int value) {
        progressBar.setProgress(value);
        String percentage = value + "%";
        textViewPercentage.setText(percentage);
    }

    public void updateProgressInformations(ProgressReport progressReport) {
        setMessage(progressReport.getMessage());
        setPercentage(progressReport.getPercentageConcluded());
    }
}
