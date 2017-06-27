package org.marceloleite.projetoanna.videorecorder.listeners;

import android.util.Size;

/**
 * Specifies the methods required for the {@link CameraSurfaceTextureListener} object to inform its
 * results.
 */
public interface CameraSurfaceTextureInterface {

    /**
     * Method to open the camera.
     *
     * @param surfacePreviewSize The size of the preview surface.
     */
    void openCamera(Size surfacePreviewSize);
}
