package server.room;

import java.util.concurrent.ThreadLocalRandom;

public enum PlayerColor {
    RED("Red"), GREEN("Green"), BLUE("Blue"), YELLOW("Yellow");

    private final String name;

    private PlayerColor(String name) {
        this.name = name;
    }

    public PlayerColor next() {
        switch (this) {
            case RED:
                return PlayerColor.GREEN;
            case GREEN:
                return PlayerColor.BLUE;
            case BLUE:
                return PlayerColor.YELLOW;
            case YELLOW:
                return PlayerColor.RED;
            default:
                return PlayerColor.RED;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static PlayerColor randomColor() {
        int value = ThreadLocalRandom.current().nextInt(1, 4 + 1);
        switch (value) {
            case 1: return PlayerColor.RED;
            case 2: return PlayerColor.GREEN;
            case 3: return PlayerColor.BLUE;
            case 4: return PlayerColor.YELLOW;
            default: return PlayerColor.RED;
        }
    }
}
