package nl.team_goliath.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.team_goliath.app.network.MessageListenerHandler;
import nl.team_goliath.app.network.Publisher;
import nl.team_goliath.app.network.Subscriber;
import nl.team_goliath.app.protos.CommandMessageProtos.Command;
import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;
import nl.team_goliath.app.protos.ConfigCommandProtos.ConfigCommand;
import nl.team_goliath.app.protos.MoveCommandProtos.MoveCommand;

public class MainActivity extends AppCompatActivity {
    private static CommandMessage.Channel PUB_CHANNEL;
    private static Command.CommandCase COMMAND = Command.CommandCase.CONFIGCOMMAND;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private TextView textView;
    private EditText messageEditText;

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

        textView = findViewById(R.id.console);
        textView.setMovementMethod(new ScrollingMovementMethod());

        messageEditText = findViewById(R.id.message);

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (COMMAND.equals(Command.CommandCase.COMMAND_NOT_SET)) {
                Toast.makeText(getApplicationContext(), R.string.command_not_set_error, Toast.LENGTH_LONG).show();
                return;
            }

            Command command;
            switch (COMMAND) {
                case MOVECOMMAND:
                    MoveCommand move = MoveCommand.newBuilder()
                            .setDirection(1)
                            .setSpeed(Integer.parseInt(messageEditText.getText().toString()))
                            .build();
                    command = Command.newBuilder()
                            .setMoveCommand(move)
                            .build();
                    break;
                case CONFIGCOMMAND:
                default:
                    ConfigCommand config = ConfigCommand.newBuilder()
                            .setName("test")
                            .setValue(messageEditText.getText().toString())
                            .build();
                    command = Command.newBuilder()
                            .setConfigCommand(config)
                            .build();
                    break;
            }

            CommandMessage send = CommandMessage.newBuilder()
                    .setChannel(PUB_CHANNEL)
                    .setCommand(command)
                    .build();

            publisher.sendMessage(send);
        });
    }

    private static String getTimeString() {
        return DATE_FORMAT.format(new Date());
    }

    private void clientMessageReceived(String messageBody) {
        textView.append(getTimeString() + " - client received: " + messageBody + "\n");
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
