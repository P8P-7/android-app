package nl.team_goliath.app.model;

import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier;

public interface Publisher {
    void connect(String address);

    void send(MessageCarrier messageCarrier);

    void disconnect();
}