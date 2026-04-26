package ZeroCarbonFootprintTracker.src.network;

import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Server that accepts client connections and calculates discounts.
 * Listens on port 6000 and returns discount percentages (1-30%).
 */
public class DiscountServer {

    private static final ConnectionConfig config = new ConnectionConfig("localhost", 6000);

    /**
     * Starts the discount server and listens for client connections.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Server: GreenPrint Discount Server started on port " + config.getPort());

        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: Client connected.");

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String line = in.readLine();
                    double totalEmission = Double.parseDouble(line.trim());

                    // Use DiscountCalculator instead of manual calculation
                    Random random = new Random();
                    int discount = random.nextInt(30) + 1;
                    double discounted = DiscountCalculator.applyDiscount(totalEmission, discount);

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("DISCOUNT:" + discount + ":" + String.format("%.2f", discounted));
                    out.flush();

                    System.out.printf("Server: Sent: %.2f | Discount: %d%% | Result: %.2f%n",
                            totalEmission, discount, discounted);

                    in.close();
                    out.close();
                    clientSocket.close();
                    

                } catch (Exception e) {
                    System.err.println("Server: Error handling client");
                    clientSocket.close();
                }
            }

        } catch (IOException e) {
            System.err.println("Server: Could not start server");
        }
    }
}