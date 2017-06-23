package org.marceloleite.projetoanna.utils.retryattempts;

import org.marceloleite.projetoanna.utils.GenericReturnCodes;

/**
 * The codes returned from a {@link RetryAttempts#wait()} operation.
 */
public class RetryAttemptsReturnCodes extends GenericReturnCodes {

    /**
     * Indicates that the maximum numbers of retry attempts was reached.
     */
    public static final int MAX_RETRY_ATTEMPTS_REACHED = 50;
}
