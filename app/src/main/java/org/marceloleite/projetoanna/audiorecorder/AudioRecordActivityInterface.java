package org.marceloleite.projetoanna.audiorecorder;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcelo Leite on 29/04/2017.
 */

public interface AudioRecordActivityInterface {

    AppCompatActivity getActivity();

    void updateInterface();

    void receiveLatestAudioFileConcluded();
}
