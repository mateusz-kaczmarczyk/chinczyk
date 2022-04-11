package server.messages;

import server.config.Config;
import server.IClient;

public class ReceivedMessage {

    private final IClient sender;
    private int pawnIndex;
    private String chatMessage;
    private ReceivedMessageType messageType = ReceivedMessageType.INVALID;

    public ReceivedMessage(IClient client, String message) {
        sender = client;
        if (!message.endsWith(Config.MESSAGE_END_CHAR)) {
            return;
        }
        message = message.substring(0, message.length() - 1);
        String[] parts = message.split(" ");
        if (parts[0].equals("SAY") && parts.length > 1) {
            chatMessage = message.substring(4);
            messageType = ReceivedMessageType.SAY;
        } else if (parts[0].equals("THROW") && parts.length == 1) {
            messageType = ReceivedMessageType.THROW;
        } else if (parts[0].equals("PASS") && parts.length == 1) {
            messageType = ReceivedMessageType.PASS;
        } else if (parts[0].equals("MOVE") && parts.length == 2) {
            try {
                pawnIndex = Integer.parseInt(parts[1]);
                if (pawnIndex >= 1 && pawnIndex <= Config.MAX_PLAYERS) {
                    messageType = ReceivedMessageType.MOVE;
                }
            } catch (Exception e) {
                messageType = ReceivedMessageType.INVALID;
            }
        }
    }

    public int pawnIndex() {
        return pawnIndex;
    }

    public IClient sender() {
        return sender;
    }

    public ReceivedMessageType messageType() {
        return messageType;
    }

    public String chatMessage() {
        return chatMessage;
    }

}
