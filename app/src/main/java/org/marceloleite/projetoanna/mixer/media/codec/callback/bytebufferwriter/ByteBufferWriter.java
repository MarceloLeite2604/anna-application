package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import android.util.Log;

import java.util.TreeMap;

/**
 * Created by marcelo on 23/05/17.
 */

public abstract class ByteBufferWriter {

    private static final String LOG_TAG = ByteBufferWriter.class.getSimpleName();

    protected static final int DEFAULT_MAXIMUM_ITEMS_ON_MAP = 8;

    protected TreeMap<Long, AudioData> audioDataTreeMap;

    protected int maximumItemsOnMap;

    public ByteBufferWriter() {
        this.maximumItemsOnMap = DEFAULT_MAXIMUM_ITEMS_ON_MAP;
        this.audioDataTreeMap = new TreeMap<>();
    }

    public ByteBufferWriter(int maximumItemsOnMap) {
        this.maximumItemsOnMap = maximumItemsOnMap;
        this.audioDataTreeMap = new TreeMap<>();
    }

    protected abstract boolean writeFirstItem();

    protected void postConcludeWriting() {

    }

    public void add(AudioData audioData) {
        Long mapKey = audioData.getBufferInfo().presentationTimeUs;

        audioDataTreeMap.put(mapKey, audioData);

        if (audioDataTreeMap.size() > maximumItemsOnMap) {
            writeFirstItem();
        }
    }

    public void concludeWriting() {
        while (writeFirstItem()) ;
        postConcludeWriting();
    }
}
