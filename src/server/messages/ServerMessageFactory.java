package server.messages;

import server.config.Config;

public class ServerMessageFactory {
    
    private ServerMessageFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static String msg(ErrorMessages msg) {
        StringBuilder sb = new StringBuilder(msg.getErrorCode());
        sb.append(Config.MESSAGE_END_CHAR);
        return sb.toString();
    }

}
