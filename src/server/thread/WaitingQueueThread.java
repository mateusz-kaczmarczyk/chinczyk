package server.thread;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Map;
import java.util.Queue;

import server.IClient;
import server.config.Config;
import server.room.Player;
import server.room.PlayerColor;
import server.room.Room;

public final class WaitingQueueThread implements Runnable {

    private final Selector selector;
    private final Map<SelectionKey, IClient> clients;
    private final Queue<IClient> queue;
    private final Queue<Room> rooms;

    public WaitingQueueThread(Selector s, Map<SelectionKey, IClient> c, Queue<IClient> q, Queue<Room> r) {
        selector = s;
        clients = c;
        queue = q;
        rooms = r;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (queue.size() >= Config.ROOM_SIZE) {
                Room room = new Room();
                PlayerColor color = PlayerColor.randomColor();
                for (int i = 0; i < Config.ROOM_SIZE; i++) {
                    IClient player = queue.poll();
                    player = new Player(player, room, color);
                    room.addPlayer((Player) player);
                    clients.put(player.getKey(), player);
                    color = color.next();
                }
                rooms.add(room);
                room.start();
                selector.wakeup();
            }
            try {
                Thread.sleep(Config.WAITING_QUEUE_THREAD_SLEEP);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
