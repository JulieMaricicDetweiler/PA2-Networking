import java.io.*;
import java.net.*;
import java.util.*;

public class TCPclient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <Server IP> <Port Number>");
            return;
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        final int NUM_IMAGES = 3; // Change to 10 for final requirement
        HashSet<Integer> sent = new HashSet<>();
        ArrayList<Long> roundTripTimes = new ArrayList<>(); // To store the round-trip times

        long TCPstartTime = System.currentTimeMillis();

        try (Socket socket = new Socket(serverIP, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            long TCPendTime = System.currentTimeMillis();
            long TCPsetupTime = TCPendTime - TCPstartTime;
            System.out.println("TCP setup time: " + TCPsetupTime + "ms");

            while (sent.size() < NUM_IMAGES) {
                Random rand = new Random();
                // Ensure unique images are requested
                int imageNumber = rand.nextInt(NUM_IMAGES) + 1;
                if (!sent.contains(imageNumber)) {
                    sent.add(imageNumber);

                    long startTime = System.currentTimeMillis();
                    String request = "Image " + imageNumber;
                    System.out.println("Requesting " + request);
                    out.writeUTF(request);

                    long fileSize = dis.readLong();
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalRead = 0;
                    try (FileOutputStream fos = new FileOutputStream("received_images_TCP/received_image" + imageNumber + ".jpg")) {
                        while (totalRead < fileSize) {
                            bytesRead = dis.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                    }
                    System.out.println("Image " + imageNumber + " received and saved.");

                    long endTime = System.currentTimeMillis();
                    long roundTripTime = endTime - startTime;
                    roundTripTimes.add(roundTripTime); // Store round-trip time
                    System.out.println("Round-trip time for image " + imageNumber + ": " + roundTripTime + "ms");
                }
            }

            // After all requests, calculate statistics
            calculateAndDisplayStatistics(roundTripTimes);

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    private static void calculateAndDisplayStatistics(ArrayList<Long> times) {
        long min = Collections.min(times);
        long max = Collections.max(times);
        double avg = times.stream().mapToLong(val -> val).average().orElse(0.0);
        double stdDev = calculateStandardDeviation(times, avg);

        System.out.println("\nStatistics for round-trip times:");
        System.out.println("Min: " + min + " ms");
        System.out.println("Max: " + max + " ms");
        System.out.println("Average: " + avg + " ms");
        System.out.println("Standard Deviation: " + stdDev + " ms");
    }

    private static double calculateStandardDeviation(ArrayList<Long> times, double avg) {
        double sum = 0;
        for (long time : times) {
            sum += Math.pow(time - avg, 2);
        }
        return Math.sqrt(sum / times.size());
    }
}
