package org.marceloleite.projetoanna.audiorecorder.commander.commmand.result;

import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandTask;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public interface CommandResultInterface {

    void receiveCommandResult(CommandTask commandTask);
}
