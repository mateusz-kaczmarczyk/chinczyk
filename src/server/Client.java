package server;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.UUID;

import server.messages.Utils;
import server.room.Player;

public class Client implements IClient {

    private String uuid;
    protected SelectionKey key;

    public Client(SelectionKey key) {
        this.key = key;
        uuid = UUID.randomUUID().toString();
    }

    public String getId() {
        return uuid;
    }

    public void send(String message) {
        printMessage(message);
        ByteBuffer buffer = Utils.toByteBuffer(message);
        ((Queue<Buffer>) key.attachment()).add(buffer);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public SelectionKey getKey() {
        return key;
    }

    private void printMessage(String message) {
        System.out.print(Thread.currentThread().getName() + ": ");
        if (this instanceof Player) {
            Player player = (Player) this;
            System.out.print("Room::" + player.room().uuid());
            System.out.print(" Player::" + this.uuid);
        }
        System.out.println(" Sending \"" + message + "\"");
    }

}
