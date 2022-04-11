package server.room;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import server.messages.ErrorMessages;
import server.messages.ReceivedMessage;
import server.messages.ReceivedMessageType;
import server.messages.ServerMessage;
import server.messages.ServerMessageFactory;
import server.room.board.Board;
import server.room.handlers.Handler;
import server.room.handlers.ThrowHandler;

public class Room {
    
    private String uuid;
    private Queue<Player> queue;
    private Board board;
    private Integer thrown = null;
    private Handler throwHandler;

    public Room() {
        board = new Board();
        queue = new LinkedList<>();
        uuid = UUID.randomUUID().toString();
        throwHandler = new ThrowHandler(board);
    }

    public void addPlayer(Player player) {
        queue.add(player);
    }

    public void start() {
        for (Player player : queue) {
            player.send(ServerMessage.gameStarted());
            player.send(ServerMessage.color(player.color()));
        }
        notifyTurn();
    }

    private Player currentTurn() {
        return queue.peek();
    }

    public void nextTurn() {
        Player player = queue.poll();
        queue.add(player);
        thrown = null;
        notifyTurn();
    }

    private void notifyTurn() {
        Player currentTurn = currentTurn();
        for (Player player : queue) {
            player.send(ServerMessage.turn(currentTurn.color()));
        }
    }

    public void send(String message) {
        for (Player player : queue) {
            player.send(message);
        }
    }

    public String uuid() {
        return uuid;
    }

    public void setThrown(Integer value) {
        thrown = value;
    }

    public void handleReceivedMessage(ReceivedMessage message) {
        Player turn = this.currentTurn();
        Context context = new Context(message, this, turn.color(), thrown);
        if (turn != context.sender) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.NOT_YOUR_TURN));
        }
        else if (context.messageType == ReceivedMessageType.PASS) {
            nextTurn();
        }
        else if (context.messageType == ReceivedMessageType.THROW) {
            throwHandler.handle(context);
        }
        else if (thrown == null && context.messageType == ReceivedMessageType.MOVE) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.NOT_THROWN));
        }
        else {
            board.handlePawnMove(context);
        }
    }

}
