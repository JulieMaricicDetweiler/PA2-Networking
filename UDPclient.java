import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.HashSet;

public class UDPclient {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        int NUM_IMAGES = 3;
        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost"); // Change to your server's IP address if not running locally
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[65507]; // Maximum UDP packet size

            Random rand = new Random();
            HashSet<Integer> sent = new HashSet<>();

            //generate requests randomly
            while(sent.size() < NUM_IMAGES) {
                int imageNumber = rand.nextInt(NUM_IMAGES) + 1; //random image number generation

                if(!sent.contains(imageNumber)) { //make sure image number is not already recieved
                    String request = "Image " + imageNumber; //request string
                    sent.add(imageNumber);
                    sendData = request.getBytes();

                    // Send request
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 1234);
                    socket.send(sendPacket);

                    // Prepare to receive the image
                    FileOutputStream fos = new FileOutputStream("received_images_UDP/received_image" + imageNumber + ".jpg");
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
            }

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}
