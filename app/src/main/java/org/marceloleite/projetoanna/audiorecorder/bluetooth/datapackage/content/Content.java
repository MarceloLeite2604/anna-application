package org.marceloleite.projetoanna.audiorecorder.bluetooth.datapackage.content;

/**
 * The fileChunk content stored on a package.
 */
public abstract class Content {

    /**
     * Converts the content into a byte array.
     *
     * @return A byte array with the information stored on this content.
     */
    public abstract byte[] convertToBytes();
}
