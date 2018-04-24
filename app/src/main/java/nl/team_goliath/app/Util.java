package nl.team_goliath.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Util {
    public static final String MESSAGE_PAYLOAD_KEY = "payload";

    public static Message bundledMessage(Handler uiThreadHandler, idp.MessageOuterClass.Content data) {
        Message m = uiThreadHandler.obtainMessage();
        prepareMessage(m, data);
        return m;
    }

    public static void prepareMessage(Message m, idp.MessageOuterClass.Content data) {
        Bundle b = new Bundle();
        b.putString(MESSAGE_PAYLOAD_KEY, data.getContent());
        m.setData(b);
    }
}
