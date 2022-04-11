package server.room.board;

import server.room.PlayerColor;

public class Pawn {

    public final int index;
    public final PlayerColor color;
    private BoardField field;

    public Pawn(int index, PlayerColor color, BoardField field) {
        this.index = index;
        this.color = color;
        this.field = field;
    }

    public void setField(BoardField field) {
        this.field = field;
    }

    public BoardField field() {
        return field;
    }

}