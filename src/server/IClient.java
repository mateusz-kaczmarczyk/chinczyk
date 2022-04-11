package server;

import java.nio.channels.SelectionKey;

public interface IClient {

    public SelectionKey getKey();
    public String getId();
    public void send(String message);
}
