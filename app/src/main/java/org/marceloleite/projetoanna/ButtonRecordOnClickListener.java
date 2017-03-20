package org.marceloleite.projetoanna;

import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 20/03/2017.
 */

public class ButtonRecordOnClickListener implements View.OnClickListener {

    private MainActivity mainActivity;

    public ButtonRecordOnClickListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
        try {
            OutputStream outputStream = mainActivity.getBluetooth().getBluetoothSocket().getOutputStream();
            byte[] command = ByteBuffer.allocate(4).putInt(Commands.START_RECORD.getCode()).array();
            outputStream.write(command);
        } catch (IOException e) {
            Log.e(MainActivity.LOG_TAG, "onClick, 29: Could not get bluetooth socket output stream.", e);
            e.printStackTrace();
        }
    }
}
