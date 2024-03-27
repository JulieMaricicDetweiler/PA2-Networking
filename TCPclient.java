import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.HashSet;

public class TCPclient {
    public static void main(String[] args) {
        // Check if the correct number of arguments are passed (Server IP and the Port Number)
        if (args.length != 2) {
            System.out.println("Usage: java Client <Server IP> <Port Number>");
            return;
        }

        String serverIP = args[0]; // Storing the server IP
        int port = Integer.parseInt(args[1]); // Parsing and storing the Port Number

        int NUM_IMAGES = 3; //TODO: change to 10
        HashSet<Integer> sent = new HashSet<>();

        try (Socket socket = new Socket(serverIP, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            //requesting images in a loop without user input
            while(sent.size() < NUM_IMAGES) {
                Random rand = new Random();
                int imageNumber = rand.nextInt(NUM_IMAGES) + 1;

                if(!sent.contains(imageNumber)) { //make sure image number has not alread been requested
                    sent.add(imageNumber); //add requested image number to set

                    long startTime = System.currentTimeMillis(); //get start time

                    String request = "Image " + imageNumber; //image request command concatenation
                    System.out.println("Requesting " + request);
                    out.writeUTF(request); //send request to server

                    long fileSize = dis.readLong(); //get file size
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalRead = 0;
                    try (FileOutputStream fos = new FileOutputStream("received_images_TCP/received_image" + imageNumber + ".jpg")) {
                        while (totalRead < fileSize) {
                            bytesRead = dis.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                        fos.flush();
                    }
                    System.out.println("Image " + imageNumber + " received and saved.");

                    //get end time
                    long endTime = System.currentTimeMillis();

                    //calc total time and display it
                    long roundTripTime = endTime - startTime;
                    System.out.println("Round-trip time for image " + imageNumber + ": " + roundTripTime + "ms\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
