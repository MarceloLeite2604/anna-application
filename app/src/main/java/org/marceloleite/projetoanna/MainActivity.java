package org.marceloleite.projetoanna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.ui.ButtonConnectOnClickListener;
import org.marceloleite.projetoanna.ui.ButtonRecordOnClickListener;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Button buttonConnect;

    private Button buttonRecord;

    private AudioRecorder audioRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioRecorder = new AudioRecorder(this);

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this.audioRecorder));

        buttonRecord = (Button) findViewById(R.id.button_record);

        buttonRecord.setOnClickListener(new ButtonRecordOnClickListener(this.audioRecorder));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE) {
            audioRecorder.enableBluetoothResult(resultCode);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }

    public void updateInterface() {
        updateButtonConnectInterface();
        updateButtonRecordInterface();
    }

    private void updateButtonConnectInterface() {
        if (audioRecorder.isConnected()) {
            buttonConnect.setText(R.string.button_connect_second_text);
        } else {
            buttonConnect.setText(R.string.button_connect_first_text);
        }
        buttonConnect.setEnabled(true);
    }

    private void updateButtonRecordInterface() {
        if (audioRecorder.isConnected()) {
            if (audioRecorder.isRecording()) {
                buttonRecord.setText(R.string.button_record_second_text);
            } else {
                buttonRecord.setText(R.string.button_record_first_text);
            }
            buttonRecord.setEnabled(true);
        } else {
            buttonRecord.setEnabled(false);
        }
    }

}
