package nl.team_goliath.app.network;

import android.os.Handler;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import org.zeromq.ZMQ;

import java.util.concurrent.CountDownLatch;

import nl.team_goliath.app.Util;
import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;
import nl.team_goliath.app.protos.CommandMessageProtos.Command;
import nl.team_goliath.app.protos.ConfigCommandProtos.ConfigCommand;
import nl.team_goliath.app.protos.MoveCommandProtos.MoveCommand;

public class Subscriber extends Thread implements Runnable {
    private static final String TAG = Subscriber.class.getSimpleName();

    private String address;
    private CommandMessage.Channel channel;
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

    public void subscribe(final  CommandMessage.Channel channel) {
        this.channel = channel;
        new Thread() {
            @Override
            public void run() {
                try {
                    lock.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mulServiceSubscriber.subscribe(String.valueOf(channel.getNumber()));
            }
        }.start();
    }

    public void unsubscribe() {
        new Thread() {
            @Override
            public void run() {
                mulServiceSubscriber.unsubscribe(String.valueOf(channel.getNumber()));
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
            Command command = null;
            try {
                command = Command.parseFrom(reply);
            } catch (InvalidProtocolBufferException ignored) {
            }
            if (command != null && handler != null) {
                switch (command.getCommandCase()) {
                    case MOVECOMMAND:
                        MoveCommand moveCommand = command.getMoveCommand();


                        Log.v(TAG, "[" + channel + "] [ move ] speed: " +
                                moveCommand.getSpeed() +
                                " direction: " +
                                moveCommand.getDirection());
                        break;
                    case CONFIGCOMMAND:
                        ConfigCommand configCommand = command.getConfigCommand();


                        Log.v(TAG, "[" + channel + "] [ config ] name: " +
                                configCommand.getName() +
                                " value: " +
                                configCommand.getValue());
                        break;
                    case COMMAND_NOT_SET:
                    default:
                        Log.v(TAG, "Command not set");
                }
                handler.sendMessage(Util.bundledMessage(handler, command));
            }
        }
    }
}