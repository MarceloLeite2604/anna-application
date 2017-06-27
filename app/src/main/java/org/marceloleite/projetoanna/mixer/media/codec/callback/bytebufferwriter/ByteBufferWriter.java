package org.marceloleite.projetoanna.mixer.media.codec.callback.bytebufferwriter;

import org.marceloleite.projetoanna.utils.Log;

import java.util.TreeMap;

/**
 * Writes pieces of audio data on an output specified by the class that inherits it.
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
        Log.addClassToLog(LOG_TAG);
    }

    private static final int DEFAULT_MAXIMUM_ITEMS_ON_MAP = 8;

    /**
     * The tree map which stores the audio data pieces to be written on the specified output. This
     * pieces are sorted on the tree map by is presentation time, guaranteeing that they are written
     * on its correct presentation order.
     */
    TreeMap<Long, AudioData> audioDataTreeMap;

    /**
     * The maximum number of audio data stored on the tree before it starts writing on the specified
     * output.
     */
    private int maximumItemsOnMap;

    /**
     * Constructor.
     */
    ByteBufferWriter() {
        this.maximumItemsOnMap = DEFAULT_MAXIMUM_ITEMS_ON_MAP;
        this.audioDataTreeMap = new TreeMap<>();
    }

    /**
     * Constructor.
     *
     * @param maximumItemsOnMap The maximum number of audio data stored on the tree before it starts
     *                          writing on the specified output.
     */
    ByteBufferWriter(int maximumItemsOnMap) {
        this.maximumItemsOnMap = maximumItemsOnMap;
        this.audioDataTreeMap = new TreeMap<>();
    }

    /**
     * Writes the first audio data of the tree map on the output specified and removes it from the
     * tree.
     *
     * @return True if an item was written on the specified output. False if there was no more audio
     * data to be written on the output.
     */
    protected abstract boolean writeFirstItem();

    /**
     * The method executed once all the audio data was written on the output specified.
     */
    void postConcludeWriting() {
    }

    /**
     * Adds an audio data on tree map.
     *
     * @param audioData The audio data to be added.
     */
    public void add(AudioData audioData) {
        Long mapKey = audioData.getBufferInfo().presentationTimeUs;

        audioDataTreeMap.put(mapKey, audioData);

        if (audioDataTreeMap.size() > maximumItemsOnMap) {
            writeFirstItem();
        }
    }

    /**
     * Finishes writing the audio data pieces on the specified output buffer.
     */
    public void finishWriting() {
        boolean continueWriting = true;
        while (continueWriting) {
            continueWriting = writeFirstItem();
        }
        postConcludeWriting();
    }
}
