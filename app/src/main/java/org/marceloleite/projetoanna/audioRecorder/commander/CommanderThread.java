package org.marceloleite.projetoanna.audioRecorder.commander;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.operator.Operator;
import org.marceloleite.projetoanna.audioRecorder.operator.OperatorException;
import org.marceloleite.projetoanna.audioRecorder.operator.command.Command;
import org.marceloleite.projetoanna.audioRecorder.operator.command.CommandResult;
import org.marceloleite.projetoanna.audioRecorder.operator.command.CommandType;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommanderThread extends Thread {

    private Operator operator;

    private Handler commandRequestHandler;

    private Commander commander;

    private volatile CommandType commandTypeToExecute;


    public volatile boolean finishExecution;

    public CommanderThread(Commander commander, BluetoothSocket bluetoothSocket) {
        this.commander = commander;
        this.operator = new Operator(this.commander.getAudioRecorder().getMainActivity(), bluetoothSocket);
        this.commandRequestHandler = null;
        this.finishExecution = false;
    }

    public Handler getCommandRequestHandler() {
        return commandRequestHandler;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Looper.prepare();

        this.commandRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Command command = (Command) message.obj;
                scheduleCommandToExecute(command.getCommandType());
            }
        };

        while (!finishExecution) {
            try {
                if (!checkCommandToExecute()) {
                    operator.receivePackage();
                }
            } catch (OperatorException operatorException) {
                /* TODO: What should be done? */
            }
        }

        Looper.loop();
    }

    private void scheduleCommandToExecute(CommandType commandType) {
        this.commandTypeToExecute = commandType;
    }

    private boolean checkCommandToExecute() {
        if (commandTypeToExecute != null) {

            Log.d(MainActivity.LOG_TAG, "checkCommandToExecute, 70: Executing command \"" + commandTypeToExecute.getTitle() + "\".");
            Integer returnValue = null;
            Throwable throwable = null;
            Command command = new Command(commandTypeToExecute);
            commandTypeToExecute = null;

            switch (command.getCommandType()) {
                case START_AUDIO_RECORD:
                    try {
                        returnValue = operator.startRecord();
                    } catch (OperatorException operatorException) {
                        throwable = operatorException;
                    }
                    break;
                case STOP_AUDIO_RECORD:
                    try {
                        returnValue = operator.stopRecord();
                    } catch (OperatorException operatorException) {
                        throwable = operatorException;
                    }
                    break;
                case DISCONNECT:
                    Log.w(MainActivity.LOG_TAG, "executeInterfaceCommand, 68: Not implemented yet.");
                    throwable = new Throwable("Command \"" + command.getCommandType().getTitle() + "\" is not implemented yet.");
                    break;
                case FINISH_EXECUTION:
                    this.finishExecution = true;
                    break;
                default:
                    Log.e(MainActivity.LOG_TAG, "executeInterfaceCommand, 37: Unknown command \"" + command.getCommandType().getTitle() + "\".");
                    break;
            }

            if (returnValue != null) {
                command.setCommandResult(CommandResult.VALUE_RETURNED);
                command.setReturnValue(returnValue);
            } else {
                command.setCommandResult(CommandResult.EXCEPTION_THROWN);
                command.setThrowable(throwable);
            }

            Message commandResultMessage = Message.obtain();
            commandResultMessage.obj = command;
            commander.getCommandResultHandler().dispatchMessage(commandResultMessage);

            return true;
        }
        return false;
    }
}
