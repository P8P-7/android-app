package nl.team_goliath.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import nl.team_goliath.app.interfaces.IMessageListener;
import nl.team_goliath.app.protos.IoConfigProtos.IoConfig;
import nl.team_goliath.app.protos.MessageProtos.CommandMessage;
import nl.team_goliath.app.protos.MessageProtos.ConfigMessage;
import nl.team_goliath.app.protos.MessageProtos.Message;
import nl.team_goliath.app.protos.MoveCommandProtos.MoveCommand;
import nl.team_goliath.app.protos.StatsProtos.Stats;
import nl.team_goliath.app.protos.VisionConfigProtos.VisionConfig;
import nl.team_goliath.app.services.ZMQPublishService;
import nl.team_goliath.app.services.ZMQSubscribeService;

public class MainActivity extends AppCompatActivity implements IMessageListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * TCP Addresses
     */
    private static String SUB_ADDRESS;
    private static String PUB_ADDRESS;

    /**
     * Binders
     */
    private ZMQSubscribeService.SubscribeBinder subscribeBinder = null;
    private ZMQPublishService.PublishBinder publishBinder = null;

    /**
     * Flags indicating whether we have called bind on the service.
     */
    private boolean subscribeBound = false;
    private boolean publishBound = false;

    /**
     * Classes for interacting with the main interface of the service.
     */
    private ServiceConnection subscribeServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            subscribeBinder = (ZMQSubscribeService.SubscribeBinder) service;
            if (subscribeBinder != null) {
                subscribeBinder.connect(SUB_ADDRESS);

                // TODO Let users define on which channel they want to subscribe.
                subscribeBinder.subscribe(Message.DataCase.COMMAND, MainActivity.this);
                subscribeBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            subscribeBinder = null;
            subscribeBound = false;
        }
    };

    private ServiceConnection publishServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            publishBinder = (ZMQPublishService.PublishBinder) service;
            if (publishBinder != null) {
                publishBinder.connect(PUB_ADDRESS);
                publishBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            publishBinder = null;
            publishBound = false;
        }
    };

    private TextView textViewAngleLeft;
    private TextView textViewStrengthLeft;

    private TextView textViewAngleRight;
    private TextView textViewStrengthRight;
    private TextView textViewCoordinateRight;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        textViewAngleLeft = findViewById(R.id.textView_angle_left);
        textViewStrengthLeft = findViewById(R.id.textView_strength_left);

        JoystickView joystickLeft = findViewById(R.id.joystickView_left);
        joystickLeft.setOnMoveListener((angle, strength) -> {
            textViewAngleLeft.setText(getString(R.string.angle, angle));
            textViewStrengthLeft.setText(getString(R.string.strength, strength));
            sendMoveCommand(angle, strength);
        });

        textViewAngleRight = findViewById(R.id.textView_angle_right);
        textViewStrengthRight = findViewById(R.id.textView_strength_right);
        textViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

        final JoystickView joystickRight = findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener((angle, strength) -> {
            textViewAngleRight.setText(getString(R.string.angle, angle));
            textViewStrengthRight.setText(getString(R.string.strength, strength));
            textViewCoordinateRight.setText(getString(R.string.coordinate, joystickRight.getNormalizedX(), joystickRight.getNormalizedY()));
            sendMoveCommand(angle, strength);
        });

        SUB_ADDRESS = prefs.getString("sub_address", getString(R.string.pref_default_sub_address));
        PUB_ADDRESS = prefs.getString("pub_address", getString(R.string.pref_default_pub_address));

        SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs1, key) -> {
            String value = prefs1.getString(key, "");

            switch (key) {
                case "sub_address":
                    SUB_ADDRESS = value;
                    subscribeBinder.disconnect();
                    subscribeBinder.connect(SUB_ADDRESS);
                    break;
                case "pub_address":
                    PUB_ADDRESS = value;
                    publishBinder.disconnect();
                    publishBinder.connect(PUB_ADDRESS);
                    break;
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    private void sendMoveCommand(int direction, int speed) {
        if (!publishBound) return;

        MoveCommand move = MoveCommand.newBuilder()
                .setDirection(direction)
                .setSpeed(speed)
                .build();

        CommandMessage command = CommandMessage.newBuilder()
                .setMoveCommand(move)
                .build();

        Message message = Message.newBuilder()
                .setCommand(command)
                .build();

        publishBinder.send(message);
    }

    private static String getTimeString() {
        return DATE_FORMAT.format(new Date());
    }

    @Override
    public void onMessageReceived(String channel, Message message) {
        switch (message.getDataCase()) {
            case COMMAND:
                CommandMessage commandMessage = message.getCommand();

                if (commandMessage.getCommandCase() == CommandMessage.CommandCase.MOVECOMMAND) {
                    MoveCommand moveCommand = commandMessage.getMoveCommand();

                    Log.d(TAG, getTimeString() + " - client received [" + channel + "] speed: " +
                            moveCommand.getSpeed()
                            + " direction: " +
                            moveCommand.getDirection()
                    );
                }
                break;
            case CONFIG:
                ConfigMessage configMessage = message.getConfig();

                if (configMessage.getConfigCase() == ConfigMessage.ConfigCase.IOCONFIG) {
                    IoConfig ioConfig = configMessage.getIoConfig();

                    Log.d(TAG, getTimeString() + " - client received [" + channel + "] ip: " +
                            ioConfig.getPublisherIp()
                            + " port: " +
                            ioConfig.getPublisherPort()
                    );
                } else if (configMessage.getConfigCase() == ConfigMessage.ConfigCase.VISIONCONFIG) {
                    VisionConfig visionConfig = configMessage.getVisionConfig();

                    Log.d(TAG, getTimeString() + " - client received [" + channel + "] camera enabled: " +
                            visionConfig.getCameraEnabled()
                    );
                }
                break;
            case STATS:
                Stats stats = message.getStats();

                Log.d(TAG, getTimeString() + " - client received [" + channel + "] total cpu usage: " +
                        stats.getCpuUsage().getTotalUsage()
                );
                break;
            case DATA_NOT_SET:
            default:
                Log.d(TAG, "Data not set");
        }

    }

    @Override
    public void onError(String message) {
        Log.e(TAG, getTimeString() + " - client error: " + message);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Bind to the services
        bindService(new Intent(this, ZMQSubscribeService.class), subscribeServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, ZMQPublishService.class), publishServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subscribeBinder != null) {
            subscribeBinder.unsubscribe(Message.DataCase.STATS);
            subscribeBinder.disconnect();
        }
        if (publishBinder != null) {
            publishBinder.disconnect();
        }

        unbindService(subscribeServiceConnection);
        unbindService(publishServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
