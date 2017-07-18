package org.marceloleite.projetoanna.utils.progressmonitor;

/**
 * Created by marcelo on 18/07/17.
 */

public class ProgressReport {

    public static final double MAX_PERCENTAGE_CONCLUDED = 1.0;

    public static final double MIN_PERCENTAGE_CONCLUDED = 0.0;

    private String message;

    private double percentageConcluded;

    public ProgressReport(String message, double percentageConcluded) {
        this.percentageConcluded = percentageConcluded;
        this.message = message;
    }

    public double getPercentageConcluded() {
        return percentageConcluded;
    }

    public String getMessage() {
        return message;
    }
}
