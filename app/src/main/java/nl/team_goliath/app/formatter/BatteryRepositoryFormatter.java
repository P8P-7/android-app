package nl.team_goliath.app.formatter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.google.protobuf.Message;

import java.util.Collections;
import java.util.List;

import nl.team_goliath.app.model.MessageFormatter;
import nl.team_goliath.app.proto.BatteryRepositoryProto;

public class BatteryRepositoryFormatter implements MessageFormatter<SpannableStringBuilder> {
    @Override
    public List<SpannableStringBuilder> format(Message message) {
        String header = "Battery level:";
        String value = Integer.toString(((BatteryRepositoryProto.BatteryRepository) message).getLevel());
        SpannableStringBuilder str = new SpannableStringBuilder(header + " " + value);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, header.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return Collections.singletonList(str);
    }
}
