package nl.team_goliath.app.network;

import android.util.Log;

import org.zeromq.ZMQ;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;

public class Publisher extends Thread implements Runnable {
    private static final String TAG = Publisher.class.getSimpleName();

    private String address;
    private BlockingQueue<CommandMessage> messages;

    public Publisher(String address) {
        this.address = address;
        messages = new ArrayBlockingQueue<>(1024);
    }

    public void sendMessage(CommandMessage message) {
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
                CommandMessage message = messages.take();
                publisher.sendMore(String.valueOf(message.getChannel().getNumber()));
                publisher.send(message.getCommand().toByteArray(), 0);
                Log.v(TAG, "Publisher send: " + message.getCommand().getCommandCase());
            } catch (InterruptedException ignored) {
            }
        }
    }
}