package nl.team_goliath.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.zeromq.ZMQ;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import nl.team_goliath.app.interfaces.IPublisher;
import nl.team_goliath.app.protos.MessageCarrierProtos.MessageCarrier;

public class ZMQPublishService extends Service {
    private static final String TAG = ZMQPublishService.class.getName();

    private PublishBinder publishBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        publishBinder = new PublishBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return publishBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (publishBinder != null) {
            publishBinder.disconnect();
            publishBinder = null;
        }
    }

    public class PublishBinder extends Binder implements IPublisher {

        private PublisherThread publisherThread;

        @Override
        public void connect(String address) {
            if (publisherThread == null) {
                publisherThread = new PublisherThread(address);
                new Thread(publisherThread).start();
            }
        }

        @Override
        public void send(MessageCarrier messageCarrier) {
            if (publisherThread != null) {
                publisherThread.send(messageCarrier);
            }
        }

        public void disconnect() {
            if (publisherThread != null) {
                publisherThread.disconnect();
                publisherThread.interrupt();
                publisherThread = null;
            }
        }
    }

    private class PublisherThread extends Thread {
        private String address;

        private BlockingQueue<MessageCarrier> messages = new ArrayBlockingQueue<>(1024);

        private ZMQ.Context zContext;
        private ZMQ.Socket zSocket;

        PublisherThread(String address) {
            this.address = address;
        }

        public void send(MessageCarrier messageCarrier) {
            messages.add(messageCarrier);
        }

        @Override
        public void run() {
            if (connect()) {
                push();
                disconnect();
            }
        }

        private boolean connect() {
            zContext = ZMQ.context(1);
            zSocket = zContext.socket(ZMQ.PUB);

            boolean result = zSocket.connect(address);
            if (result) {
                Log.d(TAG, address + " connected");
            } else {
                Log.d(TAG, "Failed to connect");
            }

            return result;
        }

        private void push() {
            while (!isInterrupted()) {
                try {
                    MessageCarrier message = messages.take();
                    zSocket.sendMore("" + message.getMessageCase().getNumber());
                    zSocket.send(message.toByteArray(), 0);
                    Log.v(TAG, "Publisher send: " + message.toString());
                } catch (InterruptedException ignored) {
                }
            }
        }

        private void disconnect() {
            if (zSocket != null) {
                zSocket.disconnect(address);
                zSocket = null;
            }
            Log.i(TAG, "push: " + address + " disconnected");
        }
    }
}