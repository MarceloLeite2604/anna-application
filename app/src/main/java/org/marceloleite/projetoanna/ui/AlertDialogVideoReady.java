package org.marceloleite.projetoanna.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.ui.listeners.ButtonPlayOnClickListener;
import org.marceloleite.projetoanna.ui.listeners.ButtonShareOnClickListener;
import org.marceloleite.projetoanna.utils.Log;

import java.io.File;

/**
 * Created by marcelo on 24/07/17.
 */
public class AlertDialogVideoReady extends AlertDialog {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = AlertDialogVideoReady.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    private View videoReadyView;

    public AlertDialogVideoReady(AppCompatActivity appCompatActivity, File videoFile) {
        super(appCompatActivity);
        Log.d(LOG_TAG, "AlertDialogVideoReady (42): Video file: " + videoFile);
        videoReadyView = View.inflate(appCompatActivity, R.layout.video_ready, null);
        createButtonListeners(appCompatActivity, videoFile);
        setVideoThumbnail(appCompatActivity, videoFile);

        setIcon(0);
        setTitle(appCompatActivity.getString(R.string.video_ready_title));
        setView(videoReadyView);
    }

    private Bitmap createVideoThumbnail(AppCompatActivity appCompatActivity, File videoFile) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoFile.getAbsolutePath());
        Bitmap videoFrame = mediaMetadataRetriever.getFrameAtTime();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int thumbnailWidth = displayMetrics.widthPixels / 4;
        double scaleRatio = (double) thumbnailWidth / (double) videoFrame.getWidth();

        Size videoThumbnailSize = new Size(thumbnailWidth, (int) (scaleRatio * videoFrame.getHeight()));

        return Bitmap.createScaledBitmap(videoFrame, videoThumbnailSize.getWidth(), videoThumbnailSize.getHeight(), true);
    }

    private void setVideoThumbnail(AppCompatActivity appCompatActivity, File videoFile) {
        ImageView thumbnailImageView = videoReadyView.findViewById(R.id.video_ready_image_view);
        Bitmap movieFileThumbnailBitmap = createVideoThumbnail(appCompatActivity, videoFile);
        thumbnailImageView.setImageBitmap(movieFileThumbnailBitmap);
    }

    private void createButtonListeners(Context context, File videoFile) {
        Button buttonPlay = videoReadyView.findViewById(R.id.video_ready_button_play);
        buttonPlay.setOnClickListener(new ButtonPlayOnClickListener(context, videoFile));

        Button buttonShare = videoReadyView.findViewById(R.id.video_ready_button_share);
        buttonShare.setOnClickListener(new ButtonShareOnClickListener(context, videoFile));
    }
}
