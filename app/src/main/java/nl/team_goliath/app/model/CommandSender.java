package nl.team_goliath.app.model;

import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;

public interface CommandSender {
    void sendCommand(CommandMessage commandMessage);
}
