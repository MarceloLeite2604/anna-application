package org.marceloleite.projetoanna.utils.file;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public enum FileType {
    TEMPORARY_FILE("tmp", "Temporary file"),
    MOVIE_FILE("mp4", "Movie file"),
    AUDIO_FILE("mp3", "Audio file"),
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
