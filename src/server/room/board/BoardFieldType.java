package server.room.board;

public enum BoardFieldType {
    BASE("Base"), FIELD("Field"), WIN_BASE("WinBase");

    private final String fieldName;

    private BoardFieldType(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }

}