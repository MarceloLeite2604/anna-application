package org.marceloleite.projetoanna.utils.file;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public enum FileType {
    TEMPORARY_FILE("tmp", "Temporary file"),
    MOVIE_FILE("mp4", "Movie file"),
    AUDIO_AAC_FILE("aac", "AAC audio file"),
    AUDIO_MP3_FILE("mp3", "MP3 audio file"),
    AUDIO_RAW_FILE("raw", "Raw audio file"),
    VIDEO_FILE("h264", "Video file");

    private String title;

    private String fileExtension;

    FileType(String fileExtension, String title) {
        this.fileExtension = fileExtension;
        this.title = title;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String toString() {
        return title;
    }
}
