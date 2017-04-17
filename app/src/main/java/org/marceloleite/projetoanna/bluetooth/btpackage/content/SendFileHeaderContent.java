package org.marceloleite.projetoanna.bluetooth.btpackage.content;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class SendFileHeaderContent extends Content {

    private static final int CONTENT_HEADER = 0x47b24e67;

    private int fileSize;

    private String fileName;

    public SendFileHeaderContent(int fileSize, String fileName) {
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
