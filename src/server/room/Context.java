package server.room;

import server.IClient;
import server.messages.ReceivedMessage;
import server.messages.ReceivedMessageType;

public class Context {

    public final ReceivedMessageType messageType;
    public final IClient sender;
    public final String chatMessage;
    public final int pawnIndex;
    public final Room room;
    public final PlayerColor color;
    public final Integer thrown;

    public Context(ReceivedMessage message, Room room, PlayerColor color, Integer thrown) {
        sender = message.sender();
        pawnIndex = message.pawnIndex();
        chatMessage = message.chatMessage();
        messageType = message.messageType();
        this.room = room;
        this.color = color;
        this.thrown = thrown;
    }
    
}
