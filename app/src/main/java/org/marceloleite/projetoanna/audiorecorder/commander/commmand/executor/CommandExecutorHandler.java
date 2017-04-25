package org.marceloleite.projetoanna.audiorecorder.commander.commmand.executor;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandTask;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommandExecutorHandler extends Handler {

    private static final String LOG_TAG = CommandExecutorHandler.class.getSimpleName();

    public static final int EXECUTE_COMMAND = 0x391f;

    public static final int CHECK_PACKAGE = 0x3a0b;

    private CommandExecutorInterface commandExecutorInterface;

    public CommandExecutorHandler(CommandExecutorInterface commandExecutorInterface) {
        super();
        this.commandExecutorInterface = commandExecutorInterface;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case EXECUTE_COMMAND:
                CommandTask commandTask = (CommandTask) message.obj;
                Log.d(LOG_TAG, "handleMessage, 30: " + commandTask.getCommandType());
                commandExecutorInterface.executeCommand(commandTask);
                break;
            case CHECK_PACKAGE:
                commandExecutorInterface.checkPackage();
                break;
            default:
                Log.d(LOG_TAG, "handleMessage, 33: Handling message to another handler.");
                super.handleMessage(message);
                break;
        }
    }
}
