package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageCarrierProtos.MessageCarrier;

public interface IPublisher {
    void connect(String address);

    void send(MessageCarrier messageCarrier);

    void disconnect();
}