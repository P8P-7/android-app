package nl.team_goliath.app.model;

import com.google.protobuf.Message;

import java.util.List;

public interface MessageFormatter<T> {
    List<T> format(Message message);
}