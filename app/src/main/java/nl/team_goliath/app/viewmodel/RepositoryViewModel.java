package nl.team_goliath.app.viewmodel;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import nl.team_goliath.app.GoliathApp;
import nl.team_goliath.app.livedata.SynchronizeMessageLiveData;
import nl.team_goliath.app.model.Resource;

public class RepositoryViewModel extends ViewModel {
    private final SynchronizeMessageLiveData synchronizeMessageRepo = new SynchronizeMessageLiveData(GoliathApp.getEventDispatcher());

    private final LiveData<Resource<List<? extends Message>>> messages;

    protected final List<Class<? extends Message>> repositories = new ArrayList<>();

    RepositoryViewModel() {
        messages = Transformations.map(synchronizeMessageRepo, resource -> {
            switch (resource.status) {
                case LOADING:
                    return Resource.loading(null);
                case ERROR:
                    return Resource.error(resource.message, null);
                case SUCCESS:
                default:
                    List<Message> messages = new ArrayList<>();
                    for (com.google.protobuf.Any anyMessage : resource.data.getMessagesList()) {
                        try {
                            parseMessage(messages, anyMessage);
                        } catch (InvalidProtocolBufferException e) {
                            return Resource.error(e.getMessage(), null);
                        }
                    }

                    return Resource.success(messages);
            }
        });
    }

    private void parseMessage(List<Message> messages, com.google.protobuf.Any anyMessage) throws InvalidProtocolBufferException {
        for (Class<? extends Message> repo : repositories) {
            if (anyMessage.is(repo)) {
                messages.add(anyMessage.unpack(repo));
            }
        }
    }

    public LiveData<Resource<List<? extends Message>>> getMessages() {
        return messages;
    }

    public SynchronizeMessageLiveData getRepo() {
        return synchronizeMessageRepo;
    }

    public <E extends Message> void watchRepo(Class<E> repoClass) {
        repositories.add(repoClass);
    }
}