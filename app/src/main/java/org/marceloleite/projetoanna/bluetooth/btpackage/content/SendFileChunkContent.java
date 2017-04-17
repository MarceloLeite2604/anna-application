package org.marceloleite.projetoanna.bluetooth.btpackage.content;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class SendFileChunkContent extends Content {

    private static final int CONTENT_HEADER = 0x43ece390;

    private int chunkSize;

    byte[] chunkData;

    public SendFileChunkContent(int chunkSize, byte[] chunkData) {
        this.chunkSize = chunkSize;
        this.chunkData = chunkData;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public void setChunkData(byte[] chunkData) {
        this.chunkData = chunkData;
    }
}
