package nl.team_goliath.app.livedata;

import com.google.protobuf.Message;

import androidx.lifecycle.LiveData;
import nl.team_goliath.app.manager.EventDispatcher;
import nl.team_goliath.app.model.MessageListener;
import nl.team_goliath.app.model.Resource;
import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier.MessageCase;
import nl.team_goliath.app.proto.SynchronizeMessageProto.SynchronizeMessage;

public class SynchronizeMessageLiveData extends LiveData<Resource<SynchronizeMessage>> {

    public SynchronizeMessageLiveData(EventDispatcher dispatcher) {
        setValue(Resource.loading(null));
        dispatcher.addHandler(MessageCase.SYNCHRONIZEMESSAGE, new MessageListener() {
            @Override
            public void onMessageReceived(Message message) {
                setValue(Resource.success((SynchronizeMessage) message));
            }

            @Override
            public void onError(String message) {
                setValue(Resource.error(message, null));
            }
        });
    }
}