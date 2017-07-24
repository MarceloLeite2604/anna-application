package org.marceloleite.projetoanna.ui.listeners;

import android.content.Context;
import android.content.Intent;
import android.media.MediaFormat;
import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * Created by marcelo on 24/07/17.
 */

public class ButtonShareOnClickListener implements View.OnClickListener {

    private Context context;

    private File videoFile;

    public ButtonShareOnClickListener(Context context, File videoFile) {
        this.context = context;
        this.videoFile = videoFile;
    }

    @Override
    public void onClick(View view) {
        Uri videoFileUri = Uri.parse(videoFile.getAbsolutePath());
        Intent shareVideoIntent = new Intent(Intent.ACTION_SEND, videoFileUri);
        shareVideoIntent.putExtra(Intent.EXTRA_SUBJECT, "My video recorded on Anna.");
        shareVideoIntent.putExtra(Intent.EXTRA_TITLE, "My video recorded on Anna.");
        shareVideoIntent.putExtra(Intent.EXTRA_STREAM, videoFileUri);
        shareVideoIntent.setDataAndType(Uri.parse(videoFile.getAbsolutePath()), MediaFormat.MIMETYPE_VIDEO_MPEG4);
        context.startActivity(Intent.createChooser(shareVideoIntent, "Share this video"));
    }
}
