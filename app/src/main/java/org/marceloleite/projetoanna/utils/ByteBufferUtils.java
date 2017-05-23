package org.marceloleite.projetoanna.utils;

import java.nio.ByteBuffer;

/**
 * Created by marcelo on 23/05/17.
 */

public class ByteBufferUtils {

    public static ByteBuffer copyByteBuffer(ByteBuffer byteBuffer) {
        ByteBuffer copyByteBuffer = ByteBuffer.allocate(byteBuffer.capacity());
        byteBuffer.rewind();
        copyByteBuffer.put(byteBuffer);
        byteBuffer.rewind();
        copyByteBuffer.flip();
        return copyByteBuffer;
    }
}
