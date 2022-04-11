package server;

import java.util.Map;
import java.util.Queue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import server.config.Config;
import server.thread.ReceivedMessagesThread;
import server.thread.WaitingQueueThread;
import server.messages.ReceivedMessage;
import server.messages.Utils;
import server.room.Room;

public class Server {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(Config.READ_BUFFER_SIZE);
    private final Queue<ReceivedMessage> receivedMessagesQueue = new ArrayBlockingQueue<>(Config.RECEIVED_MESSAGES_QUEUE_SIZE);
    private final Queue<Room> rooms = new ArrayBlockingQueue<>(Config.ROOMS_LIMIT);
    private final Queue<IClient> waitingQueue = new ArrayBlockingQueue<>(Config.WAITING_QUEUE_SIZE);
    private final Map<SelectionKey, IClient> clients = new ConcurrentHashMap<>();

    public Server() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(Config.HOSTNAME, Config.PORT));
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            initHandlers();
            System.out.println("Server listening at " + Config.HOSTNAME + ":" + Config.PORT);
            loop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initHandlers() {
        for (int i = 0; i < Config.WAITING_QUEUE_THREADS_NUM; i++) {
            new Thread(new WaitingQueueThread(selector, clients, waitingQueue, rooms), "WaitingQueue-Thread-"+i).start();
        }
        for (int i = 0; i < Config.RECEIVED_MESSAGES_THREADS_NUM; i++) {
            new Thread(new ReceivedMessagesThread(selector, receivedMessagesQueue), "ReceivedMessages-Thread-"+i).start();
        }
    }

    private void loop() throws IOException {
        while (selector.isOpen()) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {

                if (key.isValid() && key.isAcceptable()) {
                    SocketChannel serverChannel = ((ServerSocketChannel) key.channel()).accept();
                    serverChannel.configureBlocking(false);
                    SelectionKey clientKey = serverChannel.register(selector, SelectionKey.OP_READ);
                    clientKey.attach(new ArrayBlockingQueue<ByteBuffer>(Config.CLIENT_MESSAGES_QUEUE_SIZE));
                    IClient client = new Client(clientKey);
                    clients.put(clientKey, client);
                    waitingQueue.add(client);
                    System.out.println(Thread.currentThread().getName() + ": Client::" + client.getId() + " Connected from " + serverChannel.getRemoteAddress());
                }

                if (key.isValid() && key.isReadable()) {
                    IClient client = clients.get(key);
                    SocketChannel socket = ((SocketChannel) key.channel());
                    readBuffer.clear();
                    int read = socket.read(readBuffer);
                    if (read == -1) {
                        System.out.println(Thread.currentThread().getName() + ": Client::" + client.getId() + " Disconnected");
                        clients.remove(key);
                        waitingQueue.remove(client);
                        socket.close();
                        continue;
                    }
                    if (read > 0) {
                        readBuffer.flip();
                        ByteBuffer buffer = ByteBuffer.allocate(readBuffer.remaining());
                        buffer.put(readBuffer).flip();
                        String msg = Utils.toString(buffer);
                        System.out.println(Thread.currentThread().getName() + ": " + "Client::" + client.getId() + " " + msg);
                        receivedMessagesQueue.add(new ReceivedMessage(client, msg));
                        key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                    }
                }

                if (key.isValid() && key.isWritable()) {
                    SocketChannel socket = (SocketChannel) key.channel();
                    Queue<Buffer> dataToWrite = (Queue<Buffer>) key.attachment();
                    while (dataToWrite.peek() != null) {
                        ByteBuffer buffer = (ByteBuffer) dataToWrite.peek();
                        socket.write(buffer);
                        if (buffer.remaining() == 0) {
                            dataToWrite.remove();
                        } else
                            break;
                    }
                    if (dataToWrite.isEmpty()) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
            selector.selectedKeys().clear();
        }
    }

}
