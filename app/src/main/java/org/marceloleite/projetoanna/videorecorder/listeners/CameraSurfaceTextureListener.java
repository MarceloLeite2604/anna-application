package org.marceloleite.projetoanna.videorecorder.listeners;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.TextureView;

import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class CameraSurfaceTextureListener implements TextureView.SurfaceTextureListener {

    private VideoRecorder videoRecorder;

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
