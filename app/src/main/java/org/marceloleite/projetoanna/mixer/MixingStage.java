package org.marceloleite.projetoanna.mixer;

import org.marceloleite.projetoanna.utils.Log;

/**
 * Created by marcelo on 24/07/17.
 */

enum MixingStage {
    CONVERT_MP3_TO_RAW("Converting MP3 to RAW", 4.5),
    ENCODE_RAW_TO_AAC("Encoding RAW audio to AAC", 4.5),
    COPYING_VIDEO_TO_MOVIE("Copying video to movie.", 1);

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MixingStage.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private String description;

    private double weight;

    MixingStage(String description, double weight) {
        this.description = description;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return description;
    }

    public double getRelativeWeight() {
        return weight / getTotalWeight();
    }

    private static double getTotalWeight() {
        double totalWeight = 0;

        for (MixingStage mixingStage : MixingStage.values()) {
            totalWeight += mixingStage.weight;
        }

        return totalWeight;
    }

    public static int getStartPercentageFor(MixingStage mixingStage) {
        double previousMixingStagesWeight = 0;
        int startPercentage;
        for (MixingStage previousMixingStages : MixingStage.values()) {
            if (previousMixingStages == mixingStage) {
                break;
            } else {
                previousMixingStagesWeight += previousMixingStages.weight;
            }
        }

        startPercentage = (int) ((previousMixingStagesWeight / getTotalWeight()) * 100.0);

        return startPercentage;

    }
}
