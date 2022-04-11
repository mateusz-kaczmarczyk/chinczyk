package server.room.handlers;

import server.config.Config;
import server.messages.ErrorMessages;
import server.messages.ServerMessage;
import server.messages.ServerMessageFactory;
import server.room.Context;
import server.room.board.Board;
import server.room.board.BoardField;
import server.room.board.BoardFieldType;
import server.room.board.Pawn;

public class PawnInBaseHandler extends Handler {

    public PawnInBaseHandler(Board board) {
        super(board);
    }

    @Override
    public void handle(Context context) {
        Pawn pawn = board.getPawn(context.color, context.pawnIndex);
        if (context.thrown != Config.BASE_LEAVE_NUMBER) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.INVALID_THROWN));
            return;
        }
        BoardField targetField = pawn.field().next(context.color);
        Pawn targetFieldPawn = targetField.pawn();
        if (targetFieldPawn == null) {
            board.movePawn(pawn, targetField);
            context.room.send(ServerMessage.pawnMove(pawn.color, pawn.index, BoardFieldType.FIELD, targetField.index));
            context.room.nextTurn();
        }
        else if (targetFieldPawn.color == context.color) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.SPACE_OCCUPIED));
        }
        else {
            board.movePawn(targetFieldPawn, board.getColorBaseField(targetFieldPawn.color));
            context.room.send(ServerMessage.pawnMove(targetFieldPawn.color, targetFieldPawn.index, BoardFieldType.BASE, null));
            board.movePawn(pawn, targetField);
            context.room.send(ServerMessage.pawnMove(pawn.color, pawn.index, BoardFieldType.FIELD, targetField.index));
            context.room.nextTurn();
        }
    }
}
