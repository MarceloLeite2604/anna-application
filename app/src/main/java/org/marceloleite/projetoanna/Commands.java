package org.marceloleite.projetoanna;

/**
 * Created by marcelo on 18/03/17.
 */

public enum Commands {

    START_RECORD(0x05577306, "START_RECORD"),
    STOP_RECORD(0x27fb0474, "STOP_RECORD"),
    SEND_AUDIO_DATA(0xd7137bb6, "SEND_AUDIO_DATA"),
    AUDIO_DATA_RECEIVED(0x2fa8a231, "AUDIO_DATA_RECEIVED"),
    SEND_AUDIO_INFO(0x6235b8ee, "SEND_AUDIO_INFO"),
    AUDIO_INFO_RECEIVED(0xddef1fb2, "AUDIO_INFO_RECEIVED"),
    STATUS_RECORDING(0x72013ab7, "STATUS_RECORDING"),
    STATUS_IDLE(0xce3c7b97, "STATUS_IDLE"),
    SEND_STATUS(0xbddf5441, "SEND_STATUS"),
    EXECUTION_SUCCESS(0x299972ff, "EXECUTION_SUCCESS"),
    EXECUTION_FAILURE(0x30f1bcc9, "EXECUTION_FAILURE"),
    RESET_DEVICE(0xbf2b9534, "RESET_DEVICE"),
    SHUTDOWN_DEVICE(0xe60850d4, "SHUTDOWN_DEVICE");

    private int code;

    private String command;

    Commands(int code, String command) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getCommand() {
        return command;
    }
}
