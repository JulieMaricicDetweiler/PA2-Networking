import java.io.*;
import java.net.*;
import java.util.*;
import java.text.DecimalFormat;

public class UDPclient {
    public static void main(String[] args) {
        String hostAddress = args[0];
        int port = Integer.parseInt(args[1]);
        try (DatagramSocket socket = new DatagramSocket()) {

            //resolve host and get time
            long dnsResBegin = System.currentTimeMillis();
            InetAddress serverAddress = InetAddress.getByName(hostAddress); // Or replace with server IP
            long dnsResEnd = System.currentTimeMillis();
            long dnsTotalTime = dnsResEnd - dnsResBegin;
            System.out.println("\nUDP setup time: " + dnsTotalTime + "ms\n----------------------------------\n");


            byte[] sendData;
            byte[] receiveData = new byte[65507];
            int NUM_IMAGES = 3;
            Random rand = new Random();

            ArrayList<Long> roundTripTimes = new ArrayList<>();
            HashSet<Integer> sent = new HashSet<>();
            System.out.println("Image Request Times: ");
            //generate requests randomly
            while(sent.size() < NUM_IMAGES) {
                int imageNumber = rand.nextInt(NUM_IMAGES) + 1;

                if(!sent.contains(imageNumber)) { //make sure image number is not already recieved
                    String request = "Image " + imageNumber; //request string
                    sent.add(imageNumber);
                    sendData = request.getBytes();

                    long roundTripBegin = System.currentTimeMillis(); //beginning of round trip time
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
                    socket.send(sendPacket);

                    FileOutputStream fos = new FileOutputStream("received_image" + imageNumber + ".jpg");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    boolean receiving = true;
                    while (receiving) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
                        int length = receivePacket.getLength();
                        if (length == 0) {
                            long roundTripEnd = System.currentTimeMillis(); //end of round trip time
                            long roundTripTime = roundTripEnd - roundTripBegin;
                            roundTripTimes.add(roundTripTime);
                            System.out.println("Image " + imageNumber + ": " + roundTripTime + " ms");
                            receiving = false;
                        } else {
                            bos.write(receivePacket.getData(), 0, length);
                        }
                    }
                    bos.flush();
                    bos.close();
                }
            }

            System.out.println("\n----------------------------------\n");
            System.out.println("Round Trip Statistics:");
            // Calculate statistics (min, mean, max, stddev)
            // Assuming roundTripTimes is populated with all the round-trip times for each image
            DecimalFormat round = new DecimalFormat("#.##");
            double min = Collections.min(roundTripTimes);
            double max = Collections.max(roundTripTimes);
            double avg = roundTripTimes.stream().mapToLong(val -> val).average().orElse(0.0);
            double stddev = calculateStdDev(roundTripTimes, avg);

            System.out.println("Min: " + min + " ms, \nMax: " + max + " ms, \nAverage: " + round.format(avg) + " ms, \nStandard Deviation: " + round.format(stddev) + " ms");

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    private static double calculateStdDev(ArrayList<Long> data, double mean) {
        double temp = 0;
        for (long a : data)
            temp += (a - mean) * (a - mean);
        return Math.sqrt(temp / data.size());
    }
}
