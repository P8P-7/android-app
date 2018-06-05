package nl.team_goliath.app.formatter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.List;

import nl.team_goliath.app.model.MessageFormatter;
import nl.team_goliath.app.proto.CommandExecutorConfigProto.CommandExecutorConfig;
import nl.team_goliath.app.proto.EmotionConfigProto.EmotionConfig;
import nl.team_goliath.app.proto.GpioConfigProto.GpioConfig;
import nl.team_goliath.app.proto.I2cConfigProto.I2cConfig;
import nl.team_goliath.app.proto.LoggingConfigProto.LoggingConfig;
import nl.team_goliath.app.proto.MotorControllerConfigProto.MotorControllerConfig;
import nl.team_goliath.app.proto.MotorProto.Motor;
import nl.team_goliath.app.proto.SerialConfigProto.SerialConfig;
import nl.team_goliath.app.proto.ServoConfigProto.ServoConfig;
import nl.team_goliath.app.proto.VisionConfigProto.VisionConfig;
import nl.team_goliath.app.proto.WatcherConfigProto.WatcherConfig;
import nl.team_goliath.app.proto.WingProto.Wing;
import nl.team_goliath.app.proto.ZmqConfigProto.ZmqConfig;
import nl.team_goliath.app.proto.ZmqConfigRepositoryProto.ConfigRepository;

