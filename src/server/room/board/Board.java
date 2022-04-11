package server.room.board;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import server.config.Config;
import server.room.Context;
import server.room.PlayerColor;
import server.room.handlers.Handler;
import server.room.handlers.PawnMoveHandler;

public class Board {

    private Handler handler;

    private Map<PlayerColor, BoardField> bases;
    private Map<PlayerColor, List<Pawn>> pawns;
    private Map<PlayerColor, List<BoardField>> winBases;
    private List<BoardField> fields;

    public Board() {
        handler = new PawnMoveHandler(this);
        initBoard();
    }

    public void handlePawnMove(Context context) {
        handler.handle(context);
    }

    private void initBoard() {
        PlayerColor color = PlayerColor.RED;
        fields = new ArrayList<>();
        fields.add(new BoardField(BoardFieldType.FIELD, 0));
        pawns = new EnumMap<>(PlayerColor.class);
        bases = new EnumMap<>(PlayerColor.class);
        winBases = new EnumMap<>(PlayerColor.class);
        for (int i = 1; i < Config.FIELDS_NUM; i++) {
            fields.add(new BoardField(BoardFieldType.FIELD, i));
            for (int j = 0; j < Config.MAX_PLAYERS; j++) {
                fields.get(i - 1).setNext(color, fields.get(i));
                color = color.next();
            }
        }
        for (int i = 0; i < Config.MAX_PLAYERS; i++) {
            bases.put(color, new BoardField(BoardFieldType.BASE, null));
            bases.get(color).setNext(color, fields.get(getColorStartBoardFieldIndex(color)));
            winBases.put(color, new ArrayList<>());
            pawns.put(color, new ArrayList<>());
            for (int index = 1; index <= Config.MAX_PLAYERS; index++) {
                winBases.get(color).add(new BoardField(BoardFieldType.WIN_BASE, index));
                pawns.get(color).add(new Pawn(index, color, bases.get(color)));
            }
            fields.get(Config.FIELDS_NUM - 1).setNext(color, fields.get(0));
            fields.get(getColorLastBoardFieldIndex(color)).setNext(color, winBases.get(color).get(0));
            color = color.next();
        }
        for (int i = 0; i < Config.MAX_PLAYERS - 1; i++) {
            for (int j = 0; j < Config.MAX_PLAYERS; j++) {
                winBases.get(color).get(i).setNext(color, winBases.get(color).get(i + 1));
                color = color.next();
            }
        }

    }

    public Pawn getPawn(PlayerColor color, int pawnIndex) {
        return pawns.get(color).get(pawnIndex - 1);
    }

    public BoardField getField(int index) {
        return fields.get(index);
    }

    public void movePawn(Pawn pawn, BoardField newField) {
        BoardField currentField = pawn.field();
        currentField.setPawn(null);
        newField.setPawn(pawn);
        pawn.setField(newField);
    }

    public BoardField getColorBaseField(PlayerColor color) {
        return bases.get(color);
    }

    public int getColorStartBoardFieldIndex(PlayerColor color) throws NoSuchElementException {
        switch (color) {
            case RED:
                return Config.RED_START_FIELD;
            case GREEN:
                return Config.GREEN_START_FIELD;
            case BLUE:
                return Config.BLUE_START_FIELD;
            case YELLOW:
                return Config.YELLOW_START_FIELD;
            default:
                throw new NoSuchElementException();
        }
    }

    public int getColorLastBoardFieldIndex(PlayerColor color) throws NoSuchElementException {
        switch (color) {
            case RED:
                return Config.RED_LAST_FIELD;
            case GREEN:
                return Config.GREEN_LAST_FIELD;
            case BLUE:
                return Config.BLUE_LAST_FIELD;
            case YELLOW:
                return Config.YELLOW_LAST_FIELD;
            default:
                throw new NoSuchElementException();
        }
    }

}
