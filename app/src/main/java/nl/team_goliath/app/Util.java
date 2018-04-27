package nl.team_goliath.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import nl.team_goliath.app.protos.CommandMessageProtos.Command;

public class Util {
    public static final String MESSAGE_PAYLOAD_KEY = "payload";

    public static Message bundledMessage(Handler uiThreadHandler, Command command) {
        Message m = uiThreadHandler.obtainMessage();
        prepareMessage(m, command);
        return m;
    }

    public static void prepareMessage(Message m, Command command) {
        Bundle b = new Bundle();
        b.putString(MESSAGE_PAYLOAD_KEY, String.valueOf(command.getCommandCase().getNumber()));
        m.setData(b);
    }
}
