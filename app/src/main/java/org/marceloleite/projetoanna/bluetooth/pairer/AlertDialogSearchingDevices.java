package org.marceloleite.projetoanna.bluetooth.pairer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.marceloleite.projetoanna.InformationView;
import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.bluetooth.DeviceView;

/**
 * Created by Marcelo Leite on 30/04/2016.
 */
public class AlertDialogSearchingDevices extends AlertDialog implements View.OnClickListener, DialogInterface.OnClickListener {

    Pairer pairer;

    private LinearLayout linearLayoutDevices;
    private InformationView informationView;

    private AppCompatActivity appCompatActivity;

    public AlertDialogSearchingDevices(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
        this.appCompatActivity = appCompatActivity;
        this.pairer = pairer;

        setIcon(0);
        setTitle("Searching devices");
        setButton(BUTTON_NEGATIVE, "Cancel", this);
        setButton(AlertDialog.BUTTON_POSITIVE, "Search again", this);
        createViewDeviceListing();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    private void createViewDeviceListing() {
        linearLayoutDevices = new LinearLayout(appCompatActivity);
        linearLayoutDevices.setOrientation(LinearLayout.VERTICAL);

        informationView = new InformationView(getContext());
        linearLayoutDevices.addView(informationView);
        setView(linearLayoutDevices);
    }

    public void addBluetoothDeviceToList(BluetoothDevice bluetoothDevice) {
        DeviceView deviceView = new DeviceView(getContext(), bluetoothDevice);
        deviceView.setOnClickListener(this);
        linearLayoutDevices.addView(deviceView);
    }

    public void discoveryDevicesStarted() {
        if (!isShowing()) {
            show();
        }
        changeViewMode(ViewMode.SEARCH_STARTED);
    }

    public void discoveryDevicesFinished() {
        changeViewMode(ViewMode.SEARCH_FINISHED);
    }

    private void changeViewMode(ViewMode viewMode) {
        switch (viewMode) {
            case SEARCH_STARTED:
                setTitle("Searching devices");
                informationView.setInformationText("Searching devices...");
                informationView.showProgressBar(true);
                getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                break;
            case SEARCH_FINISHED:
                getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                setTitle("Search concluded");
                informationView.showProgressBar(false);
                String informationText;
                if (linearLayoutDevices.getChildCount() == 1) {
                    informationText = "No devices found.";
                } else {
                    informationText = "Select a device.";
                }
                informationView.setInformationText(informationText);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        DeviceView deviceView = (DeviceView)v;
        pairer.discoverDevicesConcluded(deviceView.getBluetoothDevice());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                pairer.startDeviceDiscovery();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                pairer.discoverDevicesConcluded(null);
                break;
            default:
                Log.e(MainActivity.LOG_TAG, "onClick, 110: Unknown button.");
                break;
        }
    }

    private enum ViewMode {
        SEARCH_STARTED,
        SEARCH_FINISHED
    }
}
