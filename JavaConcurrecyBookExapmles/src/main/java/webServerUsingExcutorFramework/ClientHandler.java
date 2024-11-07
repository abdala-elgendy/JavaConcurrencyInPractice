package webServerUsingExcutorFramework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Read and print the client's request (first line)
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                System.out.println(line);
            }
            String ok = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 10\r\n" +
                    "\r\n" +
                    "Hello man!";
            out.write(ok.getBytes());
            // Send a simple HTTP response
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 20\r\n" +
                    "\r\n" +
                    "Hello from the server!";
            out.write(httpResponse.getBytes());
            out.flush();  // Ensure the response is sent immediately

            System.out.println("***********************Response sent to " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
