package org.marceloleite.projetoanna.audiorecorder;

import org.marceloleite.projetoanna.utils.GenericReturnCodes;

/**
 * Codes returned from audio recorder methods.
 */
public abstract class AudioRecorderReturnCodes extends GenericReturnCodes {

    /**
     * Informs that the connection with audio recorder was lost.
     */
    public static final int CONNECTION_LOST = 50;

    /**
     * Informs that the audio recorder was disconnected.
     */
    public static final int DISCONNECTED = 51;
}
