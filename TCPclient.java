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

        try (Socket socket = new Socket(serverIP, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            // For simplicity, we are directly requesting images in a loop without user input
            for (int i = 1; i <= 3; i++) { // Assuming you want to download 3 images
                Random rand = new Random();
                int imageNumber = rand.nextInt(3) + 1; // Adjust if you have a different number of images

                long startTime = System.currentTimeMillis(); // Record the start time before sending the request

                String request = "Image " + imageNumber; // Constructing the image request command
                System.out.println("Requesting " + request);
                out.writeUTF(request); // Sending the request to the server

                // Correctly handle receiving the file size and then the image bytes
                long fileSize = dis.readLong(); // Read file size first
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;
                try (FileOutputStream fos = new FileOutputStream("received_image" + imageNumber + ".jpg")) {
                    while (totalRead < fileSize) {
                        bytesRead = dis.read(buffer);
                        fos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }
                    fos.flush();
                }
                System.out.println("Image " + imageNumber + " received and saved.");

                // Record the end time after receiving the complete image
                long endTime = System.currentTimeMillis();

                // Calculate and print the total round-trip time
                long roundTripTime = endTime - startTime;
                System.out.println("Round-trip time for image " + imageNumber + ": " + roundTripTime + "ms");
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
