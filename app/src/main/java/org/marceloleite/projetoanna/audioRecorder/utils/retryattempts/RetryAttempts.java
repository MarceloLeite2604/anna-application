package org.marceloleite.projetoanna.audioRecorder.utils.retryattempts;

import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.utils.GenericReturnCodes;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class RetryAttempts {



    private static final int MINIMUM_WAIT_TIME = 150;

    private static final int WAIT_TIME_STEP = 10;

    private int maximumAttempts;

    private int totalAttempts;

    public RetryAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
        this.totalAttempts = 0;
    }

    private boolean increateAttempts() {
        if (this.totalAttempts < this.maximumAttempts) {
            totalAttempts++;
            return true;
        } else {
            return false;
        }
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public static int wait(RetryAttempts retryAttempts) {
        if (retryAttempts.increateAttempts()) {
            int waitTime = MINIMUM_WAIT_TIME;
            waitTime += retryAttempts.getTotalAttempts() * WAIT_TIME_STEP;

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException interruptedException) {
                Log.e(MainActivity.LOG_TAG, "wait, 58: " + interruptedException);
            }
        }
        else {
            return RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED;
        }

        return RetryAttemptsReturnCode.SUCCESS;
    }

    public abstract class RetryAttemptsReturnCode extends GenericReturnCodes {



    }
}
