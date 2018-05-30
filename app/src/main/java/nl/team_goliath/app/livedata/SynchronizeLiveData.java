package nl.team_goliath.app.livedata;

import com.google.protobuf.Message;

import androidx.lifecycle.LiveData;
import nl.team_goliath.app.manager.EventDispatcher;
import nl.team_goliath.app.model.MessageListener;
import nl.team_goliath.app.model.Resource;

public abstract class SynchronizeLiveData<T extends Message> extends LiveData<Resource<T>> {
    private final Class<T> typeParameterClass;
    private EventDispatcher dispatcher;

    SynchronizeLiveData(EventDispatcher dispatcher, Class<T> typeParameterClass) {
        this.dispatcher = dispatcher;
        this.typeParameterClass = typeParameterClass;
        setValue(Resource.loading(null));
    }

    private MessageListener listener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            // noinspection unchecked
            setValue(Resource.success((T) message));
        }

        @Override
        public void onError(String message) {
            setValue(Resource.error(message, null));
        }
    };

    @Override
    protected void onActive() {
        dispatcher.addHandler(typeParameterClass, listener);
    }

    @Override
    protected void onInactive() {
        dispatcher.removeHandler(typeParameterClass);
    }
}