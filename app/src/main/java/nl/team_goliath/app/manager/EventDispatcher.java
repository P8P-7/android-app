package nl.team_goliath.app.manager;

import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import nl.team_goliath.app.model.MessageListener;

/**
 * Handles the routing of {@link Message} messages to associated handlers.
 * A {@link HashMap} is used to store the association between events and their respective handlers.
 */
public class EventDispatcher {
    private Map<Class<? extends Message>, MessageListener> handlers = new HashMap<>();

    /**
     * Links an {@link Message} to a specific {@link MessageListener}.
     *
     * @param eventType The {@link Message} to be added.
     * @param handler   The {@link MessageListener} that will be handling the {@link Message}.
     */
    public <E extends Message> void addHandler(Class<E> eventType, MessageListener handler) {
        handlers.put(eventType, handler);
    }

    /**
     * Removes an {@link MessageListener} for a specific {@link Message}.
     *
     * @param eventType The {@link Message} to be removed.
     */
    public <E extends Message> void removeHandler(Class<E> eventType) {
        handlers.remove(eventType);
    }

    /**
     * Gets an {@link MessageListener} for a specific {@link Message}.
     *
     * @param eventType The {@link Message} to get the handler for.
     */
    @Nullable
    public <E extends Message> MessageListener getHandlerForClassType(Class<E> eventType) {
        //noinspection unchecked
        return handlers.get(eventType);
    }
}