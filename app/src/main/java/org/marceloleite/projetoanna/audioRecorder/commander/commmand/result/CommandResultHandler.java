package org.marceloleite.projetoanna.audioRecorder.commander.commmand.result;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.CommandTask;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommandResultHandler extends Handler {

    public static final int RECEIVE_COMMAND_RESULT = 0xab7f;

    private CommandResultInterface commandResultInterface;

    public CommandResultHandler(CommandResultInterface commandResultInterface) {
        super();
        this.commandResultInterface = commandResultInterface;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == RECEIVE_COMMAND_RESULT) {
            CommandTask commandTask = (CommandTask) message.obj;
            Log.d(MainActivity.LOG_TAG, "handleMessage, 26: " + commandTask.getCommandType());
            commandResultInterface.receiveCommandResult(commandTask);
        } else {
            Log.d(MainActivity.LOG_TAG, "handleMessage, 33: Handling message to another handler.");
            super.handleMessage(message);
        }
    }
}
