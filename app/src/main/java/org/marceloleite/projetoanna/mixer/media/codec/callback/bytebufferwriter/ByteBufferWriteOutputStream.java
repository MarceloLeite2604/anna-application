package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import org.marceloleite.projetoanna.utils.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 * Created by marcelo on 23/05/17.
 */

public class ByteBufferWriteOutputStream extends ByteBufferWriter {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ByteBufferWriteOutputStream.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(ByteBufferWriteOutputStream.class);
    }

    private OutputStream outputStream;

    private long bytesToIgnore;

    private long bytesToWrite;

    public ByteBufferWriteOutputStream(OutputStream outputStream, long bytesToIgnore, long bytesToWrite) {
        super();
        this.outputStream = outputStream;
        this.bytesToIgnore = bytesToIgnore;
        this.bytesToWrite = bytesToWrite;
        Log.d(ByteBufferWriteOutputStream.class, LOG_TAG, "ByteBufferWriteOutputStream (39): Bytes do ignore: " + bytesToIgnore + ", bytes to write: " + bytesToWrite);
    }

    public ByteBufferWriteOutputStream(OutputStream outputStream, int maximumItemsOnMap, long bytesToIgnore, long bytesToWrite) {
        super(maximumItemsOnMap);
        this.bytesToIgnore = bytesToIgnore;
        this.bytesToWrite = bytesToWrite;
        this.outputStream = outputStream;
    }

    @Override
    protected boolean writeFirstItem() {
        Long firstKey;
        try {
            firstKey = audioDataTreeMap.firstKey();
        } catch (NoSuchElementException e) {
            firstKey = null;
        }

        if (firstKey != null) {
            AudioData firstAudioData = audioDataTreeMap.get(firstKey);

            ByteBuffer byteBuffer = firstAudioData.getByteBuffer();
            writeOnOutput(byteBuffer);
            audioDataTreeMap.remove(firstKey);
            return true;
        } else {
            return false;
        }
    }

    private void writeOnOutput(ByteBuffer byteBuffer) {

        int bytesToRead;
        int byteBufferSize = byteBuffer.limit();
        int remainingBytesIgnored;

        if (bytesToIgnore <= byteBufferSize) {

            remainingBytesIgnored = (int) bytesToIgnore;

            byte[] buffer = new byte[byteBufferSize];

            if (remainingBytesIgnored > 0) {
                byteBuffer.get(buffer, 0, remainingBytesIgnored);
            }

            bytesToRead = (int) (byteBufferSize - bytesToIgnore);

            if (bytesToWrite <= bytesToRead) {
                bytesToRead = (int) bytesToWrite;
            }
            byteBuffer.get(buffer, 0, bytesToRead);

            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(ByteBufferWriteOutputStream.class, LOG_TAG, "writeOnOutput (96): Error while writing content on output stream.", e);
            }

            bytesToIgnore -= remainingBytesIgnored;
            bytesToWrite -= bytesToRead;
        } else {
            remainingBytesIgnored = byteBufferSize;
            bytesToIgnore -= remainingBytesIgnored;
        }
    }

    @Override
    protected void postConcludeWriting() {
        try {
            outputStream.close();
        } catch (IOException e) {
            Log.e(ByteBufferWriteOutputStream.class, LOG_TAG, "postConcludeWriting (112): Error while closing output stream.", e);
        }
    }
}