package org.marceloleite.projetoanna.bluetooth.btpackage;

/**
 * Created by marcelo on 18/03/17.
 */

public enum TypeCode {

    CHECK_CONNECTION(0xd4482fd1, "CHECK_CONNECTION"),
    CONFIRMATION(0xede91e4b, "CONFIRMATION"),
    COMMAND_RESULT(0xf1614798, "COMMAND_RESULT"),
    DISCONNECT(0x8cfb04b7, "DISCONNECT"),
    ERROR(0x5a9fc089, "ERROR"),
    REQUEST_AUDIO_FILE(0x9b7ba242, "REQUEST_AUDIO_FILE"),
    SEND_FILE_CHUNK(0x9f760f0f, "SEND_FILE_CHUNK"),
    SEND_FILE_HEADER(0xd466f934, "SEND_FILE_HEADER"),
    SEND_FILE_TRAILER(0x1ce1618c, "SEND_FILE_TRAILER"),
    START_RECORD(0x74bbd211, "START_RECORD"),
    STOP_RECORD(0xe5d1f6a1, "STOP_RECORD");

    private int code;

    private String description;

    TypeCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TypeCode getTypeCode(int code) {
        for (TypeCode typeCode :
                TypeCode.values()) {
            if (typeCode.getCode() == code) {
                return typeCode;
            }
        }
        throw new IllegalArgumentException("Value \"" + Integer.toHexString(code) + "\" does not match any type code.");
    }
}
