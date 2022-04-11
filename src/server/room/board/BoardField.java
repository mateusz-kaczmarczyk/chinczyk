package server.room.board;

import java.util.EnumMap;
import java.util.Map;

import server.room.PlayerColor;

public class BoardField {

    public final BoardFieldType fieldType;
    public final Integer index;
    private Pawn pawn = null;
    private Map<PlayerColor, BoardField> next = null;

    public BoardField(BoardFieldType fieldType, Integer index) {
        this.fieldType = fieldType;
        this.index = index;
        this.next = new EnumMap<>(PlayerColor.class);
    }

    public void setNext(PlayerColor color, BoardField nextField) {
        next.put(color, nextField);
    }

    public BoardField next(PlayerColor color) {
        return next.get(color);
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }

    public Pawn pawn() {
        return pawn;
    }

}