package org.marceloleite.projetoanna;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.AudioRecordActivityInterface;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.mixer.MixerAsyncTask;
import org.marceloleite.projetoanna.mixer.MixerAsyncTaskParameters;
import org.marceloleite.projetoanna.ui.ButtonConnectOnClickListener;
import org.marceloleite.projetoanna.ui.ButtonRecordOnClickListener;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;

import java.io.File;

/**
 * The application main activity.
 */
public class MainActivity extends AppCompatActivity implements AudioRecordActivityInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * The button to connect and disconnect from audio recorder.
     */
    private Button buttonConnect;

    /**
     * The button to start and stop audio record.
     */
    private Button buttonRecord;

    private TextureView textureView;

    /**
     * The audio recorder controller.
     */
    private AudioRecorder audioRecorder;

    private VideoRecorder videoRecorder;

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }

    public VideoRecorder getVideoRecorder() {
        return videoRecorder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileUtils.setContext(this);

        audioRecorder = new AudioRecorder(this);

        textureView = (TextureView) findViewById(R.id.texture_view_camera_preview);

        videoRecorder = new VideoRecorder(this, textureView);

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this.audioRecorder));

        buttonRecord = (Button) findViewById(R.id.button_record);

        buttonRecord.setOnClickListener(new ButtonRecordOnClickListener(this));

        updateInterface();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecorder != null) {
            audioRecorder.connectionLost();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Bluetooth.ENABLE_BLUETOOTH_REQUEST_CODE) {
            audioRecorder.enableBluetoothResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    /**
     * Updates the activity interface.
     */
    public void updateInterface() {
        updateButtonConnectInterface();
        updateButtonRecordInterface();
    }

    @Override
    public void receiveLatestAudioFileConcluded() {
        requestAudioAndVideoMix();
    }

    /**
     * Updates the connect button interface.
     */
    private void updateButtonConnectInterface() {
        if (audioRecorder.isConnected()) {
            buttonConnect.setText(R.string.button_connect_second_text);
        } else {
            buttonConnect.setText(R.string.button_connect_first_text);
        }
        buttonConnect.setEnabled(true);
    }

    /**
     * Updates the record button interface.
     */
    private void updateButtonRecordInterface() {
        if (audioRecorder.isConnected()) {
            if (audioRecorder.isRecording() && videoRecorder.isRecording()) {
                buttonRecord.setText(R.string.button_record_second_text);
            } else {
                buttonRecord.setText(R.string.button_record_first_text);
            }
            buttonRecord.setEnabled(true);
        } else {
            buttonRecord.setEnabled(false);
        }
    }

    private void requestAudioAndVideoMix() {
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 142: Requesting audio and video mix.");
        File audioFile = audioRecorder.getLatestAudioFile();
        File movieFile = videoRecorder.getVideoFile();
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 145: Audio file: " + audioFile);
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 146: Video file: " + movieFile);

        if (audioFile != null && movieFile != null) {
            MixerAsyncTask mixerAsyncTask = new MixerAsyncTask(this);
            MixerAsyncTaskParameters mixerAsyncTaskParameters = new MixerAsyncTaskParameters(audioFile, movieFile);
            mixerAsyncTask.execute(mixerAsyncTaskParameters);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume, 154: OnResume");

        if (textureView != null) {
            Size textureViewSize = new Size(textureView.getWidth(), textureView.getHeight());
            videoRecorder.resume(textureViewSize);
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause, 160: OnPause");
        if (videoRecorder != null) {
            videoRecorder.pause();
        }
        super.onPause();
    }

    public void mixConcluded(File file) {
        Toast.makeText(this, "Movie saved on " + file.getAbsolutePath() + ".", Toast.LENGTH_LONG).show();
        updateInterface();
    }
}
