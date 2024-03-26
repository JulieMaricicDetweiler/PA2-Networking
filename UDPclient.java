import java.io.*;
import java.net.*;

public class UDPclient {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            // Prepare request
            String request = "Hello"; // Your request here
            sendData = request.getBytes();

            // Send request
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 1234);
            socket.send(sendPacket);

            // Receive response
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("FROM SERVER: " + response);

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
