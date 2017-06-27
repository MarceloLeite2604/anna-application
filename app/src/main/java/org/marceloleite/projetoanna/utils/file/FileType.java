package org.marceloleite.projetoanna.utils.file;

/**
 * Enumerates the type of files the application works with.
 */
public enum FileType {
    TEMPORARY_FILE("tmp", "Temporary file"),
    MOVIE_FILE("mp4", "Movie file"),
    AUDIO_AAC_FILE("aac", "AAC audio file"),
    AUDIO_MP3_FILE("mp3", "MP3 audio file"),
    AUDIO_RAW_FILE("raw", "Raw audio file"),
    VIDEO_FILE("h264", "Video file");

    /**
     * Description of the file type.
     */
    private final String description;

    /**
     * The extension used to identify the file type.
     */
    private final String fileExtension;

    /**
     * Creates a new {@link FileType} enumeration.
     *
     * @param fileExtension The extension used to identify the file type.
     * @param description   The description of the file type.
     */
    FileType(String fileExtension, String description) {
        this.fileExtension = fileExtension;
        this.description = description;
    }

    /**
     * Returns the extension used to identify the file type.
     *
     * @return The extension used to identify the file type.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String toString() {
        return description;
    }
}
