package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageCarrierProtos.MessageCarrier;

public interface IMessageListener {
    void onMessageReceived(String channel, MessageCarrier messageCarrier);

    void onError(String message);
}