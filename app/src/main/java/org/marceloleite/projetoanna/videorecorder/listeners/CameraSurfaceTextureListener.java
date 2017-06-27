package org.marceloleite.projetoanna.videorecorder.listeners;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.TextureView;

import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * A {@link TextureView.SurfaceTextureListener} object to receive notification about changes on the
 * surface associated with a texture view and request a camera to be opened.
 */
public class CameraSurfaceTextureListener implements TextureView.SurfaceTextureListener {

    /**
     * The object which controls the camera opening.
     */
    private final VideoRecorder videoRecorder;

    /**
     * Constructor.
     *
     * @param videoRecorder The object which controls the camera opening.
     */
    public CameraSurfaceTextureListener(VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Size surfaceSize = new Size(width, height);
        videoRecorder.openCamera(surfaceSize);
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
