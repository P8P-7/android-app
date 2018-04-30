package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageProtos.Message;

public interface ISubscriber {
    void connect(String address);

    void subscribe(Message.DataCase key, IMessageListener listener);

    void unsubscribe(Message.DataCase key);

    void disconnect();
}