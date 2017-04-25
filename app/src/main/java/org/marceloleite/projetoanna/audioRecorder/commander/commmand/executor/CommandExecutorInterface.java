package org.marceloleite.projetoanna.audioRecorder.commander.commmand.executor;

import org.marceloleite.projetoanna.audioRecorder.commander.commmand.CommandTask;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public interface CommandExecutorInterface {

    void executeCommand(CommandTask commandTask);

    void checkPackage();
}
