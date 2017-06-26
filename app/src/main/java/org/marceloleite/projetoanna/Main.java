package org.marceloleite.projetoanna;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorderInterface;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorderParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnector;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorInterface;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorParameters;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorResult;
import org.marceloleite.projetoanna.audiorecorder.bluetoothconnector.BluetoothConnectorReturnCodes;
import org.marceloleite.projetoanna.mixer.MixerAsyncTask;
import org.marceloleite.projetoanna.mixer.MixerAsyncTaskParameters;
import org.marceloleite.projetoanna.ui.listeners.buttonconnect.ButtonConnectOnClickListener;
import org.marceloleite.projetoanna.ui.listeners.buttonrecord.ButtonRecordInterface;
import org.marceloleite.projetoanna.ui.listeners.buttonrecord.ButtonRecordOnClickListener;
import org.marceloleite.projetoanna.ui.listeners.buttonconnect.ButtonConnectInterface;
import org.marceloleite.projetoanna.utils.GenericReturnCodes;
import org.marceloleite.projetoanna.utils.Log;
import org.marceloleite.projetoanna.videorecorder.VideoRecorder;
import org.marceloleite.projetoanna.videorecorder.VideoRecorderInterface;

import java.io.File;
import java.io.IOException;

import static org.marceloleite.projetoanna.utils.Log.addClassToLog;

/**
 * The application main activity.
 */
