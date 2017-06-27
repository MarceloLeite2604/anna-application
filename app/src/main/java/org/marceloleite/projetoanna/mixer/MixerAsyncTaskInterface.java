package org.marceloleite.projetoanna.mixer;

import java.io.File;

/**
 * Establishes the method to be executed once the {@link MixerAsyncTask} operation is concluded.
 */
public interface MixerAsyncTaskInterface {

    /**
     * The method to be executed once the audio and video mixing is concluded.
     *
     * @param mixedFile The file which contains the mixed audio and video.
     */
    void mixConcluded(File mixedFile);
}
