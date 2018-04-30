package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageProtos.Message;

public interface IMessageListener {
    void onMessageReceived(String channel, Message message);

    void onError(String message);
}