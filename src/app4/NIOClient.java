package app4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class NIOClient {

    //Main method
    public static void main(String[] args) throws IOException {
        // Create a socket channel and connect to the server
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress("localhost", 5454));
        ByteBuffer buffer = ByteBuffer.allocate(256);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg = "aa";
        while(clientChannel.isConnected()){
             msg = br.readLine();
            // Message to send to the server
            buffer.clear();
            buffer.put(msg.getBytes());
            buffer.flip();

            // Send the message to the server
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
            buffer.clear();
            // Read the server's response
            clientChannel.read(buffer);
            buffer.flip();
            String response = new String(buffer.array(), 0, buffer.limit());
            System.out.println("Server Response: " + response);
            if(msg.equalsIgnoreCase("close")){
                clientChannel.close();

            }
        }
    }
}