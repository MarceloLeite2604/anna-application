package org.marceloleite.projetoanna.utils.progressmonitor;

/**
 * Created by marcelo on 18/07/17.
 */

public class ProgressReport {

    private String message;

    private int percentageConcluded;

    public ProgressReport(String message, int percentageConcluded) {
        this.percentageConcluded = percentageConcluded;
        this.message = message;
    }

    public int getPercentageConcluded() {
        return percentageConcluded;
    }

    public String getMessage() {
        return message;
    }
}
