package nl.team_goliath.app.model;

import androidx.annotation.NonNull;
import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier;

public interface Subscriber {
    void connect(String address);

    void subscribe(@NonNull MessageCarrier.MessageCase key, @NonNull MessageListener listener);

    void unsubscribe(@NonNull MessageCarrier.MessageCase key);

    void disconnect();
}