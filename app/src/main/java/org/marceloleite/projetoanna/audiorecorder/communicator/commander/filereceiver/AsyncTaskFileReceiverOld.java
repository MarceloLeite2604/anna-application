package org.marceloleite.projetoanna.audiorecorder.communicator.commander.filereceiver;

import android.os.AsyncTask;

import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.utils.progressmonitor.ProgressReport;

/**
 * Created by marcelo on 18/07/17.
 */

public class AsyncTaskFileReceiverOld extends AsyncTask<FileReceiverParameters, Integer, RequestLatestAudioFileResult> /*implements ProgressReporterOld*/ {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AsyncTaskFileReceiverOld.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private static final String PROGRESS_MESSAGE = "Receiving file";

    private AsyncTaskFileReceiver asyncTaskFileReceiver;

    private RequestLatestAudioFileResult requestLatestAudioFileResult;


    @Override
    protected RequestLatestAudioFileResult doInBackground(FileReceiverParameters... fileReceiverParameterses) {
        Log.d(LOG_TAG, "doInBackground (37): Starting file receiver task.");
        FileReceiverParameters fileReceiverParameters = fileReceiverParameterses[0];

        /*this.asyncTaskFileReceiver = new AsyncTaskFileReceiver(fileReceiverParameters.getContext(), fileReceiverParameters.getSenderReceiver());
        ReceiveFileResult receiveFileResult = asyncTaskFileReceiver.receiveFile();
        if (receiveFileResult.getReturnCode() == GenericReturnCodes.SUCCESS) {
            this.requestLatestAudioFileResult = new RequestLatestAudioFileResult(GenericReturnCodes.SUCCESS, receiveFileResult.getFileReceived());
        } else {
            this.requestLatestAudioFileResult = new RequestLatestAudioFileResult(GenericReturnCodes.GENERIC_ERROR, null);
        }

        Log.d(LOG_TAG, "doInBackground (48): Concluding file receiver task.");*/
        return this.requestLatestAudioFileResult;
    }

    public ProgressReport reportProgress() {
        /*double percentageConcluded = asyncTaskFileReceiver.getPercentageConcluded();
        Log.d(LOG_TAG, "reportProgress (54): Percentage concluded: " + percentageConcluded);
        return new ProgressReport(PROGRESS_MESSAGE, percentageConcluded);*/
        return null;
    }

    public RequestLatestAudioFileResult getRequestLatestAudioFileResult() {
        return requestLatestAudioFileResult;
    }
}
