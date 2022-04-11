package server.room.handlers;

import server.room.Context;
import server.room.board.Board;

public abstract class Handler {

    protected Board board;
    protected Handler nextHandler;

    protected Handler(Board board) {
        this.board = board;
    }

    public void setNext(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }    
    
    public void next(Context context) {
        this.nextHandler.handle(context);
    }
    public abstract void handle(Context context);

}
