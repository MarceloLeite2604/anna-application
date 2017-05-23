package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import android.media.MediaCodec;
import android.media.MediaMuxer;
import android.util.Log;

import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 * Created by marcelo on 23/05/17.
 */

public class ByteBufferWriteMediaMuxerWrapper extends ByteBufferWriter {

    private static final String LOG_TAG = ByteBufferWriteMediaMuxerWrapper.class.getSimpleName();

    private MediaMuxerWrapper mediaMuxerWrapper;

    public ByteBufferWriteMediaMuxerWrapper(MediaMuxerWrapper mediaMuxerWrapper) {
        super();
        this.mediaMuxerWrapper = mediaMuxerWrapper;
    }

    public ByteBufferWriteMediaMuxerWrapper(MediaMuxerWrapper mediaMuxerWrapper, int maximumItemsOnMap) {
        super(maximumItemsOnMap);
        this.mediaMuxerWrapper = mediaMuxerWrapper;
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

            MediaCodec.BufferInfo bufferInfo = firstAudioData.getBufferInfo();
            ByteBuffer byteBuffer = firstAudioData.getByteBuffer();

            int audioTrackIndex = mediaMuxerWrapper.getAudioTrackIndex();
            MediaMuxer mediaMuxer = mediaMuxerWrapper.getMediaMuxer();

            mediaMuxer.writeSampleData(audioTrackIndex, byteBuffer, bufferInfo);

            audioDataTreeMap.remove(firstKey);
            return true;
        } else {
            return false;
        }
    }


}
