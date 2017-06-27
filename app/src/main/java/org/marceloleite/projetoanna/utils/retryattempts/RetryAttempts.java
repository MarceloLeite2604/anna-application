package org.marceloleite.projetoanna.utils.retryattempts;


import org.marceloleite.projetoanna.utils.Log;

/**
 * Stores the number of attempts made to retry an operation and waits a specified amount of time
 * before retry the operation. Each time a retry attempt is waited, it will sleep its minimum wait
 * time, plus the step time multiplied by the current number of attempts.
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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The default value for the minimum time to wait to retry an operation.
     */
    private static final int DEFAULT_MINIMUM_WAIT_TIME = 150;

    /**
     * The default value for the wait time step.
     */
    private static final int DEFAULT_WAIT_TIME_STEP = 10;

    /**
     * The maximum attempts to retry an operation.
     */
    private int maximumAttempts;

    /**
     * The total of attempts to realize an operation,
     */
    private int totalAttempts;

    /**
     * The minimum time to wait to retry an operation.
     */
    private int minimumWaitTime;

    /**
     * The step time to retry an operation.
     */
    private int waitTimeStep;

    /**
     * Object constructor.
     *
     * @param maximumAttempts The maximum number of attempts to retry.
     */
    public RetryAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
        this.minimumWaitTime = DEFAULT_MINIMUM_WAIT_TIME;
        this.waitTimeStep = DEFAULT_WAIT_TIME_STEP;
        this.totalAttempts = 0;
    }

    /**
     * Object constructor.
     *
     * @param maximumAttempts The maximum number of attempts to retry.
     * @param minimumWaitTime The minimum time to wait before a new attempt is realized.
     * @param waitTimeStep    The step time to increase each wait time.
     */
    public RetryAttempts(int maximumAttempts, int minimumWaitTime, int waitTimeStep) {
        this.maximumAttempts = maximumAttempts;
        this.minimumWaitTime = minimumWaitTime;
        this.waitTimeStep = waitTimeStep;
        this.totalAttempts = 0;
    }

    /**
     * Increase the number of retry attempts.
     *
     * @return True if the number of attempts was increased. False if it has reached its limit.
     */
    private boolean increaseAttempts() {
        if (this.totalAttempts < this.maximumAttempts) {
            totalAttempts++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Waits for the next retry attempt. The wait time is calculated by its minimum wait
     * time, plus the step time multiplied by the current number of attempts.
     *
     * @return {@link RetryAttemptsReturnCodes#MAX_RETRY_ATTEMPTS_REACHED} if the maximum numbers of retry attempts was reached, {@link RetryAttemptsReturnCodes#SUCCESS} otherwise.
     */
    public int waitForNextAttempt() {
        if (increaseAttempts()) {
            int waitTime = minimumWaitTime;
            waitTime += totalAttempts * waitTimeStep;

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException interruptedException) {
                Log.e(LOG_TAG, "wait (84): " + interruptedException);
            }
        } else {
            return RetryAttemptsReturnCodes.MAX_RETRY_ATTEMPTS_REACHED;
        }

        return RetryAttemptsReturnCodes.SUCCESS;
    }
}
