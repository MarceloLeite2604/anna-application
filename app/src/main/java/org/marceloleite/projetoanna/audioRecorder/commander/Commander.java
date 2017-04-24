package org.marceloleite.projetoanna.audioRecorder.commander;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.AudioRecorder;
import org.marceloleite.projetoanna.audioRecorder.operator.command.Command;
import org.marceloleite.projetoanna.audioRecorder.operator.command.CommandType;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class Commander {

    private Handler commandResultHandler;

    private AudioRecorder audioRecorder;

    private CommanderThread commanderThread;

    public Commander(AudioRecorder audioRecorder, BluetoothSocket bluetoothSocket) {
        this.audioRecorder = audioRecorder;
        this.commandResultHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                checkCommandResult((Command) message.obj);
            }
        };

        this.commanderThread = new CommanderThread(this, bluetoothSocket);
        this.commanderThread.start();
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }

    public void checkCommandResult(Command command) {
        this.audioRecorder.checkCommandResult(command);
    }

    public Handler getCommandResultHandler() {
        return commandResultHandler;
    }

    public void executeCommand(CommandType commandType) {
        Command command = new Command(commandType);

        Message commandRequestMessage = new Message();
        commandRequestMessage.obj = command;

        Handler commandRequestHandler = commanderThread.getCommandRequestHandler();
        Log.d(MainActivity.LOG_TAG, "executeCommand, 59: Requesting to execute command \"" + commandType.getTitle() + "\".");
        commandRequestHandler.dispatchMessage(commandRequestMessage);
        Log.d(MainActivity.LOG_TAG, "executeCommand, 64: Concluded \"executeCommand\".");
    }
}
