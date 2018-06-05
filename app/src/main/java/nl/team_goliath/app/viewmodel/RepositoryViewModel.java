package nl.team_goliath.app.viewmodel;

import android.text.SpannableStringBuilder;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import nl.team_goliath.app.GoliathApp;
import nl.team_goliath.app.livedata.SynchronizeMessageLiveData;
import nl.team_goliath.app.model.MessageFormatter;
import nl.team_goliath.app.model.Status;

public class RepositoryViewModel extends ViewModel {
    private final SynchronizeMessageLiveData synchronizeMessageRepo = new SynchronizeMessageLiveData(GoliathApp.getEventDispatcher());

    private final LiveData<List<SpannableStringBuilder>> messages;

    private final HashMap<Class<? extends Message>, MessageFormatter> repositories = new HashMap<>();

    RepositoryViewModel() {
        messages = Transformations.map(synchronizeMessageRepo, resource -> {
            if (resource == null || resource.status != Status.SUCCESS) {
                return new ArrayList<>();
            }

            List<SpannableStringBuilder> messages = new ArrayList<>();
            for (com.google.protobuf.Any anyMessage : resource.data.getMessagesList()) {
                parseMessage(messages, anyMessage);
            }

            return messages;
        });
    }

    private void parseMessage(List<SpannableStringBuilder> messages, com.google.protobuf.Any anyMessage) {
        for (Map.Entry<Class<? extends Message>, MessageFormatter> repositoryEntry : repositories.entrySet()) {
            if (anyMessage.is(repositoryEntry.getKey())) {
                try {
                    Message repository = anyMessage.unpack(repositoryEntry.getKey());
                    messages.addAll(repositoryEntry.getValue().format(repository));
                } catch (InvalidProtocolBufferException ignored) {
                }
            }
        }
    }

    public LiveData<List<SpannableStringBuilder>> getMessages() {
        return messages;
    }

    public SynchronizeMessageLiveData getRepo() {
        return synchronizeMessageRepo;
    }

    public <E extends Message> void watchRepo(Class<E> repoClass, MessageFormatter formatter) {
        repositories.put(repoClass, formatter);
    }

}