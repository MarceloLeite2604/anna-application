package org.marceloleite.projetoanna.videorecorder;

import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

/**
 * The parameters informed to construct a {@link VideoRecorder} object.
 */
public class VideoRecorderParameters {

    /**
     * The application in execution.
     */
    private final AppCompatActivity appCompatActivity;

    /**
     * The texture view which should be used to show the video preview.
     */
    private final TextureView textureView;

    /**
     * Constructor.
     *
     * @param appCompatActivity The application in execution.
     * @param textureView       The texture view which should be used to show the video preview.
     */
    public VideoRecorderParameters(AppCompatActivity appCompatActivity, TextureView textureView) {
        this.appCompatActivity = appCompatActivity;
        this.textureView = textureView;
    }

    /**
     * Returns the application in execution.
     *
     * @return The application in execution.
     */
    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    /**
     * Returns the texture view which should be used to show the video preview.
     *
     * @return The texture view which should be used to show the video preview.
     */
    public TextureView getTextureView() {
        return textureView;
    }
}
