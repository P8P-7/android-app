package nl.team_goliath.app;

import android.app.Application;

import nl.team_goliath.app.manager.EventDispatcher;
import timber.log.Timber;

/**
 * Android Application class. Used for accessing singletons.
 */
public class GoliathApp extends Application {

    private static EventDispatcher eventDispatcherInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static EventDispatcher getEventDispatcher() {
        if (eventDispatcherInstance == null) {
            synchronized (EventDispatcher.class) {
                if (eventDispatcherInstance == null) {
                    eventDispatcherInstance = new EventDispatcher();
                }
            }
        }
        return eventDispatcherInstance;
    }
}