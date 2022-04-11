package server.room.handlers;

import server.messages.ErrorMessages;
import server.messages.ServerMessage;
import server.messages.ServerMessageFactory;
import server.room.Context;
import server.room.board.Board;
import server.room.board.BoardField;
import server.room.board.BoardFieldType;
import server.room.board.Pawn;

public class PawnInFieldHandler extends Handler {

    public PawnInFieldHandler(Board board) {
        super(board);
        setNext(new CheckWinningHandler(board));
    }

    @Override
    public void handle(Context context) {
        Pawn pawn = board.getPawn(context.color, context.pawnIndex);
        BoardField currentField = pawn.field();
        BoardField targetField = currentField;
        try {
            for (int i = 0; i < context.thrown; i++) {
                targetField = targetField.next(context.color);
            }
        } catch (NullPointerException e) {
            targetField = null;
        }    
        if (targetField == null) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.PAWN_MOVED_TO_FAR));
            return;
        }
        Pawn targetFieldPawn = targetField.pawn();
        if (targetFieldPawn == null) {
            board.movePawn(pawn, targetField);
            context.room.send(ServerMessage.pawnMove(pawn.color, pawn.index, targetField.fieldType, targetField.index));
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
        next(context);
    }
}
