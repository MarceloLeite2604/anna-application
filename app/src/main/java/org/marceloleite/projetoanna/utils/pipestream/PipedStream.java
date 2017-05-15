package org.marceloleite.projetoanna.utils.pipestream;

import android.util.Log;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by Marcelo Leite on 13/05/2017.
 */

public class PipedStream {

    private final String LOG_TAG = PipedStream.class.getSimpleName();

    private PipedInputStream pipedInputStream;

    private PipedOutputStream pipedOutputStream;

    private boolean doneWriting;

    private boolean doneReading;

    public PipedStream() {
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream = null;
        doneWriting = false;
        doneReading = false;
    }

    public void createInputStream() throws IOException {
        Log.d(LOG_TAG, "createInputStream, 33: Creating input stream.");
        pipedInputStream = new PipedInputStream(pipedOutputStream);
    }

    public PipedInputStream getPipedInputStream() {
        return pipedInputStream;
    }

    public PipedOutputStream getPipedOutputStream() {
        return pipedOutputStream;
    }

    public void doneWriting() throws IOException {
        doneWriting = true;
        checkClosePipes();
    }

    public void doneReading() throws IOException {
        doneReading = true;
        checkClosePipes();
    }

    public boolean isDoneWriting() {
        return doneWriting;
    }

    public boolean isDoneReading() {
        return doneReading;
    }

    private void checkClosePipes() throws IOException {
        if (doneReading && doneWriting) {
            pipedInputStream.close();
            pipedOutputStream.close();
        }
    }

    public boolean isOutputCreated() {
        return (pipedOutputStream != null);
    }

    public boolean isInputCreated() {
        return (pipedInputStream != null);
    }
}


