package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageProtos.Message;

public interface IPublisher {
    void connect(String address);

    void send(Message message);

    void disconnect();
}