public class Main extends AppCompatActivity implements ButtonConnectInterface, ButtonRecordInterface, BluetoothConnectorInterface, AudioRecorderInterface, VideoRecorderInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = Main.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        addClassToLog(LOG_TAG);
    }

    /**
     * The button to startConnectionProcess and disconnect from audio recorder.
     */
    private Button buttonConnect;

    /**
     * start and stop audio record.
     */
    private Button buttonRecord;

    private TextureView textureView;

    private BluetoothConnector bluetoothConnector;

    /**
     * The audio recorder controller.
     */
    private AudioRecorder audioRecorder;

    private VideoRecorder videoRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            bluetoothConnector = new BluetoothConnector(this);
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "This device does not have a bluetooth adapter.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        textureView = (TextureView) findViewById(R.id.texture_view_camera_preview);

        videoRecorder = new VideoRecorder(this, textureView);

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this));

        buttonRecord = (Button) findViewById(R.id.button_record);

        buttonRecord.setOnClickListener(new ButtonRecordOnClickListener(this));

        updateInterface();
        // testMix();
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
        if (requestCode == BluetoothConnector.ENABLE_BLUETOOTH_REQUEST_CODE) {
            bluetoothConnector.requestBluetoothAdapterActivationResult(resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean isConnected() {
        return (audioRecorder != null && audioRecorder.isConnected());
    }

    @Override
    public void connectWithAudioRecorder() {
        bluetoothConnector.startConnectionProcess();
    }

    public void connectWithAudioRecorderResult(int result) {
        String audioRecorderDeviceName;
        switch (result) {
            case BluetoothConnectorReturnCodes.SUCCESS:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Connected with \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothConnectorReturnCodes.GENERIC_ERROR:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Could not connect with \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_LONG).show();
                break;
            case BluetoothConnectorReturnCodes.CONNECTION_CANCELLED:
                break;
            default:
                Log.e(LOG_TAG, "bluetoothConnectionResult (137): Unknown result received from \"startConnectionProcess\" method.");
                break;
        }
        updateInterface();
    }


    @Override
    public void disconnectFromAudioRecorder() {
        audioRecorder.disconnect();
    }

    public void disconnectFromAudioRecorderResult(int result) {
        String audioRecorderDeviceName;
        switch (result) {
            case BluetoothConnectorReturnCodes.SUCCESS:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Disconnected from \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_SHORT).show();
                break;
            case BluetoothConnectorReturnCodes.GENERIC_ERROR:
                audioRecorderDeviceName = audioRecorder.getAudioRecorderDeviceName();
                Toast.makeText(this, "Error while disconnecting from \"" + audioRecorderDeviceName + "\".", Toast.LENGTH_LONG).show();
                break;
            case BluetoothConnectorReturnCodes.CONNECTION_CANCELLED:
                break;
            default:
                Log.e(LOG_TAG, "disconnectFromAudioRecorderResult (163): Unknown result received from \"disconnectFromhAudioRecorder\" method.");
                break;
        }
        updateInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume (172): ");

        if (textureView != null) {
            Size textureViewSize = new Size(textureView.getWidth(), textureView.getHeight());
            videoRecorder.resume(textureViewSize);
        }
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause (182): ");
        if (videoRecorder != null) {
            videoRecorder.pause();
        }
        super.onPause();
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return this;
    }

    @Override
    public boolean isRecording() {
        return (audioRecorder.isRecording() && videoRecorder.isRecording());
    }

    @Override
    public void startRecording() {
        Log.d(LOG_TAG, "startRecording (195): ");
        audioRecorder.startAudioRecording();
    }

    @Override
    public void startAudioRecordingResult(int result) {
        Log.d(LOG_TAG, "startAudioRecordingResult (201): ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                videoRecorder.startRecord();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not start audio recorder.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "startAudioRecordingResult (210): Unknown return code received from \"start record\" command.");
                break;
        }
        updateInterface();
    }

    @Override
    public void startVideoRecordingResult(int result) {
        Log.d(LOG_TAG, "startVideoRecordingResult (218): ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Toast.makeText(this, "Recording.", Toast.LENGTH_LONG).show();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not start video recording.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "startVideoRecordingResult (227): Unknown code returned from start video recording command: " + result);
                Toast.makeText(this, "Could not start video recorder.", Toast.LENGTH_LONG).show();
                break;
        }
        updateInterface();
    }

    @Override
    public void finishRecording() {
        Log.d(LOG_TAG, "stopRecording (235): ");
        videoRecorder.stopRecord();
    }

    @Override
    public void stopVideoRecordingResult(int result) {
        Log.d(LOG_TAG, "stopVideoRecordingResult (241): ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "stopVideoRecordingResult (244): Video recording stopped");
                audioRecorder.stopRecord();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not stop video recorder.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "stopVideoRecordingResult (251): Unknown code returned from stop video recording command: " + result);
                Toast.makeText(this, "Could not stop video recorder.", Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public void stopAudioRecordingResult(int result) {
        Log.d(LOG_TAG, "stopAudioRecordingResult (260): ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "stopAudioRecordingResult (263): Audio record stopped.");
                audioRecorder.requestLatestAudioFile();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Toast.makeText(this, "Could not stop audio recording.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "stopAudioRecordingResult (270): Unknown return code received from \"stop record\" command.");
                break;
        }
        updateInterface();
    }

    @Override
    public void requestLatestAudioFileResult(int result) {
        Log.d(LOG_TAG, "requestLatestAudioFileResult (278): ");
        switch (result) {
            case GenericReturnCodes.SUCCESS:
                Log.d(LOG_TAG, "requestLatestAudioFileResult (282): Received latest audio file succesfully.");
                requestAudioAndVideoMix();
                break;
            case GenericReturnCodes.GENERIC_ERROR:
                Log.e(LOG_TAG, "requestLatestAudioFileResult (285): Error while receiving latest audio file.");
                Toast.makeText(this, "Error while receiving latest audio file.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(LOG_TAG, "requestLatestAudioFileResult (289): Unknown result received from \"request latest audio file\" command.");
                Toast.makeText(this, "Error while receiving latest audio file.", Toast.LENGTH_LONG).show();
                break;
        }
        updateInterface();
    }

    private void requestAudioAndVideoMix() {
        Log.d(LOG_TAG, "requestAudioAndVideoMix (297): Requesting audio and video mix.");
        File audioFile = audioRecorder.getLatestAudioFile();
        File movieFile = videoRecorder.getVideoFile();

        long startAudioCommandDelay = audioRecorder.getStartCommandDelay();
        Log.d(LOG_TAG, "requestAudioAndVideoMix (302): Audio file: " + audioFile);
        Log.d(LOG_TAG, "requestAudioAndVideoMix (303): Video file: " + movieFile);
        Log.d(LOG_TAG, "requestAudioAndVideoMix (304): Audio recorder start command delay (us): " + startAudioCommandDelay);
        Log.d(LOG_TAG, "requestAudioAndVideoMix (305): Video recorder start command delay (us): " + videoRecorder.getStartRecordingDelay());

        startAudioCommandDelay += videoRecorder.getStartRecordingDelay();

        if (audioFile != null && movieFile != null) {

            MixerAsyncTask mixerAsyncTask = new MixerAsyncTask(this);
            MixerAsyncTaskParameters mixerAsyncTaskParameters = new MixerAsyncTaskParameters(audioFile, movieFile, startAudioCommandDelay);
            mixerAsyncTask.execute(mixerAsyncTaskParameters);
        }
    }

    private void testMix() {
        File audioFile = new File("/storage/emulated/0/Music/org.marceloleite.projetoanna/20170523_120021.mp3");
        File movieFile = new File("/storage/emulated/0/Movies/org.marceloleite.projetoanna/20170523_115855.mp4");

        //long startAudioCommandDelay = audioRecorder.getStartCommandDelay();
        long startAudioCommandDelay = 560000;

        Log.d(LOG_TAG, "testMix (324): Audio file: " + audioFile);
        Log.d(LOG_TAG, "testMix (325): Video file: " + movieFile);
        Log.d(LOG_TAG, "testMix (326): Audio recorder start command delay (us): " + startAudioCommandDelay);

        //startAudioCommandDelay += videoRecorder.getStartRecordingDelay();
        startAudioCommandDelay += 40000;

        if (audioFile != null && movieFile != null) {

            MixerAsyncTask mixerAsyncTask = new MixerAsyncTask(this);
            MixerAsyncTaskParameters mixerAsyncTaskParameters = new MixerAsyncTaskParameters(audioFile, movieFile, startAudioCommandDelay);
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
     * Updates the startConnectionProcess button interface.
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

    @Override
    public BluetoothConnectorParameters getBluetoothConnectionParameters() {
        return new BluetoothConnectorParameters(this);
    }

    @Override
    public void bluetoothConnectionResult(BluetoothConnectorResult bluetoothConnectorResult) {

    }
}