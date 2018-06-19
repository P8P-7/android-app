package nl.team_goliath.app.formatter;

import android.view.ContextThemeWrapper;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import nl.team_goliath.app.proto.CommandExecutorConfigProto.CommandExecutorConfig;
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

public class ConfigRepositoryFormatter {

    private ContextThemeWrapper contextThemeWrapper;
    private PreferenceScreen preferenceScreen;

    public ConfigRepositoryFormatter(ContextThemeWrapper contextThemeWrapper, PreferenceScreen preferenceScreen) {
        this.contextThemeWrapper = contextThemeWrapper;
        this.preferenceScreen = preferenceScreen;
    }

    public void parseToPreferences(ConfigRepository configRepository) {
        ZmqConfig zmqConfig = configRepository.getZmq();

        PreferenceCategory zmqCategory = new PreferenceCategory(contextThemeWrapper);
        zmqCategory.setIcon(null);
        zmqCategory.setKey("zmq-config");
        zmqCategory.setTitle("ZMQ config");

        preferenceScreen.addPreference(zmqCategory);

        Preference subPortPreference = new Preference(contextThemeWrapper);
        subPortPreference.setKey("subscriber-port");
        subPortPreference.setTitle("Subscriber port");
        subPortPreference.setSummary(String.valueOf(zmqConfig.getSubscriberPort()));

        zmqCategory.addPreference(subPortPreference);

        Preference pubPortPreference = new Preference(contextThemeWrapper);
        pubPortPreference.setKey("publisher-port");
        pubPortPreference.setTitle("Publisher port");
        pubPortPreference.setSummary(String.valueOf(zmqConfig.getPublisherPort()));

        zmqCategory.addPreference(pubPortPreference);

        SerialConfig serialConfig = configRepository.getSerial();

        PreferenceCategory serialCategory = new PreferenceCategory(contextThemeWrapper);
        serialCategory.setKey("serial-config");
        serialCategory.setTitle("Serial config");

        preferenceScreen.addPreference(serialCategory);

        Preference serialPortPreference = new Preference(contextThemeWrapper);
        serialPortPreference.setKey("serial-port");
        serialPortPreference.setTitle("Serial port");
        serialPortPreference.setSummary(serialConfig.getPort());

        serialCategory.addPreference(serialPortPreference);

        Preference serialBaudPreference = new Preference(contextThemeWrapper);
        serialBaudPreference.setKey("serial-baudrate");
        serialBaudPreference.setTitle("Serial baudrate");
        serialBaudPreference.setSummary(String.valueOf(serialConfig.getBaudrate()));

        serialCategory.addPreference(serialBaudPreference);

        GpioConfig gpioConfig = configRepository.getGpio();

        PreferenceCategory gpioCategory = new PreferenceCategory(contextThemeWrapper);
        gpioCategory.setKey("gpio-config");
        gpioCategory.setTitle("GPIO config");

        preferenceScreen.addPreference(gpioCategory);

        Preference gpioPinPreference = new Preference(contextThemeWrapper);
        gpioPinPreference.setKey("gpio-pin");
        gpioPinPreference.setTitle("GPIO Pin");
        gpioPinPreference.setSummary(String.valueOf(gpioConfig.getPin()));

        gpioCategory.addPreference(gpioPinPreference);

        VisionConfig visionConfig = configRepository.getVision();

        PreferenceCategory visionCategory = new PreferenceCategory(contextThemeWrapper);
        visionCategory.setKey("vision-config");
        visionCategory.setTitle("Vision config");

        preferenceScreen.addPreference(visionCategory);

        Preference camPortPreference = new Preference(contextThemeWrapper);
        camPortPreference.setKey("webcam-port");
        camPortPreference.setTitle("Webcam port");
        camPortPreference.setSummary(String.valueOf(visionConfig.getWebcam()));

        visionCategory.addPreference(camPortPreference);

        ServoConfig servosConfig = configRepository.getServos();

        PreferenceCategory servoCategory = new PreferenceCategory(contextThemeWrapper);
        servoCategory.setKey("servos-config");
        servoCategory.setTitle("Servos config");

        preferenceScreen.addPreference(servoCategory);

        for (Wing wing : servosConfig.getWingsList()) {
            Preference wingPreference = new Preference(contextThemeWrapper);
            wingPreference.setTitle("Wing " + wing.getId() + " position: ");
            wingPreference.setSummary(wing.getPosition().name());

            servoCategory.addPreference(wingPreference);
        }

        I2cConfig i2cConfig = configRepository.getI2C();

        PreferenceCategory i2cCategory = new PreferenceCategory(contextThemeWrapper);
        i2cCategory.setKey("i2c-config");
        i2cCategory.setTitle("I2C config");

        preferenceScreen.addPreference(i2cCategory);

        Preference i2cDevicePreference = new Preference(contextThemeWrapper);
        i2cDevicePreference.setKey("i2c-device");
        i2cDevicePreference.setTitle("I2C Device");
        i2cDevicePreference.setSummary(i2cConfig.getDevice());

        i2cCategory.addPreference(i2cDevicePreference);

        MotorControllerConfig motorControllerConfig = configRepository.getMotorController();

        PreferenceCategory motorControllerCategory = new PreferenceCategory(contextThemeWrapper);
        motorControllerCategory.setKey("motor-controller-config");
        motorControllerCategory.setTitle("Motor controller config");

        preferenceScreen.addPreference(motorControllerCategory);

        Preference motorControllerAddressPreference = new Preference(contextThemeWrapper);
        motorControllerAddressPreference.setKey("motor-address");
        motorControllerAddressPreference.setTitle("Motor address");
        motorControllerAddressPreference.setSummary(motorControllerConfig.getAddress());

        for (Motor motor : motorControllerConfig.getMotorsList()) {
            Preference motorPreference = new Preference(contextThemeWrapper);
            motorPreference.setKey("motor-" + motor.getId());
            motorPreference.setTitle("Motor " + motor.getId() + " position: ");
            motorPreference.setSummary(motor.getPosition().name());

            motorControllerCategory.addPreference(motorPreference);
        }

        CommandExecutorConfig commandExecutorConfig = configRepository.getCommandExecutor();

        PreferenceCategory commandExecutorCategory = new PreferenceCategory(contextThemeWrapper);
        commandExecutorCategory.setKey("command-executor-config");
        commandExecutorCategory.setTitle("Command executor config");

        preferenceScreen.addPreference(commandExecutorCategory);

        Preference numExecutorsPreference = new Preference(contextThemeWrapper);
        numExecutorsPreference.setKey("number-executors");
        numExecutorsPreference.setTitle("Number of executors");
        numExecutorsPreference.setSummary(String.valueOf(commandExecutorConfig.getNumberOfExecutors()));

        commandExecutorCategory.addPreference(numExecutorsPreference);

        WatcherConfig watcherConfig = configRepository.getWatcher();

        PreferenceCategory watcherCategory = new PreferenceCategory(contextThemeWrapper);
        watcherCategory.setKey("watcher-config");
        watcherCategory.setTitle("Watcher config");

        preferenceScreen.addPreference(watcherCategory);

        Preference pollingRatePreference = new Preference(contextThemeWrapper);
        pollingRatePreference.setKey("polling-rate");
        pollingRatePreference.setTitle("Polling rate");
        pollingRatePreference.setSummary(String.valueOf(watcherConfig.getPollingRate()));

        watcherCategory.addPreference(pollingRatePreference);

        LoggingConfig loggingConfig = configRepository.getLogging();

        PreferenceCategory loggingCategory = new PreferenceCategory(contextThemeWrapper);
        loggingCategory.setKey("logging-config");
        loggingCategory.setTitle("Logging config");

        preferenceScreen.addPreference(loggingCategory);

        Preference severityLevelPreference = new Preference(contextThemeWrapper);
        severityLevelPreference.setKey("severity-level");
        severityLevelPreference.setTitle("Severity level");
        severityLevelPreference.setSummary(String.valueOf(loggingConfig.getSeverityLevel().name()));

        loggingCategory.addPreference(severityLevelPreference);
    }
}
