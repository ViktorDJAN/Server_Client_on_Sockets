package app4;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    //Main method
    public static void main(String[] args) throws IOException {
        // Create a new Selector
        Selector selector = Selector.open();
        // Create a ServerSocketChannel, bind it, and configure it as non-blocking
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 5454));
        serverChannel.configureBlocking(false);
        int ops = serverChannel.validOps(); // returns qty maintained operations
        System.out.println("qty of maintained operations = " + ops); //=16
        // Register the server socket channel with the Selector for accepting connections
        serverChannel.register(selector, ops,null);
        ByteBuffer buffer = ByteBuffer.allocate(256);
        System.out.println("Server started and listening on port 5454...");
        while (true) {
            // Select ready channels using the Selector
            selector.select();
            // Get the set of selected keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    // Accept a new client connection                  .channel - returns channel for wchich this key was created
                    serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    // Register the client channel with the Selector for reading
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    int bytesRead = 0;
                    // Read data from the client
                    SocketChannel client = (SocketChannel) key.channel();
                    buffer.clear();
                    try {
                        bytesRead = client.read(buffer); // returns number of bytes
                    }catch (SocketException e){
                        bytesRead =-1;
                    }
                    System.out.println(bytesRead + " bytes read");
                    System.out.println("Bytes = " + bytesRead);

                    if (bytesRead == -1) {
                        // Client closed the connection
                        key.cancel();
                        client.close();
                        continue;
                    }
                    buffer.flip();
                    String receivedMessage = new String(buffer.array(), 0, bytesRead);

                    // Process the received message (e.g., echo it back to the client)
                    System.out.println("Received: " + receivedMessage);

                    // Prepare the buffer for writing and echo the received message back to the client
                    String messageResponse = "Thank you for message";
                    Charset charset = StandardCharsets.UTF_8;
                    ByteBuffer responseBuffer = charset.encode(messageResponse);
                    client.write(responseBuffer);

                }
                // Remove the processed key from the set
                keyIterator.remove();
            }
        }
    }
}

