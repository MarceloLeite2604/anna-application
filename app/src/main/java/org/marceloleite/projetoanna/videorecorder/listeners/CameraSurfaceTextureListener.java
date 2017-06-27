package org.marceloleite.projetoanna.videorecorder.listeners;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.TextureView;

/**
 * A {@link TextureView.SurfaceTextureListener} object to receive notification about changes on the
 * surface associated with a texture view and request a camera to be opened.
 */
public class CameraSurfaceTextureListener implements TextureView.SurfaceTextureListener {

    /**
     * The object which controls the camera opening.
     */
    private final CameraSurfaceTextureInterface cameraSurfaceTextureInterface;

    /**
     * Constructor.
     *
     * @param cameraSurfaceTextureInterface The object which controls the camera opening.
     */
    public CameraSurfaceTextureListener(CameraSurfaceTextureInterface cameraSurfaceTextureInterface) {
        this.cameraSurfaceTextureInterface = cameraSurfaceTextureInterface;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Size surfaceSize = new Size(width, height);
        cameraSurfaceTextureInterface.openCamera(surfaceSize);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
}
