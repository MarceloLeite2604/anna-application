package org.marceloleite.projetoanna.utils.retryattempts;


import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by Marcelo Leite on 18/04/2017.
 */

public class RetryAttempts {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = RetryAttempts.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(RetryAttempts.class);
    }

    private static final int MINIMUM_WAIT_TIME = 150;

    private static final int WAIT_TIME_STEP = 10;

    private int maximumAttempts;

    private int totalAttempts;

    private int minimumWaitTime;

    private int waitTimeStep;

    public RetryAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
        this.minimumWaitTime = MINIMUM_WAIT_TIME;
        this.waitTimeStep = WAIT_TIME_STEP;
        this.totalAttempts = 0;
    }

    public RetryAttempts(int maximumAttempts, int minimumWaitTime, int waitTimeStep) {
        this.maximumAttempts = maximumAttempts;
        this.minimumWaitTime = minimumWaitTime;
        this.waitTimeStep = waitTimeStep;
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

    public int getMaximumAttempts() {
        return maximumAttempts;
    }

    public int getMinimumWaitTime() {
        return minimumWaitTime;
    }

    public int getWaitTimeStep() {
        return waitTimeStep;
    }

    public static int wait(RetryAttempts retryAttempts) {
        if (retryAttempts.increateAttempts()) {
            int waitTime = retryAttempts.getMinimumWaitTime();
            waitTime += retryAttempts.getTotalAttempts() * retryAttempts.getWaitTimeStep();

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException interruptedException) {
                Log.e(RetryAttempts.class, LOG_TAG, "wait (84): " + interruptedException);
            }
        } else {
            return RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED;
        }

        return RetryAttemptsReturnCode.SUCCESS;
    }

    public abstract class RetryAttemptsReturnCode extends GenericReturnCodes {


    }
}
