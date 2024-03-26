import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPclient {
    public static void main(String[] args) {
        // Check if the correct number of arguments are passed (Server IP and the Port Number)
        if (args.length != 2) {
            System.out.println("Usage: java Client <Server IP> <Port Number>");
            return;
        }

        String serverIP = args[0]; // Storing the server IP
        int port = Integer.parseInt(args[1]); // Parsing and storing the Port Number

        try (Socket socket = new Socket(serverIP, port)) { // Establishing Connection with server
            // For simplicity, we are directly requesting images in a loop without user input
            for (int i = 1; i <= 3; i++) { // Assuming you want to download 3 images
                Random rand = new Random();
                int imageNumber = rand.nextInt(3) + 1; // Randomly choose an image between 1 and 3

                // Record the start time before sending the request
                long startTime = System.currentTimeMillis();

                String request = "Image " + imageNumber; // Constructing the image request command
                System.out.println("Requesting " + request);

                // Send request to the server
                try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println(request); // Sending the request to the server

                    // Receive image data from the server
                    try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                         FileOutputStream fos = new FileOutputStream("received_image" + imageNumber + ".jpg")) {

                        long fileSize = dis.readLong(); // Read file size
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = dis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                        fos.flush();
                        System.out.println("Image " + imageNumber + " received and saved.");

                        // Record the end time after receiving the complete image
                        long endTime = System.currentTimeMillis();

                        // Calculate and print the total round-trip time
                        long roundTripTime = endTime - startTime;
                        System.out.println("Round-trip time for image " + imageNumber + ": " + roundTripTime + "ms");
                    } catch (IOException e) {
                        System.out.println("Error receiving image data: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Error sending request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
