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

public class ButtonPlayOnClickListener implements View.OnClickListener {

    private File videoFile;

    private Context context;

    public ButtonPlayOnClickListener(Context context, File videoFile) {
        this.context = context;
        this.videoFile = videoFile;
    }

    @Override
    public void onClick(View view) {
        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoFile.getAbsolutePath()));
        playVideoIntent.setDataAndType(Uri.parse(videoFile.getAbsolutePath()), MediaFormat.MIMETYPE_VIDEO_H263);
        context.startActivity(playVideoIntent);
    }
}
