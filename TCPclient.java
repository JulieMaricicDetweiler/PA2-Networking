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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // To send data to the server

            // For simplicity, we are directly requesting images in a loop without user input
            for (int i = 1; i <= 10; i++) { // Assuming you want to download 10 images
                Random rand = new Random();
                int imageNumber = rand.nextInt(10) + 1; // Randomly choose an image between 1 and 10

                String request = "Image " + imageNumber; // Constructing the image request command
                System.out.println("Requesting " + request);
                out.println(request); // Sending the request to the server

                // Prepare to receive the image file
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("received_image" + imageNumber + ".jpg"));
                     InputStream is = socket.getInputStream()) {

                    byte[] bytes = new byte[4096]; // Use a larger buffer for binary data
                    int count;
                    while ((count = is.read(bytes)) > -1) {
                        bos.write(bytes, 0, count);
                        if (is.available() == 0) break; // Break if there's no more data to read
                    }
                    System.out.println("Image " + imageNumber + " received and saved.");
                } catch (IOException e) {
                    System.out.println("Error receiving image: " + e.getMessage());
                }

                // Note: The socket is not closed and recreated for each request in this loop, which might be necessary depending on how the server handles connections.
                // If the server closes the connection after sending an image, you will need to re-establish the connection for each new request.
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
