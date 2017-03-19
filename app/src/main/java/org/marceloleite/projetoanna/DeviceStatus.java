package org.marceloleite.projetoanna;

/**
 * Created by marcelo on 18/03/17.
 */

public enum DeviceStatus {

    IDLE(0xfabf4ce9, "IDLE"),
    RECORDING(0xff04406e, "RECORDING"),
    TRANSMITTING(0xf3d80066, "TRASMITTING"),
    DISCONNECTED(0xacf1cc6d, "DISCONNECTED");

    int code;

    String deviceStatusName;

    DeviceStatus(int code, String deviceStatusName) {
        this.code = code;
        this.deviceStatusName = deviceStatusName;
    }
}
