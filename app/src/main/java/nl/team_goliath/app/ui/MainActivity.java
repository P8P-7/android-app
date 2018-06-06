package nl.team_goliath.app.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.protobuf.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import nl.team_goliath.app.GoliathApp;
import nl.team_goliath.app.R;
import nl.team_goliath.app.manager.EventDispatcher;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.model.MessageListener;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier;
import nl.team_goliath.app.service.ZMQPublishService;
import nl.team_goliath.app.service.ZMQSubscribeService;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MessageListener, CommandSender, PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
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
                subscribeBinder.subscribe(MessageCarrier.MessageCase.SYNCHRONIZEMESSAGE, MainActivity.this);
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

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.US);

    private EventDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dispatcher = GoliathApp.getEventDispatcher();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            selectFragment(item.getItemId());

            return true;
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            selectFragment(R.id.action_control);
        }
    }

    @Override
    public void sendCommand(CommandMessage commandMessage) {
        if (!publishBound) return;

        MessageCarrier message = MessageCarrier.newBuilder()
                .setCommandMessage(commandMessage)
                .build();

        publishBinder.send(message);
    }

    private static String getTimeString() {
        return DATE_FORMAT.format(new Date());
    }

    @Override
    public void onMessageReceived(Message message) {
        MessageCarrier messageCarrier = (MessageCarrier) message;

        MessageCarrier.MessageCase messageCase = messageCarrier.getMessageCase();

        MessageListener handler;
        if ((handler = dispatcher.getHandlerForMessageCase(messageCase)) != null) {
            switch (messageCase) {
                case COMMANDMESSAGE:
                    handler.onMessageReceived(messageCarrier.getCommandMessage());
                    break;
                case EMOTIONMESSAGE:
                    handler.onMessageReceived(messageCarrier.getEmotionMessage());
                    break;
                case SYNCHRONIZEMESSAGE:
                    handler.onMessageReceived(messageCarrier.getSynchronizeMessage());
                    break;
            }
        }
    }

    @Override
    public void onError(String message) {
        Timber.e(TAG, "%s - client error:  %s", getTimeString(), message);
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
            subscribeBinder.unsubscribe(MessageCarrier.MessageCase.SYNCHRONIZEMESSAGE);
            subscribeBinder.disconnect();
        }
        if (publishBinder != null) {
            publishBinder.disconnect();
        }

        unbindService(subscribeServiceConnection);
        unbindService(publishServiceConnection);
    }

    private void selectFragment(int id) {
        Fragment frag = null;

        // Init corresponding fragment
        switch (id) {
            case R.id.action_control:
                frag = ControlFragment.newInstance();
                break;
            case R.id.action_presets:
                frag = PresetsFragment.newInstance();
                break;
            case R.id.action_statistics:
                frag = StatisticsFragment.newInstance();
                break;
            case R.id.action_settings:
                frag = PreferenceFragment.newInstance();
                break;
        }

        if (frag != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, frag)
                    .commitNow();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());

        PreferenceFragment fragment = PreferenceFragment.newInstance();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, preferenceScreen.getKey())
                .addToBackStack(preferenceScreen.getKey())
                .commit();
        return true;
    }
}
