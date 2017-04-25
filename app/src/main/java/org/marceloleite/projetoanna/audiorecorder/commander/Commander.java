package org.marceloleite.projetoanna.audiorecorder.commander;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audiorecorder.AudioRecorder;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandTask;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.CommandType;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.executor.CommandExecutorHandler;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.result.CommandResultHandler;
import org.marceloleite.projetoanna.audiorecorder.commander.commmand.result.CommandResultInterface;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Commander implements CommandResultInterface {

    private static final String LOG_TAG = Commander.class.getSimpleName();

    private CommandResultHandler commandResultHandler;

    private AudioRecorder audioRecorder;

    private CommanderThread commanderThread;

    public Commander(AudioRecorder audioRecorder, BluetoothSocket bluetoothSocket) {
        this.audioRecorder = audioRecorder;
        this.commandResultHandler = new CommandResultHandler(this);

        this.commanderThread = new CommanderThread(commandResultHandler, audioRecorder.getMainActivity(), bluetoothSocket);
        this.commanderThread.start();
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }

    public Handler getCommandResultHandler() {
        return commandResultHandler;
    }

    public void executeCommand(CommandType commandType) {
        Log.d(LOG_TAG, "executeCommand, 46: " + commandType);
        CommandTask commandTask = new CommandTask(commandType);

        CommandExecutorHandler commandExecutorHandler = commanderThread.getCommandExecutorHandler();
        Message commandExecutorMessage = commandExecutorHandler.obtainMessage();
        commandExecutorMessage.what = CommandExecutorHandler.EXECUTE_COMMAND;
        commandExecutorMessage.obj = commandTask;
        Log.d(LOG_TAG, "executeCommand, 51: Sending message to execute command \"" + commandType + "\".");
        commanderThread.getCommandExecutorHandler().sendMessage(commandExecutorMessage);
    }

    @Override
    public void receiveCommandResult(CommandTask commandTask) {
        this.audioRecorder.checkCommandResult(commandTask);
    }
}
