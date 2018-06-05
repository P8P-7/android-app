package nl.team_goliath.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.google.protobuf.InvalidProtocolBufferException;

import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nl.team_goliath.app.model.MessageListener;
import nl.team_goliath.app.model.Subscriber;
import nl.team_goliath.app.proto.MessageCarrierProto.MessageCarrier;
import timber.log.Timber;

public class ZMQSubscribeService extends Service {
    private SubscribeBinder subscribeBinder;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        subscribeBinder = new SubscribeBinder();
        handler = new Handler(getMainLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return subscribeBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribeBinder != null) {
            subscribeBinder.disconnect();
            subscribeBinder = null;
        }
    }

    public class SubscribeBinder extends Binder implements Subscriber {

        private SubscriberThread subscriberThread;

        @Override
        public void connect(String address) {
            if (subscriberThread == null) {
                subscriberThread = new SubscriberThread(address);
                new Thread(subscriberThread).start();
            }
        }

        @Override
        public void subscribe(@NonNull MessageCarrier.MessageCase key, @NonNull MessageListener listener) {
            if (subscriberThread != null) {
                subscriberThread.subscribe(key, listener);
            }
        }

        @Override
        public void unsubscribe(@NonNull MessageCarrier.MessageCase key) {
            if (subscriberThread != null) {
                subscriberThread.unsubscribe(key);
            }
        }

        public void disconnect() {
            if (subscriberThread != null) {
                subscriberThread.disconnect();
                subscriberThread.interrupt();
                subscriberThread = null;
            }
        }
    }

    private class SubscriberThread extends Thread {
        private String address;

        private Map<MessageCarrier.MessageCase, MessageListener> listeners = new HashMap<>();
        private ZMQ.Context zContext;
        private ZMQ.Socket zSocket;

        SubscriberThread(String address) {
            this.address = address;
        }

        void subscribe(@NonNull MessageCarrier.MessageCase key, @NonNull MessageListener listener) {
            listeners.put(key, listener);
        }

        void unsubscribe(@NonNull MessageCarrier.MessageCase key) {
            listeners.remove(key);
        }

        @Override
        public void run() {
            if (connect()) {
                try {
                    poll();
                } catch (InvalidProtocolBufferException e) {
                    Timber.e(e, "poll: Failed to decode data");
                }
                disconnect();
            }
        }

        private boolean connect() {
            zContext = ZMQ.context(1);
            zSocket = zContext.socket(ZMQ.SUB);
            zSocket.subscribe("".getBytes());

            boolean result = zSocket.connect(address);
            if (result) {
                Timber.d("%s connected", address);
            } else {
                broadcastConnectError();
            }

            return result;
        }

        private void poll() throws InvalidProtocolBufferException {
            ZMQ.Poller poller = zContext.poller(1);
            poller.register(zSocket, ZMQ.Poller.POLLIN);
            while (!isInterrupted()) {
                int poll = poller.poll();
                if (poll == ZMQ.Poller.POLLIN) {
                    if (poller.pollin(0)) {
                        String channel = zSocket.recvStr(0);
                        byte[] received = zSocket.recv(0);

                        MessageCarrier message = MessageCarrier.parseFrom(received);
                        MessageListener subscriber;

                        if (message != null && (subscriber = listeners.get(message.getMessageCase())) != null) {
                            handler.post(() -> subscriber.onMessageReceived(message));
                        }
                    }
                }
            }
        }

        private void disconnect() {
            if (zSocket != null) {
                zSocket.disconnect(address);
                zSocket = null;
            }
            Timber.i("poll: %s disconnected", address);
        }

        private void broadcastConnectError() {
            for (Map.Entry<MessageCarrier.MessageCase, MessageListener> entry : listeners.entrySet()) {
                handler.post(() -> {
                    MessageListener subscriber = entry.getValue();
                    if (subscriber != null) {
                        subscriber.onError("Failed to connect");
                    }
                });
            }
        }
    }
}