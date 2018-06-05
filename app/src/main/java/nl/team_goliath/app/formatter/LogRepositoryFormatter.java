package nl.team_goliath.app.formatter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.google.protobuf.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.MessageFormatter;
import nl.team_goliath.app.proto.LogRepositoryProto;

public class LogRepositoryFormatter implements MessageFormatter {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S", Locale.getDefault());

    private Context context;

    public LogRepositoryFormatter(Context context) {
        this.context = context;
    }

    @Override
    public List<SpannableStringBuilder> format(Message message) {
        List<SpannableStringBuilder> stringList = new ArrayList<>();

        LogRepositoryProto.LogRepository logRepository = (LogRepositoryProto.LogRepository) message;

        for (LogRepositoryProto.LogRepository.Entry logEntry : logRepository.getEntriesList()) {
            Date date = new Date(logEntry.getTimestamp());
            String dateTime = format.format(date);

            SpannableStringBuilder spannableString = new SpannableStringBuilder("[" + dateTime + "] ");
            int startPos = 1;
            int endPos = startPos + dateTime.length();
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.dateTimeColor)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.
                    append("<").
                    append(logEntry.getThreadId()).
                    append("> ");
            startPos = dateTime.length() + 4;
            endPos = startPos + logEntry.getThreadId().length();
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.threadIdColor)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString
                    .append("(")
                    .append(logEntry.getSeverity().toString().toLowerCase())
                    .append(")")
                    .append("\n");

            int messageColor;
            switch (logEntry.getSeverity()) {
                case TRACE:
                    messageColor = ContextCompat.getColor(context, R.color.traceColor);
                    break;
                case DEBUG:
                    messageColor = ContextCompat.getColor(context, R.color.debugColor);
                    break;
                case INFO:
                    messageColor = ContextCompat.getColor(context, R.color.infoColor);
                    break;
                case WARNING:
                    messageColor = ContextCompat.getColor(context, R.color.warningColor);
                    break;
                case ERROR:
                    messageColor = ContextCompat.getColor(context, R.color.errorColor);
                    break;
                case FATAL:
                    messageColor = ContextCompat.getColor(context, R.color.fatalColor);
                    break;
                default:
                    messageColor = ContextCompat.getColor(context, R.color.defaultColor);
                    break;
            }

            spannableString.append(logEntry.getMessage());
            startPos = dateTime.length() + 4 + logEntry.getThreadId().length() + 3 + logEntry.getSeverity().toString().length() + 2;
            endPos = startPos + logEntry.getMessage().length();
            spannableString.setSpan(new ForegroundColorSpan(messageColor), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (logEntry.getSeverity().equals(LogRepositoryProto.LogSeverity.DEBUG) ||
                    logEntry.getSeverity().equals(LogRepositoryProto.LogSeverity.ERROR) ||
                    logEntry.getSeverity().equals(LogRepositoryProto.LogSeverity.FATAL)) {
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (logEntry.getSeverity().equals(LogRepositoryProto.LogSeverity.FATAL)) {
                spannableString.setSpan(new UnderlineSpan(), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            stringList.add(spannableString);
        }

        return stringList;
    }
}
