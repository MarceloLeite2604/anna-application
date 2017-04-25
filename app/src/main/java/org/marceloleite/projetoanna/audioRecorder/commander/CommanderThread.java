package org.marceloleite.projetoanna.audioRecorder.commander;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.marceloleite.projetoanna.MainActivity;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.CommandTask;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.executor.CommandExecutorHandler;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.executor.CommandExecutorInterface;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.result.CommandResultHandler;
import org.marceloleite.projetoanna.audioRecorder.commander.commmand.result.CommandResultType;
import org.marceloleite.projetoanna.audioRecorder.operator.Operator;
import org.marceloleite.projetoanna.audioRecorder.operator.OperatorException;

/**
 * Created by Marcelo Leite on 24/04/2017.
 */

public class CommanderThread extends Thread implements CommandExecutorInterface {

    private Operator operator;

    private CommandExecutorHandler commandExecutorHandler;

    private CommandResultHandler commandResultHandler;

    private boolean finishExecution;

    public CommanderThread(CommandResultHandler commandResultHandler, Context context, BluetoothSocket bluetoothSocket) {
        this.commandResultHandler = commandResultHandler;
        this.operator = new Operator(context, bluetoothSocket);
        this.finishExecution = false;
    }

    public CommandExecutorHandler getCommandExecutorHandler() {
        return commandExecutorHandler;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Looper.prepare();

        this.commandExecutorHandler = new CommandExecutorHandler(this);

        sendCheckPackageMessage();

        Looper.loop();
    }

    @Override
    public void executeCommand(CommandTask commandTask) {
        Log.d(MainActivity.LOG_TAG, "checkCommandToExecute, 70: Executing command \"" + commandTask.getCommandType() + "\".");
        Integer returnValue = null;
        Throwable throwable = null;

        switch (commandTask.getCommandType()) {
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
                Log.w(MainActivity.LOG_TAG, "executeCommand, 79: \"" + commandTask.getCommandType() + "\"Not implemented yet.");
                throwable = new Throwable("CommandTask \"" + commandTask.getCommandType() + "\" is not implemented yet.");
                break;
            case FINISH_EXECUTION:
                this.finishExecution = true;
                break;
            default:
                Log.e(MainActivity.LOG_TAG, "executeInterfaceCommand, 37: Unknown commandTask \"" + commandTask.getCommandType() + "\".");
                break;
        }

        if (returnValue != null) {
            commandTask.setCommandResultType(CommandResultType.VALUE_RETURNED);
            commandTask.setReturnValue(returnValue);
        } else {
            commandTask.setCommandResultType(CommandResultType.EXCEPTION_THROWN);
            commandTask.setThrowable(throwable);
        }


        Message commandResultMessage = commandResultHandler.obtainMessage();
        commandResultMessage.what = CommandResultHandler.RECEIVE_COMMAND_RESULT;
        commandResultMessage.obj = commandTask;
        Log.d(MainActivity.LOG_TAG, "executeCommand, 105: Sending the result of command " + commandTask.getCommandType());
        commandResultHandler.sendMessage(commandResultMessage);
    }

    @Override
    public void checkPackage() {
        try {
            operator.receivePackage();
        } catch (OperatorException operatorException) {
                /* TODO: What should be done? */
        }

        sendCheckPackageMessage();
    }

    private void sendCheckPackageMessage() {
        Message checkPackageMessage = this.commandExecutorHandler.obtainMessage();
        checkPackageMessage.what = CommandExecutorHandler.CHECK_PACKAGE;
        this.commandExecutorHandler.sendMessage(checkPackageMessage);
    }
}
