package org.marceloleite.projetoanna;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.marceloleite.projetoanna.audiorecorder.AudioRecordActivityInterface;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.bluetooth.Bluetooth;
import org.marceloleite.projetoanna.camera.CameraController;
import org.marceloleite.projetoanna.camera.CameraPreview;
import org.marceloleite.projetoanna.ui.ButtonConnectOnClickListener;
import org.marceloleite.projetoanna.ui.ButtonRecordOnClickListener;

/**
 * The application main activity.
 */
public class MainActivity extends AppCompatActivity implements AudioRecordActivityInterface {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Controls the bluetooth activation, pairing and connection.
     */
    private Bluetooth bluetooth;

    /**
     * The button to connect and disconnect from audio recorder.
     */
    private Button buttonConnect;

    /**
     * The button to start and stop audio record.
     */
    private Button buttonRecord;

    /**
     * The audio recorder controller.
     */
    private AudioRecorder audioRecorder = null;

    private CameraController cameraController;

    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioRecorder = new AudioRecorder(this);

        buttonConnect = (Button) findViewById(R.id.button_connect);

        buttonConnect.setOnClickListener(new ButtonConnectOnClickListener(this.audioRecorder));

        buttonRecord = (Button) findViewById(R.id.button_record);

        cameraController = new CameraController();

        // Create our Preview view and set it as the content of our activity.
        cameraPreview = new CameraPreview(this, cameraController.getCamera());

        cameraController.setCameraPreview(cameraPreview);

        LinearLayout linearLayoutCameraPreview = (LinearLayout) findViewById(R.id.linear_layout_camera_preview);
        ViewGroup.LayoutParams layoutParams = linearLayoutCameraPreview.getLayoutParams();
        linearLayoutCameraPreview.addView(cameraPreview, layoutParams);

        buttonRecord.setOnClickListener(new ButtonRecordOnClickListener(audioRecorder, cameraController));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioRecorder.connectionLost();
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
