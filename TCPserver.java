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
                System.out.println("Got connection request from: " + client.getInetAddress() + "\n");
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
            DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
            DataInputStream fromClient = new DataInputStream(client.getInputStream());

            String str;
            while (true) {
                try {
                    str = fromClient.readUTF(); // Read request using DataInputStream
                    str = str.trim();
                    System.out.println("Got request from client: \"" + str + "\"");
                    if ("bye".equalsIgnoreCase(str)) {
                        toClient.writeUTF("Disconnected");
                        break;
                    } else if (str.startsWith("Image")) {
                        // Extract the image number from the request
                        int imageNumber = Integer.parseInt(str.substring(6)); // Assuming request format is "Image X"

                        long startTime = System.nanoTime(); // Start timing

                        sendImage(client, imageNumber);

                        long endTime = System.nanoTime(); // Start timing

                        System.out.println("Image " + imageNumber + " loading time: " + (endTime - startTime)/1_000_000 + " ms");

                    } else {
                        toClient.writeUTF("Error: Unsupported request. Please request an image.");
                    }
                } catch (EOFException e) {
                    System.out.println("Client disconnected");
                    break; // Break the loop if the client disconnects
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
            File file = new File("./images/image" + imageNumber + ".jpg"); // Adjust path as needed
            long startTime = System.currentTimeMillis(); // Start measuring meme access time

            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytes, 0, bytes.length);

            long endTime = System.currentTimeMillis(); // End measuring meme access time
            System.out.println("Meme access time for image " + imageNumber + ": " + (endTime - startTime) + "ms");

            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeLong(bytes.length); // Send file size first
            dos.write(bytes, 0, bytes.length); // Then send file
            dos.flush();

            System.out.println("Image " + imageNumber + " sent to client.\n");
        } catch (FileNotFoundException e) {
            System.out.println("Image file not found.");
        } catch (IOException e) {
            System.out.println("Error sending the image: " + e.getMessage());
        }
    }



}
