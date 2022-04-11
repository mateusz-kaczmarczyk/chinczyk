package server.room.handlers;

import java.util.concurrent.ThreadLocalRandom;

import server.config.Config;
import server.messages.ErrorMessages;
import server.messages.ServerMessage;
import server.messages.ServerMessageFactory;
import server.room.Context;
import server.room.PlayerColor;
import server.room.board.Board;
import server.room.board.BoardFieldType;
import server.room.board.Pawn;

public class ThrowHandler extends Handler {

    public ThrowHandler(Board board) {
        super(board);
    }

    @Override
    public void handle(Context context) {
        Integer thrown = context.thrown;
        if (thrown != null) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.ALREADY_THROWN));
            return;
        }
        thrown = allPawnsAreInBase(context.color) ?
            throwDice(Config.PAWNS_IN_BASE_MIN_THROW, Config.PAWNS_IN_BASE_MAX_THROW) :
            throwDice(Config.MIN_THROW, Config.MAX_THROW) ;
        context.room.setThrown(thrown);
        context.room.send(ServerMessage.thrown(context.color, thrown));
    }

    private boolean allPawnsAreInBase(PlayerColor color) {
        for (int i = 1; i <= Config.MAX_PLAYERS; i++) {
            Pawn pawn = board.getPawn(color, i);
            if (pawn.field().fieldType == BoardFieldType.FIELD) {
                return false;
            }
        }
        return true;
    }

    private int throwDice(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
}
