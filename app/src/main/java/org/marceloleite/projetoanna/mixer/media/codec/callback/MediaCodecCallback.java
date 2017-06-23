package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public abstract class MediaCodecCallback extends MediaCodec.Callback {

    /**
     * Copies a {@link ByteBuffer} object.
     *
     * @param byteBuffer The {@link ByteBuffer} object to be copied.
     * @return The copy of the {@link ByteBuffer} object.
     */
    public static ByteBuffer copyByteBuffer(ByteBuffer byteBuffer) {
        ByteBuffer copyByteBuffer = ByteBuffer.allocate(byteBuffer.capacity());
        byteBuffer.rewind();
        copyByteBuffer.put(byteBuffer);
        byteBuffer.rewind();
        copyByteBuffer.flip();
        return copyByteBuffer;
    }

    /**
     * Informs the codec has finished its operation.
     *
     * @return True if the operation has finished, false otherwise.
     */
    public abstract boolean finished();
}
