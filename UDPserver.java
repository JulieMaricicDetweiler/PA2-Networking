import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UDPserver {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            int port = 1234; // Replace with your port
            socket = new DatagramSocket(port);
            System.out.println("Server is running on port " + port);

            while (true) {
                byte[] receiveData = new byte[1024];

                // Receive request
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if (request.startsWith("Image")) {
                    // Parse the request to identify which image is being requested
                    String[] parts = request.split(" ");
                    if (parts.length == 2) {
                        int imageNumber = Integer.parseInt(parts[1]);
                        String imagePath = "./images" + imageNumber + ".jpg"; // Adjust the path as necessary

                        try {
                            // Load the image file into a byte array
                            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));

                            // Send the image data
                            DatagramPacket sendPacket = new DatagramPacket(imageData, imageData.length, clientAddress, clientPort);
                            socket.send(sendPacket);
                            System.out.println("Image " + imageNumber + " sent to the client.");
                        } catch (IOException e) {
                            System.out.println("Error loading or sending image: " + e.getMessage());
                            // Send an error message back to the client if the image couldn't be loaded or sent
                            byte[] errorData = "Error: Unable to send image.".getBytes();
                            DatagramPacket errorPacket = new DatagramPacket(errorData, errorData.length, clientAddress, clientPort);
                            socket.send(errorPacket);
                        }
                    }
                } else {
                    // If the request does not start with "Image", send an error message back to the client
                    String errorMessage = "Error: Unsupported request. Please request an image.";
                    byte[] errorData = errorMessage.getBytes();
                    DatagramPacket errorPacket = new DatagramPacket(errorData, errorData.length, clientAddress, clientPort);
                    socket.send(errorPacket);
                }
            }
        } catch (IOException e) {
            System.out.println("Server Exception: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
