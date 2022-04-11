package server.room;

import server.Client;
import server.IClient;

public class Player extends Client {

    protected final Room room;
    private final PlayerColor color;

    public Player(IClient client, Room room, PlayerColor color) {
        super(client.getKey());
        this.room = room;
        this.color = color;
    }

    public Room room() {
        return room;
    }

    public PlayerColor color() {
        return color;
    }

}
