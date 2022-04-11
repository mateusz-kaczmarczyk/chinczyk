package server.config;

import server.room.PlayerColor;

public final class Config {

    // SERVER RELATED
    public static final String HOSTNAME = "192.168.1.12";
    public static final int PORT = 5050;
    public static final int READ_BUFFER_SIZE = 1024;

    // MESSAGES
    public static final String MESSAGE_END_CHAR = "\0";
    public static final int CLIENT_MESSAGES_QUEUE_SIZE = 1000;

    // THREADS
    public static final int WAITING_QUEUE_SIZE = 1000;
    public static final int WAITING_QUEUE_THREADS_NUM = 1;
    public static final int WAITING_QUEUE_THREAD_SLEEP = 2000;
    public static final int RECEIVED_MESSAGES_QUEUE_SIZE = 1000;
    public static final int RECEIVED_MESSAGES_THREADS_NUM = 3;
    public static final int RECEIVED_MESSAGES_THREAD_SLEEP_TIME = 100;

    // ROOM RELATED
    public static final int ROOM_SIZE = 4;
    public static final int MAX_PLAYERS = 4;
    public static final int ROOMS_LIMIT = 1000;

    // GAME RELATED
    public static final int FIELDS_NUM = 40;
    public static final int MIN_THROW = 1;
    public static final int MAX_THROW = 6;
    public static final int PAWNS_IN_BASE_MIN_THROW = 3;
    public static final int PAWNS_IN_BASE_MAX_THROW = 6;
    public static final int BASE_LEAVE_NUMBER = 6;

    public static final int RED_START_FIELD = 0;
    public static final int GREEN_START_FIELD = 10;
    public static final int BLUE_START_FIELD = 20;
    public static final int YELLOW_START_FIELD = 30;

    public static final int RED_LAST_FIELD = 39;
    public static final int GREEN_LAST_FIELD = 9;
    public static final int BLUE_LAST_FIELD = 19;
    public static final int YELLOW_LAST_FIELD = 29;

    private Config() {
        throw new IllegalStateException("Utility class");
    }

}
