package nl.team_goliath.app.model;

import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;

public interface CommandSender {
    void sendCommand(CommandMessage commandMessage);
}
