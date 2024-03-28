import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class UDPServer {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            int port = Integer.parseInt(args[0]); // Replace with your port
            socket = new DatagramSocket(port);
            System.out.println("Server is running on port " + port + "\n-----------------------------------\n");

            System.out.println("Image Load Times:");
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
                        String imagePath = "./images/" + "image" + imageNumber + ".jpg"; // Corrected path concatenation

                        long imgLoadBegin = System.currentTimeMillis(); // BEGIN IMAGE LOAD TIME

                        // Load the image file into a byte array
                        byte[] imageData = Files.readAllBytes(Paths.get(imagePath));

                        long imgLoadEnd = System.currentTimeMillis(); // END IMAGE LOAD TIME
                        System.out.println("Image " + imageNumber + ": " + (imgLoadEnd - imgLoadBegin) + "ms");

                        int chunkSize = 508; // Safe payload size for UDP to avoid fragmentation
                        for (int i = 0; i < imageData.length; i += chunkSize) {
                            int end = Math.min(imageData.length, i + chunkSize);
                            byte[] chunk = Arrays.copyOfRange(imageData, i, end);
                            DatagramPacket sendPacket = new DatagramPacket(chunk, chunk.length, clientAddress, clientPort);
                            socket.send(sendPacket);
                        }
                        // Send a zero-length packet as end of transmission indicator
                        socket.send(new DatagramPacket(new byte[0], 0, clientAddress, clientPort));
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
                System.out.println("\n------------------------\n\nClosing connection...");
                socket.close();
            }
        }
    }
}
