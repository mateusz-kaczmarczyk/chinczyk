package server.room.handlers;

import server.messages.ErrorMessages;
import server.messages.ServerMessageFactory;
import server.room.Context;
import server.room.board.Board;
import server.room.board.BoardFieldType;
import server.room.board.Pawn;

public class PawnMoveHandler extends Handler {

    private PawnInBaseHandler pawnInBaseHandler;
    private PawnInFieldHandler pawnInFieldHandler;

    public PawnMoveHandler(Board board) {
        super(board);
        pawnInBaseHandler = new PawnInBaseHandler(board);
        pawnInFieldHandler = new PawnInFieldHandler(board);
    }

    @Override
    public void handle(Context context) {
        Pawn pawn = board.getPawn(context.color, context.pawnIndex);
        if (pawn.field().fieldType == BoardFieldType.WIN_BASE) {
            context.sender.send(ServerMessageFactory.msg(ErrorMessages.PAWN_IN_WIN_BASE));
            return;
        }
        else if (pawn.field().fieldType == BoardFieldType.BASE) {
            setNext(pawnInBaseHandler);
        }
        else setNext(pawnInFieldHandler);
        next(context);
    }
}
