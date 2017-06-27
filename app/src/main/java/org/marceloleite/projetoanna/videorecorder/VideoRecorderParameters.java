package org.marceloleite.projetoanna.videorecorder;

import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

/**
 * Created by marcelo on 26/06/17.
 */

public class VideoRecorderParameters {

    private final AppCompatActivity appCompatActivity;

    private final TextureView textureView;

    public VideoRecorderParameters(AppCompatActivity appCompatActivity, TextureView textureView) {
        this.appCompatActivity = appCompatActivity;
        this.textureView = textureView;
    }

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public TextureView getTextureView() {
        return textureView;
    }
}
