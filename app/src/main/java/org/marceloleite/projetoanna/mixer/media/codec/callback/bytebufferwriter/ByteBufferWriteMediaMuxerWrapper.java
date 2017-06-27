package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import android.media.MediaCodec;
import android.media.MediaMuxer;

import org.marceloleite.projetoanna.mixer.media.MediaMuxerWrapper;
import org.marceloleite.projetoanna.utils.Log;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 * Writes of the audio data pieces received on a {@link MediaMuxer} wrapped on a
 * {@link MediaMuxerWrapper} object.
 */
public class ByteBufferWriteMediaMuxerWrapper extends ByteBufferWriter {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ByteBufferWriteMediaMuxerWrapper.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The wrapper class of the {@link MediaMuxer} object.
     */
    private MediaMuxerWrapper mediaMuxerWrapper;

    /**
     * Constructor.
     *
     * @param mediaMuxerWrapper The wrapper class of the {@link MediaMuxer} object.
     */
    public ByteBufferWriteMediaMuxerWrapper(MediaMuxerWrapper mediaMuxerWrapper) {
        super();
        this.mediaMuxerWrapper = mediaMuxerWrapper;
    }

    @SuppressWarnings("unused")
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