public class ConfigRepositoryFormatter implements MessageFormatter {
    @Override
    public List<SpannableStringBuilder> format(Message message) {
        List<SpannableStringBuilder> stringList = new ArrayList<>();

        ConfigRepository configRepository = (ConfigRepository) message;

        ZmqConfig zmqConfig = configRepository.getZmq();
        String zmqConfigHeader = "ZMQ config:";

        SpannableStringBuilder zmqConfigStr = new SpannableStringBuilder(zmqConfigHeader);
        zmqConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, zmqConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        zmqConfigStr
                .append("\n")
                .append("Subscriber port: ")
                .append(String.valueOf(zmqConfig.getSubscriberPort()));

        zmqConfigStr
                .append("\n")
                .append("Publisher port: ")
                .append(String.valueOf(zmqConfig.getPublisherPort()));

        stringList.add(zmqConfigStr);

        SerialConfig serialConfig = configRepository.getSerial();
        String serialConfigHeader = "Serial config:";

        SpannableStringBuilder serialConfigStr = new SpannableStringBuilder(serialConfigHeader);
        serialConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, serialConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        serialConfigStr
                .append("\n")
                .append("Serial port: ")
                .append(serialConfig.getPort());

        serialConfigStr
                .append("\n")
                .append("Serial baudrate: ")
                .append(String.valueOf(serialConfig.getBaudrate()));

        stringList.add(serialConfigStr);

        GpioConfig gpioConfig = configRepository.getGpio();
        String gpioConfigHeader = "GPIO config:";

        SpannableStringBuilder gpioConfigStr = new SpannableStringBuilder(gpioConfigHeader);
        gpioConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, gpioConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        gpioConfigStr
                .append("\n")
                .append("GPIO Pin: ")
                .append(String.valueOf(gpioConfig.getPin()));

        stringList.add(gpioConfigStr);

        VisionConfig visionConfig = configRepository.getVision();
        String visionConfigHeader = "Vision config:";

        SpannableStringBuilder visionConfigStr = new SpannableStringBuilder(visionConfigHeader);
        visionConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, visionConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        visionConfigStr
                .append("\n")
                .append("Webcam port: ")
                .append(String.valueOf(visionConfig.getWebcam()));

        stringList.add(visionConfigStr);

        ServoConfig servosConfig = configRepository.getServos();
        String servosConfigHeader = "Servos config:";

        SpannableStringBuilder servosConfigStr = new SpannableStringBuilder(servosConfigHeader);
        servosConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, servosConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        servosConfigStr.append("\n");

        List<Wing> wingsList = servosConfig.getWingsList();
        for (int i = 0, wingsListSize = wingsList.size(); i < wingsListSize; i++) {
            Wing wing = wingsList.get(i);
            servosConfigStr.append("Wing id: ")
                    .append(String.valueOf(wing.getId()))
                    .append("\n");
            servosConfigStr.append("Wing position: ")
                    .append(wing.getPosition().name());
            if (i != wingsList.size() - 1) {
                servosConfigStr.append("\n");
            }
        }

        stringList.add(servosConfigStr);

        I2cConfig i2cConfig = configRepository.getI2C();
        String i2cConfigHeader = "I2C config:";

        SpannableStringBuilder i2cConfigStr = new SpannableStringBuilder(i2cConfigHeader);
        i2cConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, i2cConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        i2cConfigStr
                .append("\n")
                .append("I2C Device: ")
                .append(i2cConfig.getDevice());

        stringList.add(i2cConfigStr);

        MotorControllerConfig motorControllerConfig = configRepository.getMotorController();
        String motorControllerConfigHeader = "Motor controller config:";

        SpannableStringBuilder motorControllerConfigStr = new SpannableStringBuilder(motorControllerConfigHeader);
        motorControllerConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, motorControllerConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        motorControllerConfigStr
                .append("\n")
                .append("Motor address: ")
                .append(motorControllerConfig.getAddress())
                .append("\n");

        List<Motor> motorsList = motorControllerConfig.getMotorsList();
        for (int i = 0, motorsListSize = motorsList.size(); i < motorsListSize; i++) {
            Motor motor = motorsList.get(i);
            motorControllerConfigStr.append("Motor id: ")
                    .append(String.valueOf(motor.getId()))
                    .append("\n");
            motorControllerConfigStr.append("Motor position: ")
                    .append(motor.getPosition().name());
            if (i != wingsList.size() - 1) {
                motorControllerConfigStr.append("\n");
            }
        }

        stringList.add(motorControllerConfigStr);

        EmotionConfig emotionConfig = configRepository.getEmotions();
        String emotionConfigHeader = "Emotion config:";

        SpannableStringBuilder emotionConfigStr = new SpannableStringBuilder(emotionConfigHeader);
        emotionConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, emotionConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        emotionConfigStr
                .append("\n")
                .append("Emotion host: ")
                .append(emotionConfig.getHost());

        emotionConfigStr
                .append("\n")
                .append("Emotion port: ")
                .append(String.valueOf(emotionConfig.getPort()));

        stringList.add(emotionConfigStr);

        CommandExecutorConfig commandExecutorConfig = configRepository.getCommandExecutor();
        String commandExecutorConfigHeader = "Command executor config";

        SpannableStringBuilder commandExecutorConfigStr = new SpannableStringBuilder(commandExecutorConfigHeader);
        commandExecutorConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, commandExecutorConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        commandExecutorConfigStr
                .append("\n")
                .append("Number of executors: ")
                .append(String.valueOf(commandExecutorConfig.getNumberOfExecutors()));

        stringList.add(commandExecutorConfigStr);

        WatcherConfig watcherConfig = configRepository.getWatcher();
        String watcherConfigHeader = "Watcher config";

        SpannableStringBuilder watcherConfigStr = new SpannableStringBuilder(watcherConfigHeader);
        watcherConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, watcherConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        watcherConfigStr
                .append("\n")
                .append("Polling rate: ")
                .append(String.valueOf(watcherConfig.getPollingRate()));

        stringList.add(watcherConfigStr);

        LoggingConfig loggingConfig = configRepository.getLogging();
        String loggingConfigHeader = "Logging config";

        SpannableStringBuilder loggingConfigStr = new SpannableStringBuilder(loggingConfigHeader);
        loggingConfigStr.setSpan(new StyleSpan(Typeface.BOLD), 0, loggingConfigHeader.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loggingConfigStr
                .append("\n")
                .append("Severity level: ")
                .append(String.valueOf(loggingConfig.getSeverityLevel().name()));

        stringList.add(loggingConfigStr);

        return stringList;
    }
}
