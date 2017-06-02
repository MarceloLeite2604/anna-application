package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import org.marceloleite.projetoanna.utils.Log;

import java.util.TreeMap;

/**
 * Created by marcelo on 23/05/17.
 */

public abstract class ByteBufferWriter {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = ByteBufferWriter.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(ByteBufferWriter.class);
    }

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
