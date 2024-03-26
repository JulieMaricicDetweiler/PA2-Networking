import java.io.*;
import java.net.*;

public class TCPserver {
    public static void main(String[] args) {
        int port_number = 0;
        ServerSocket server = null;
        try {
            // Validate port number and create server socket
            if (args.length != 1) {
                System.out.println("Usage: java TCPserver <port_number>");
                return;
            }
            port_number = Integer.parseInt(args[0]);
            if (port_number < 1 || port_number > 65535) {
                System.out.println("Port number must be between 1 and 65535");
                return;
            }
            server = new ServerSocket(port_number);
            System.out.println("Server is running on port " + port_number);

            // Accept connections infinitely
            while (true) {
                Socket client = server.accept();
                System.out.println("Got connection request from: " + client.getInetAddress());
                processRequest(client);
            }
        } catch (IOException e) {
            System.out.println("Server Exception: " + e.getMessage());
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Error closing server: " + e.getMessage());
                }
            }
        }
    }

    private static void processRequest(Socket client) {
        try {
            PrintWriter toClient = new PrintWriter(client.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String str;
            while ((str = fromClient.readLine()) != null) {
                str = str.trim();
                System.out.println("Got request from client: \"" + str + "\"");
                if ("bye".equalsIgnoreCase(str)) {
                    toClient.println("Disconnected");
                    break;
                } else if (str.startsWith("Image")) {
                    // Extract the image number from the request
                    int imageNumber = Integer.parseInt(str.substring(6)); // Assuming request format is "Image X"
                    sendImage(client, imageNumber);
                } else {
                    toClient.println("Error: Unsupported request. Please request an image.");
                }
            }
            System.out.println("Closing connection...");
            client.close();
        } catch (IOException e) {
            System.out.println("Error processing request: " + e.getMessage());
        }
    }

    private static void sendImage(Socket client, int imageNumber) {
        try {
            long startTime = System.currentTimeMillis(); // Start measuring time

            File file = new File("./images/image" + imageNumber + ".jpg"); // Adjust path as needed
            System.out.println("File length in bytes: " + file.length());
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytes, 0, bytes.length);

            OutputStream os = client.getOutputStream();
            os.write(bytes, 0, bytes.length);
            os.flush();

            long endTime = System.currentTimeMillis(); // End measuring time
            long elapsedTime = endTime - startTime; // Calculate elapsed time

            System.out.println("Image " + imageNumber + " sent to client. Time taken: " + elapsedTime + "ms");
        } catch (FileNotFoundException e) {
            System.out.println("Image file not found.");
        } catch (IOException e) {
            System.out.println("Error sending the image.");
        }
    }

}
