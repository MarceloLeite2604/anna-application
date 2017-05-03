package org.marceloleite.projetoanna.mixer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTask extends AsyncTask<MixerAsyncTaskParameters, Integer, File> {

    private static final String LOG_TAG = MixerAsyncTask.class.getSimpleName();

    @Override
    protected File doInBackground(MixerAsyncTaskParameters... mixerAsyncTaskParametersArray) {

        for (int counter = 0; counter < mixerAsyncTaskParametersArray.length; counter++) {
            MixerAsyncTaskParameters mixerAsyncTaskParameters = mixerAsyncTaskParametersArray[counter];
            Log.d(LOG_TAG, "doInBackground, 22: Mixing file \"" + mixerAsyncTaskParameters.getVideoFileAbsolutePath() + "\".");
            Mixer mixer = new Mixer(mixerAsyncTaskParameters.getVideoFileAbsolutePath(), mixerAsyncTaskParameters.getAudioFileAbsolutePath());
            try {
                mixer.test();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
