import java.io.*;
import java.net.*;

public class UDPserver {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            int port = 1234; // Replace with your port
            socket = new DatagramSocket(port);
            System.out.println("Server is running on port " + port);

            while (true) {
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];

                // Receive request
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Process request
                String response = "Hello from UDP Server"; // Placeholder response

                // Send response
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println("Server Exception: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
