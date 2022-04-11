package server.messages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static ByteBuffer toByteBuffer(String message) {
        return StandardCharsets.UTF_8.encode(message);
    }

    public static String toString(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

}
