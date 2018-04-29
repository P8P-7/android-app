package nl.team_goliath.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import nl.team_goliath.app.network.MessageListenerHandler;
import nl.team_goliath.app.network.Publisher;
import nl.team_goliath.app.network.Subscriber;
import nl.team_goliath.app.protos.CommandMessageProtos.Command;
import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;
import nl.team_goliath.app.protos.MoveCommandProtos.MoveCommand;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static CommandMessage.Channel PUB_CHANNEL;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private TextView textViewAngleLeft;
    private TextView textViewStrengthLeft;

    private TextView textViewAngleRight;
    private TextView textViewStrengthRight;
    private TextView textViewCoordinateRight;

    private Publisher publisher;
    private Subscriber subscriber;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.US);

    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(this::clientMessageReceived, Util.MESSAGE_PAYLOAD_KEY);

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
            // sendMoveCommand(angle, strength);
        });

        textViewAngleRight = findViewById(R.id.textView_angle_right);
        textViewStrengthRight = findViewById(R.id.textView_strength_right);
        textViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

        final JoystickView joystickRight = findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener((angle, strength) -> {
            textViewAngleRight.setText(getString(R.string.angle, angle));
            textViewStrengthRight.setText(getString(R.string.strength, strength));
            textViewCoordinateRight.setText(getString(R.string.coordinate, joystickRight.getNormalizedX(), joystickRight.getNormalizedY()));
            // sendMoveCommand(angle, strength);
        });

        subscriber = new Subscriber(prefs.getString("sub_address", getString(R.string.pref_default_sub_address)));
        subscriber.setMessageHandler(clientMessageHandler);
        subscriber.start();

        subscriber.subscribe(CommandMessage.Channel.valueOf(prefs.getString("sub_channel", CommandMessage.Channel.DEFAULT.name())));

        publisher = new Publisher(prefs.getString("pub_address", getString(R.string.pref_default_pub_address)));
        publisher.start();

        PUB_CHANNEL = CommandMessage.Channel.valueOf(prefs.getString("pub_channel", CommandMessage.Channel.DEFAULT.name()));

        listener = (prefs1, key) -> {
            String value = prefs1.getString(key, "");

            switch (key) {
                case "sub_address":
                    subscriber.interrupt();
                    subscriber = new Subscriber(value);
                    subscriber.start();

                    subscriber.subscribe(CommandMessage.Channel.valueOf(prefs1.getString("sub_channel", CommandMessage.Channel.DEFAULT.name())));
                    break;
                case "pub_address":
                    publisher.interrupt();
                    publisher = new Publisher(value);
                    publisher.start();
                    break;
                case "sub_channel":
                    subscriber.unsubscribe();
                    subscriber.subscribe(CommandMessage.Channel.valueOf(value));
                    break;
                case "pub_channel":
                    PUB_CHANNEL = CommandMessage.Channel.valueOf(value);
                    break;

            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    /*
     * TODO
     */
    private void sendMoveCommand(int direction, int speed) {
        MoveCommand move = MoveCommand.newBuilder()
                .setDirection(direction)
                .setSpeed(speed)
                .build();

        Command command = Command.newBuilder()
                .setMoveCommand(move)
                .build();

        CommandMessage send = CommandMessage.newBuilder()
                .setChannel(PUB_CHANNEL)
                .setCommand(command)
                .build();

        publisher.sendMessage(send);
    }

    private static String getTimeString() {
        return DATE_FORMAT.format(new Date());
    }

    private void clientMessageReceived(String messageBody) {
        Log.d(TAG, getTimeString() + " - client received: " + messageBody + "\n");
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
