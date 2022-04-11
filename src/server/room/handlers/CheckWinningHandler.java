package server.room.handlers;

import server.config.Config;
import server.messages.ServerMessage;
import server.room.Context;
import server.room.PlayerColor;
import server.room.board.Board;
import server.room.board.BoardFieldType;
import server.room.board.Pawn;

public class CheckWinningHandler extends Handler {

    protected CheckWinningHandler(Board board) {
        super(board);
    }

    @Override
    public void handle(Context context) {
        PlayerColor color = context.color;
        int pawnsInWinBase = 0;
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
            Pawn pawn = board.getPawn(color, i + 1);
            if (pawn.field().fieldType == BoardFieldType.WIN_BASE) {
                pawnsInWinBase++;
            }
        }
        if (pawnsInWinBase == Config.MAX_PLAYERS) {
            context.room.send(ServerMessage.playerWins(color));
        }
    }
    
}
