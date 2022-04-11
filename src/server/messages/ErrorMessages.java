package server.messages;

public enum ErrorMessages {
    INVALID_MESSAGE("error: Invalid message"),
    NOT_IN_GAME("error: Not in game"),
    NOT_YOUR_TURN("error: Not your turn"),
    PAWN_IN_WIN_BASE("error: Pawn in win base"),
    INVALID_THROWN("error: Must roll 6 to leave base"),
    NOT_THROWN("error: Not thrown"),
    ALREADY_THROWN("error: Already thrown"),
    SPACE_OCCUPIED("error: Space occupied"),
    PAWN_MOVED_TO_FAR("error: Pawn moved to far"),

    ;

    private final String errorCode;

    private ErrorMessages(String code) {
        errorCode = code;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

}
