import java.io.*;
import java.net.*;
import java.util.Random;

public class UDPclient {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost"); // Change to your server's IP address if not running locally
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[65507]; // Maximum UDP packet size

            Random rand = new Random();

            // Directly request an image without user input for jokes
            for(int i = 1; i <= 3; i++) { // Assuming you want to fetch all images in sequence or random
                int imageNumber = rand.nextInt(3) + 1; // Randomly choose an image number
                String request = "Image " + imageNumber; // Request a specific image
                sendData = request.getBytes();

                // Send request
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 1234);
                socket.send(sendPacket);

                // Prepare to receive the image
                FileOutputStream fos = new FileOutputStream("received_image" + imageNumber + ".jpg");
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                // Assume we're receiving a single image in multiple packets
                boolean receiving = true;
                while (receiving) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket); // Receive a packet

                    int length = receivePacket.getLength();
                    if (length == 0) { // Assuming the server sends a zero-length packet to indicate the end of the image transmission
                        receiving = false;
                    } else {
                        // Write received data to file
                        bos.write(receivePacket.getData(), 0, length);
                    }
                }

                bos.flush();
                bos.close();
                System.out.println("Image " + imageNumber + " received and saved.");
            }

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
