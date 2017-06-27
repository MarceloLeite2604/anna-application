package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

/**
 * A piece of audio data and its information.
 */
public class AudioData {

    /**
     * The audio data.
     */
    private final ByteBuffer byteBuffer;

    /**
     * The information about the audio data.
     */
    private final MediaCodec.BufferInfo bufferInfo;

    /**
     * Constructor.
     *
     * @param byteBuffer The audio data.
     * @param bufferInfo The information about the audio data.
     */
    public AudioData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        this.byteBuffer = byteBuffer;
        this.bufferInfo = bufferInfo;
    }

    /**
     * Returns the audio data.
     *
     * @return the audio data.
     */
    ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    /**
     * Returns the information about the audio data.
     *
     * @return The information about the audio data.
     */
    MediaCodec.BufferInfo getBufferInfo() {
        return bufferInfo;
    }
}
