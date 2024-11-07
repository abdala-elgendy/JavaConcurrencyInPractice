package webServerUsingExcutorFramework;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class SimpleWebServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public SimpleWebServer(int port, int poolSize) throws IOException {
        // Initialize server socket on the specified port
        serverSocket = new ServerSocket(port);
        // Initialize the ExecutorService with a fixed thread pool
        executorService = Executors.newFixedThreadPool(poolSize);
        System.out.println("Server started on port " + port);
    }

     public void start() {
        while (true) {
            try {
                // Accept incoming client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                // Handle client connection in a separate thread from the pool
                executorService.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
                break;
            }
        }
    }

    public void shutdown() {
        try {
            executorService.shutdown();
            serverSocket.close();
            System.out.println("Server shut down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        SimpleWebServer server = new SimpleWebServer(8080, 10); // Port 8080, pool of 10 threads
        server.start();

    }
}




