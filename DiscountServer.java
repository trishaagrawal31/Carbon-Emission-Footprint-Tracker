package ZeroCarbonFootprintTracker;

import java.io.*;
import java.net.*;
import java.util.Random;
 
/**
 * GreenPrint Discount Server 
 * Port: 6000
 *
 * Protocol:
 *   Client sends : total emission as a number  e.g. "12.75"
 *   Server replies: "DISCOUNT:<pct>:<discountedValue>"  e.g. "DISCOUNT:15:10.84"
 */
public class DiscountServer {
 
    public static final int PORT = 6000;
 
    public static void main(String[] args) {
        System.out.println("[SERVER] GreenPrint Discount Server started on port " + PORT);
 
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
 
            // Accept clients in a loop — one at a time
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Client connected.");
 
                try {
                    // Read total emission from client
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String line = in.readLine();
                    double totalEmission = Double.parseDouble(line.trim());
 
                    // Generate random discount 1–30
                    Random random = new Random();
                    int discount = random.nextInt(30) + 1;
 
                    // Calculate discounted emission
                    double discounted = totalEmission * (1.0 - discount / 100.0);
                    discounted = Math.round(discounted * 100.0) / 100.0;
 
                    // Send response back to client
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("DISCOUNT:" + discount + ":" + String.format("%.2f", discounted));
                    out.flush();
 
                    System.out.printf("[SERVER] Sent: %.2f | Discount: %d%% | Result: %.2f%n",
                            totalEmission, discount, discounted);
 
                    // Close resources
                    in.close();
                    out.close();
                    clientSocket.close();
 
                } catch (Exception e) {
                    System.err.println("[SERVER] Error handling client " );
                    clientSocket.close();
                }
            }
 
        } catch (IOException e) {
            System.err.println("[SERVER] Could not start server: " );
        }
    }
}