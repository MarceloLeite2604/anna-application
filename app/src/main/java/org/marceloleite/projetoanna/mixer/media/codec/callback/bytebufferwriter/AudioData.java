package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import android.media.MediaCodec;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;

/**
 * Created by marcelo on 23/05/17.
 */

public class AudioData {

    private ByteBuffer byteBuffer;

    private MediaCodec.BufferInfo bufferInfo;

    public AudioData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        this.byteBuffer = byteBuffer;
        this.bufferInfo = bufferInfo;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public MediaCodec.BufferInfo getBufferInfo() {
        return bufferInfo;
    }
}
