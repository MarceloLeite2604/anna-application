package org.marceloleite.projetoanna.mixer;

import android.content.Context;
import android.os.AsyncTask;

import org.marceloleite.projetoanna.utils.Log;

import java.io.File;

/**
 * Extends the {@link AsyncTask} class to execute the recorded audio and video mixing.
 */
public class MixerAsyncTask extends AsyncTask<MixerAsyncTaskParameters, Integer, File> {

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

    /**
     * The object which contains the method to be executed once the mixing is concluded.
     */
    private MixerAsyncTaskInterface mixerAsyncTaskInterface;

    /**
     * Constructor.
     *
     * @param mixerAsyncTaskInterface The object which contains the method to be executed once the mixing is concluded.
     */
    public MixerAsyncTask(MixerAsyncTaskInterface mixerAsyncTaskInterface) {
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
        mixerAsyncTaskInterface.mixConcluded(file);
        super.onPostExecute(file);
    }
}
