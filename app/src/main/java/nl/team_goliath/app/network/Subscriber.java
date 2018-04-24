package nl.team_goliath.app.network;

import android.os.Handler;
import android.util.Log;

import nl.team_goliath.app.Util;
import com.google.protobuf.InvalidProtocolBufferException;

import org.zeromq.ZMQ;

import java.util.concurrent.CountDownLatch;

import idp.MessageOuterClass.Content;

public class Subscriber extends Thread implements Runnable {
    private static final String TAG = Subscriber.class.getSimpleName();

    private String address;
    private String channel;
    private ZMQ.Socket mulServiceSubscriber;
    private Handler handler;

    /**
     * Countdown latch
     */
    private CountDownLatch lock = new CountDownLatch(1);

    public Subscriber(String address) {
        this.address = address;
    }

    public void setMessageHandler(Handler handler) {
        this.handler = handler;
    }

    public void subscribe(final String channel) {
        this.channel = channel;
        new Thread() {
            @Override
            public void run() {
                try {
                    lock.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mulServiceSubscriber.subscribe(channel.getBytes());
            }
        }.start();
    }

    public void unsubscribe() {
        new Thread() {
            @Override
            public void run() {
                mulServiceSubscriber.unsubscribe(channel.getBytes());
            }
        }.start();
    }

    @Override
    public void run() {
        super.run();

        ZMQ.Context ctx = ZMQ.context(1);
        mulServiceSubscriber = ctx.socket(ZMQ.SUB);
        mulServiceSubscriber.connect(address);

        lock.countDown();

        while (!Thread.interrupted()) {
            String channel = mulServiceSubscriber.recvStr();
            byte[] reply = mulServiceSubscriber.recv(0);
            Content data = null;
            try {
                data = Content.parseFrom(reply);
            } catch (InvalidProtocolBufferException ignored) {
            }
            if (data != null && handler != null) {
                Log.v(TAG, "Subscriber receive. Channel: " + channel + " Data: " + data.getContent());
                handler.sendMessage(Util.bundledMessage(handler, data));
            }
        }
    }
}