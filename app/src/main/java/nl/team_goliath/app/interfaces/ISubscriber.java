package nl.team_goliath.app.interfaces;

import nl.team_goliath.app.protos.MessageCarrierProtos.MessageCarrier;

public interface ISubscriber {
    void connect(String address);

    void subscribe(MessageCarrier.MessageCase key, IMessageListener listener);

    void unsubscribe(MessageCarrier.MessageCase key);

    void disconnect();
}