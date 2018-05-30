package nl.team_goliath.app;

import android.app.Application;

import nl.team_goliath.app.manager.EventDispatcher;

/**
 * Android Application class. Used for accessing singletons.
 */
public class GoliathApp extends Application {

    private static EventDispatcher eventDispatcherInstance;

    @Override
    public void onCreate() {
        super.onCreate();
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