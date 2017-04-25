package org.marceloleite.projetoanna.audioRecorder.datapackage;

/**
 * Identifies the type of a package.
 */
public enum PackageType {

    /**
     * Identifies a package to check the connection between the application and the recorder.
     */
    CHECK_CONNECTION(0xd4482fd1, "CHECK_CONNECTION"),

    /**
     * Identified a package to confirm a package delivery.
     */
    CONFIRMATION(0xede91e4b, "CONFIRMATION"),

    /**
     * Identifies a package returning the result of a command.
     */
    COMMAND_RESULT(0xf1614798, "COMMAND_RESULT"),

    /**
     * Identifies a package to announce the device is disconnecting.
     */
    DISCONNECT(0x8cfb04b7, "DISCONNECT"),

    /**
     * Identifies a package with an error message.
     */
    ERROR(0x5a9fc089, "ERROR"),

    /**
     * Identifies a package with a file chunk.
     */
    FILE_CHUNK(0x9f760f0f, "FILE_CHUNK"),

    /**
     * Identifies a package with a file header.
     */
    FILE_HEADER(0xd466f934, "FILE_HEADER"),

    /**
     * Identifies a package with a file trailer.
     */
    FILE_TRAILER(0x1ce1618c, "FILE_TRAILER"),

    /**
     * Identifies a package requesting the audio file.
     */
    REQUEST_AUDIO_FILE(0x9b7ba242, "REQUEST_AUDIO_FILE"),

    /**
     * Identifies a package requesting to start audio record.
     */
    START_RECORD(0x74bbd211, "START_RECORD"),

    /**
     * Identifies a package requesting to stop audio record.
     */
    STOP_RECORD(0xe5d1f6a1, "STOP_RECORD");

    /**
     * The code which identifies the package type.
     */
    private int code;

    /**
     * The title of the package type.
     */
    private String title;

    /**
     * Creates a new package type
     *
     * @param code  The code which identified the package type.
     * @param title The title of the package type.
     */
    PackageType(int code, String title) {
        this.code = code;
        this.title = title;
    }

    /**
     * Returns the package code.
     *
     * @return The package code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the package title.
     *
     * @return The package title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the package type according with the code informed.
     *
     * @param code The code to be checked.
     * @return The package type which matches the code informed.
     */
    public static PackageType getTypeCode(int code) {
        for (PackageType packageType :
                PackageType.values()) {
            if (packageType.getCode() == code) {
                return packageType;
            }
        }
        throw new IllegalArgumentException("Value \"" + Integer.toHexString(code) + "\" does not match any type code.");
    }

    @Override
    public String toString() {
        return title;
    }
}
