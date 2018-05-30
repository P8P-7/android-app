package nl.team_goliath.app.model;

import com.google.protobuf.Message;

public interface MessageListener {
    void onMessageReceived(Message message);

    void onError(String message);
}