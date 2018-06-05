package nl.team_goliath.app.manager;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import nl.team_goliath.app.model.MessageListener;
import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier.MessageCase;

/**
 * Handles the routing of {@link MessageCase} messages to associated handlers.
 * A {@link HashMap} is used to store the association between events and their respective handlers.
 */
public class EventDispatcher {
    private final Map<MessageCase, MessageListener> handlers = new HashMap<>();

    /**
     * Links an {@link MessageCase} to a specific {@link MessageListener}.
     *
     * @param messageCase The {@link MessageCase} to be added.
     * @param handler     The {@link MessageListener} that will be handling the {@link MessageCase}.
     */
    public void addHandler(MessageCase messageCase, MessageListener handler) {
        handlers.put(messageCase, handler);
    }

    /**
     * Removes an {@link MessageListener} for a specific {@link MessageCase}.
     *
     * @param messageCase The {@link MessageCase} to be removed.
     */
    public void removeHandler(MessageCase messageCase) {
        handlers.remove(messageCase);
    }

    /**
     * Gets an {@link MessageListener} for a specific {@link MessageCase}.
     *
     * @param messageCase The {@link MessageCase} to get the handler for.
     */
    @Nullable
    public MessageListener getHandlerForMessageCase(MessageCase messageCase) {
        return handlers.get(messageCase);
    }
}