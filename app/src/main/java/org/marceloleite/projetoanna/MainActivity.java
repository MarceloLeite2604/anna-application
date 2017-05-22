package org.marceloleite.projetoanna;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorderActivityInterface;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.BluetoothConnectReturnCodes;
import org.marceloleite.projetoanna.mixer.MixerAsyncTask;
import org.marceloleite.projetoanna.mixer.MixerAsyncTaskParameters;
import org.marceloleite.projetoanna.ui.ButtonConnectOnClickListener;
import org.marceloleite.projetoanna.ui.ButtonRecordOnClickListener;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.chronometer.Chronometer;
import org.marceloleite.projetoanna.utils.file.FileUtils;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;
import org.marceloleite.projetoanna.videorecorder.VideoRecorderActivityInterface;

import java.io.File;

/**
 * The application main activity.
 */
public class MainActivity extends AppCompatActivity implements AudioRecorderActivityInterface, VideoRecorderActivityInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * The button to connectWithAudioRecorder and disconnect from audio recorder.
     */
    private Button buttonConnect;

    /**
     * start and stop audio record.
     */
    private Button buttonRecord;

    private TextureView textureView;

    /**
     * The audio recorder controller.
     */
    private AudioRecorder audioRecorder;

    private VideoRecorder videoRecorder;

    private Chronometer startVideoRecodingChronometer;

    private Chronometer stopVideoRecordingChronometer;

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

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this));

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
            audioRecorder.enableBluetoothActivityResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    public void startConnectionWithAudioRecorder() {
        audioRecorder.connectWithAudioRecorder();
    }

    @Override
    public void connectWithAudioRecorderResult(int result) {
        String audioRecorderDeviceName;
        switch (result) {
            case BluetoothConnectReturnCodes.SUCCESS:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Connected with \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothConnectReturnCodes.GENERIC_ERROR:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Could not connect with \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_LONG).show();
                break;
            case BluetoothConnectReturnCodes.CONNECTION_CANCELLED:
                break;
            default:
                Log.e(LOG_TAG, "connectWithAudioRecorderResult, 132: Unknown result received from \"connectWithAudioRecorder\" method.");
                break;
        }
        updateInterface();
    }


    public void startDisconnectionFromAudioRecorder() {
        audioRecorder.disconnectFromAudioRecorder();
    }

    @Override
    public void disconnectFromAudioRecorderResult(int result) {
        String audioRecorderDeviceName;
        switch (result) {
            case BluetoothConnectReturnCodes.SUCCESS:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Disconnected from \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothConnectReturnCodes.GENERIC_ERROR:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Error while disconnecting from \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_LONG).show();
                break;
            case BluetoothConnectReturnCodes.CONNECTION_CANCELLED:
                break;
            default:
                Log.e(LOG_TAG, "connectWithAudioRecorderResult, 132: Unknown result received from \"disconnectFromhAudioRecorder\" method.");
                break;
        }
        updateInterface();
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

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return this;
    }

    public void startRecording() {
        Log.d(LOG_TAG, "startRecording, 306: Initializing start record process.");
        audioRecorder.startAudioRecording();
    }

    @Override
    public void startAudioRecordingResult(int result) {
        Log.d(LOG_TAG, "startAudioRecordingResult, 122: ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                startVideoRecodingChronometer = new Chronometer();
                startVideoRecodingChronometer.start();
                videoRecorder.startRecord();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not start audio recorder.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "startAudioRecordingResult, 135: Unknown return code received from \"start record\" command.");
                break;
        }
        updateInterface();
    }

    @Override
    public void startVideoRecordingResult(int result) {
        Log.d(LOG_TAG, "startVideoRecordingResult, 255: ");
        startVideoRecodingChronometer.stop();
        Log.d(LOG_TAG, "startVideoRecordingResult, 237: Start video recording time: " + startVideoRecodingChronometer.getDifference());
        switch (result) {
            case GenericReturnCodes.SUCCESS:

                Toast.makeText(this, "Recording.", Toast.LENGTH_LONG).show();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not start video recording.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "stopVideoRecordingResult, 270: Unknown code returned from start video recording command: " + result);
                Toast.makeText(this, "Could not start video recorder.", Toast.LENGTH_LONG).show();
                break;
        }
        updateInterface();
    }

    public void stopRecording() {
        Log.d(LOG_TAG, "stopRecording, 311: ");
        stopVideoRecordingChronometer = new Chronometer();
        stopVideoRecordingChronometer.start();
        videoRecorder.stopRecord();
    }

    @Override
    public void stopVideoRecordingResult(int result) {
        Log.d(LOG_TAG, "stopVideoRecordingResult, 274: ");
        stopVideoRecordingChronometer.stop();
        Log.d(LOG_TAG, "stopVideoRecordingResult, 261: Stop video time: " + stopVideoRecordingChronometer.getDifference());
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "stopVideoRecordingResult, 302: Video recording stopped.");
                audioRecorder.stopRecord();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not stop video recorder.", Toast.LENGTH_LONG);
                break;
            default:
                Log.e(LOG_TAG, "stopVideoRecordingResult, 270: Unknown code returned from stop video recording command: " + result);
                Toast.makeText(this, "Could not stop video recorder.", Toast.LENGTH_LONG);
                break;
        }

    }

    @Override
    public void stopAudioRecordingResult(int result) {
        Log.d(LOG_TAG, "stopAudioRecordingResult, 271: ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "stopAudioRecordingResult, 304: Audio record stopped.");
                audioRecorder.requestLatestAudioFile();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not stop audio recording.", Toast.LENGTH_LONG);
                break;
            default:
                Log.e(LOG_TAG, "startAudioRecordingResult, 135: Unknown return code received from \"stop record\" command.");
                break;
        }
        updateInterface();
    }

    @Override
    public void requestLatestAudioFileResult(int result) {
        Log.d(LOG_TAG, "requestLatestAudioFileResult, 288: ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "requestLatestAudioFileResult, 292: Received latest audio file succesfully.");
                requestAudioAndVideoMix();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Log.e(LOG_TAG, "requestLatestAudioFileResult, 171: Error while receiving latest audio file.");
                Toast.makeText(this, "Error while receiving latest audio file.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "requestLatestAudioFileResult, 175: Unknown result received from \"request latest audio file\" command.");
                Toast.makeText(this, "Error while receiving latest audio file.", Toast.LENGTH_LONG).show();
                break;
        }
        updateInterface();
    }

    private void requestAudioAndVideoMix() {
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 142: Requesting audio and video mix.");
        File audioFile = audioRecorder.getLatestAudioFile();
        File movieFile = videoRecorder.getVideoFile();

        long startAudioDelay = audioRecorder.getStartCommandDelay() + (startVideoRecodingChronometer.getDifference() / 1000l);

        long cuttedDelay = 1000000L;
        if (startAudioDelay > cuttedDelay) {
            startAudioDelay -= cuttedDelay;
        }

        long stopAudioDelay = audioRecorder.getStopCommandDelay() + (stopVideoRecordingChronometer.getDifference() / 1000l);

        Toast.makeText(this, "Stop delay: " + (float) stopAudioDelay / 1000000f, Toast.LENGTH_LONG).show();

        stopAudioDelay += cuttedDelay;

        Log.d(LOG_TAG, "requestAudioAndVideoMix, 327: Audio file: " + audioFile);
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 328: Video file: " + movieFile);
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 329: Audio recorder start command delay (us): " + audioRecorder.getStartCommandDelay());
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 330: Video recorder start command delay (us): " + (startVideoRecodingChronometer.getDifference() / 1000l));
        Log.d(LOG_TAG, "requestLatestAudioFileResult, 331: Total start audio delay (us): " + startAudioDelay);
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 332: Audio recorder stop command delay (us): " + audioRecorder.getStopCommandDelay());
        Log.d(LOG_TAG, "requestAudioAndVideoMix, 333: Video recorder stop command delay (us): " + (stopVideoRecordingChronometer.getDifference() / 1000l));
        Log.d(LOG_TAG, "requestLatestAudioFileResult, 334: Total stop audio delay (us): " + stopAudioDelay);

        if (audioFile != null && movieFile != null) {

            MixerAsyncTask mixerAsyncTask = new MixerAsyncTask(this);
            MixerAsyncTaskParameters mixerAsyncTaskParameters = new MixerAsyncTaskParameters(audioFile, movieFile, startAudioDelay, stopAudioDelay);
            mixerAsyncTask.execute(mixerAsyncTaskParameters);
        }
    }

    public void mixConcluded(File file) {
        Toast.makeText(this, "Movie saved on " + file.getAbsolutePath() + ".", Toast.LENGTH_LONG).show();
        updateInterface();
    }

    /**
     * Updates the activity interface.
     */
    private void updateInterface() {
        updateButtonConnectInterface();
        updateButtonRecordInterface();
    }

    /**
     * Updates the connectWithAudioRecorder button interface.
     */
    private void updateButtonConnectInterface() {
        if (audioRecorder != null) {
            if (audioRecorder.isConnected()) {
                buttonConnect.setText(R.string.button_connect_second_text);
            } else {
                buttonConnect.setText(R.string.button_connect_first_text);
            }
            buttonConnect.setEnabled(true);
        }
    }

    /**
     * Updates the record button interface.
     */
    private void updateButtonRecordInterface() {
        if (audioRecorder != null) {
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
    }
}
