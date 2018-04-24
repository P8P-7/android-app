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

import nl.team_goliath.app.network.MessageListenerHandler;
import nl.team_goliath.app.network.Publisher;
import nl.team_goliath.app.network.Subscriber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import idp.MessageOuterClass.Message;
import idp.MessageOuterClass.Content;

public class MainActivity extends AppCompatActivity {
    private static String PUB_CHANNEL;

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

        subscriber.subscribe(prefs.getString("sub_channel", "default"));

        publisher = new Publisher(prefs.getString("pub_address", getString(R.string.pref_default_pub_address)));
        publisher.start();

        PUB_CHANNEL = prefs.getString("pub_channel", "default");

        listener = (prefs1, key) -> {
            String value = prefs1.getString(key, "");

            switch (key) {
                case "sub_address":
                    subscriber.interrupt();
                    subscriber = new Subscriber(value);
                    subscriber.start();

                    subscriber.subscribe(prefs1.getString("sub_channel", "default"));
                    break;
                case "pub_address":
                    publisher.interrupt();
                    publisher = new Publisher(value);
                    publisher.start();
                    break;
                case "sub_channel":
                    subscriber.unsubscribe();
                    subscriber.subscribe(value);
                    break;
                case "pub_channel":
                    PUB_CHANNEL = value;
                    break;

            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Content data = Content.newBuilder()
                    .setContent(messageEditText.getText().toString())
                    .build();

            Message send = Message.newBuilder()
                    .setChannel(PUB_CHANNEL)
                    .setData(data)
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
