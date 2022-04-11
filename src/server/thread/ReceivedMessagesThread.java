package server.thread;

import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import server.IClient;
import server.config.Config;
import server.messages.ErrorMessages;
import server.messages.ReceivedMessage;
import server.messages.ReceivedMessageType;
import server.messages.ServerMessageFactory;
import server.room.Player;

public final class ReceivedMessagesThread implements Runnable {

    private final Selector selector;
    private final Queue<ReceivedMessage> queue;

    public ReceivedMessagesThread(Selector s, Queue<ReceivedMessage> q) {
        selector = s;
        queue = q;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ReceivedMessage message = ((ArrayBlockingQueue<ReceivedMessage>) queue).take();
                IClient sender = message.sender();
                if (message.messageType() == ReceivedMessageType.INVALID) {
                    sender.send(ServerMessageFactory.msg(ErrorMessages.INVALID_MESSAGE));
                }
                else if (sender instanceof Player) {
                    ((Player) sender).room().handleReceivedMessage(message);
                }
                else sender.send(ServerMessageFactory.msg(ErrorMessages.NOT_IN_GAME));
                selector.wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Config.RECEIVED_MESSAGES_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
