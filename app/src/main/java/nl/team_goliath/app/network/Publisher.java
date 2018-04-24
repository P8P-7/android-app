package nl.team_goliath.app.network;

import android.util.Log;

import org.zeromq.ZMQ;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import idp.MessageOuterClass.Message;

public class Publisher extends Thread implements Runnable {
    private static final String TAG = Publisher.class.getSimpleName();

    private String address;
    private BlockingQueue<Message> messages;

    public Publisher(String address) {
        this.address = address;
        messages = new ArrayBlockingQueue<>(1024);
    }

    public void sendMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void run() {
        super.run();

        ZMQ.Context ctx = ZMQ.context(1);
        ZMQ.Socket publisher = ctx.socket(ZMQ.PUB);
        publisher.connect(address);

        while (!Thread.interrupted()) {
            try {
                Message message = messages.take();
                publisher.sendMore(message.getChannel().getBytes());
                publisher.send(message.getData().toByteArray(), 0);
                Log.v(TAG, "Publisher send: " + message.getData().getContent());
            } catch (InterruptedException ignored) {
            }
        }
    }
}