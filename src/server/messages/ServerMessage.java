package server.messages;

import server.config.Config;
import server.room.PlayerColor;
import server.room.board.BoardFieldType;

public class ServerMessage {

    private ServerMessage() {
        throw new IllegalStateException("Utility class");
    }

    public static String gameStarted() {
        StringBuilder sb = new StringBuilder("GAME STARTED");
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String gameClosed() {
        StringBuilder sb = new StringBuilder("GAME CLOSED");
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String color(PlayerColor color) {
        StringBuilder sb = new StringBuilder("COLOR ");
        sb.append(color);
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String turn(PlayerColor color) {
        StringBuilder sb = new StringBuilder("TURN ");
        sb.append(color);
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String thrown(PlayerColor color, int value) {
        StringBuilder sb = new StringBuilder(color.toString());
        sb.append(" THROWN ");
        sb.append(value);
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String pawnMove(PlayerColor color, int pawnIndex, BoardFieldType fieldType, Integer fieldIndex) {
        StringBuilder sb = new StringBuilder("MOVE ");
        sb.append(color.toString());
        sb.append(" PAWN ");
        sb.append(pawnIndex);
        sb.append(" TO ");
        sb.append(fieldType.toString());
        sb.append(" ");
        sb.append(fieldIndex);
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

    public static String playerWins(PlayerColor color) {
        StringBuilder sb = new StringBuilder("WIN ");
        sb.append(color.toString());
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

}
