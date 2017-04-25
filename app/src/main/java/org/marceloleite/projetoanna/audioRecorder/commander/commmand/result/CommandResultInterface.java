package org.marceloleite.projetoanna.audioRecorder.commander.commmand.result;

import org.marceloleite.projetoanna.audioRecorder.commander.commmand.CommandTask;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public interface CommandResultInterface {

    void receiveCommandResult(CommandTask commandTask);
}
