package nl.team_goliath.app.model;

import android.text.SpannableStringBuilder;

import com.google.protobuf.Message;

import java.util.List;

public interface MessageFormatter {
    List<SpannableStringBuilder> format(Message message);
}