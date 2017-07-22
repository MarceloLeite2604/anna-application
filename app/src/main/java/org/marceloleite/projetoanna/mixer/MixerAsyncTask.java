package org.marceloleite.projetoanna.mixer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressMonitorAlertDialog;

import java.io.File;

/**
 * Extends the {@link AsyncTask} class to execute the recorded audio and video mixing.
 */
public class MixerAsyncTask extends AsyncTask<MixerAsyncTaskParameters, Integer, File> {

    private static final String PROGRESS_MONITOR_DIALOG_TITLE = "Mixing";

    private static final String PROGRESS_MONITOR_DIALOG_MESSAGE = "Mixing audio and video.";

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MixerAsyncTask.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private AppCompatActivity appCompatActivity;

    /**
     * The object which contains the method to be executed once the mixing is concluded.
     */
    private MixerAsyncTaskInterface mixerAsyncTaskInterface;

    private ProgressMonitorAlertDialog progressMonitorAlertDialog;

    /**
     * Constructor.
     *
     * @param mixerAsyncTaskInterface The object which contains the method to be executed once the mixing is concluded.
     */
    public MixerAsyncTask(AppCompatActivity appCompatActivity, MixerAsyncTaskInterface mixerAsyncTaskInterface) {
        this.appCompatActivity = appCompatActivity;
        this.mixerAsyncTaskInterface = mixerAsyncTaskInterface;
    }

    @Override
    protected File doInBackground(MixerAsyncTaskParameters... mixerAsyncTaskParametersArray) {

        File mixedFile = null;
        for (MixerAsyncTaskParameters mixerAsyncTaskParameters : mixerAsyncTaskParametersArray) {

            Context context = mixerAsyncTaskParameters.getContext();
            File movieFile = mixerAsyncTaskParameters.getVideoFile();
            File audioFile = mixerAsyncTaskParameters.getAudioFile();
            long startAudioDelay = mixerAsyncTaskParameters.getAudioAndVideoDelayTime();

            mixedFile = Mixer.mixAudioAndVideo(context, audioFile, movieFile, startAudioDelay);
        }

        return mixedFile;
    }

    @Override
    protected void onPostExecute(File file) {
        appCompatActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressMonitorAlertDialog.hide();
            }
        });
        mixerAsyncTaskInterface.mixConcluded(file);
        super.onPostExecute(file);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        appCompatActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressMonitorAlertDialog = new ProgressMonitorAlertDialog(appCompatActivity, PROGRESS_MONITOR_DIALOG_TITLE, PROGRESS_MONITOR_DIALOG_MESSAGE);
                progressMonitorAlertDialog.show();
            }
        });
    }
}
