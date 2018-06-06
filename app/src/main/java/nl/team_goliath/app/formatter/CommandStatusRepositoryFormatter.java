package nl.team_goliath.app.formatter;

import android.text.SpannableStringBuilder;

import com.google.protobuf.Message;

import java.util.Collections;
import java.util.List;

import nl.team_goliath.app.model.MessageFormatter;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto;

public class CommandStatusRepositoryFormatter implements MessageFormatter {
    @Override
    public List<SpannableStringBuilder> format(Message message) {
        String header = "Command Status:";
        SpannableStringBuilder commandStatusRepositoryBuilder = new SpannableStringBuilder(header);

        CommandStatusRepositoryProto.CommandStatusRepository commandStatusRepository = (CommandStatusRepositoryProto.CommandStatusRepository) message;

        for(CommandStatusRepositoryProto.CommandStatusItem item : commandStatusRepository.getStatusList()) {
            commandStatusRepositoryBuilder
                    .append('\n')
                    .append(Integer.toString(item.getId()))
                    .append(": ");

            commandStatusRepositoryBuilder
                    .append(item.getCommandStatus().name());
        }

        return Collections.singletonList(commandStatusRepositoryBuilder);
    }
}
