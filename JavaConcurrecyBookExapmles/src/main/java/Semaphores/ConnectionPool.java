package Semaphores;

import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private final Semaphore semaphore;

    public ConnectionPool(int maxConnections) {
        // Initialize semaphore with the maximum number of allowed connections
        this.semaphore = new Semaphore(maxConnections);

    }

    public void connect() {
        try {
            semaphore.acquire(); // Acquire a permit before connecting
            System.out.println(Thread.currentThread().getName() + " acquired a connection.");
            Thread.sleep(1000); // Simulate database connection usage
            System.out.println(Thread.currentThread().getName() + " released a connection.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // Release the permit after using the connection
        }
    }

    public static void main(String[] args) {
        int maxConnections = 3;
        ConnectionPool pool = new ConnectionPool(maxConnections);

        // Create and start multiple threads to simulate concurrent access
        for (int i = 0; i < 10; i++) {
            new Thread(() -> pool.connect(), "Thread-" + i).start();
        }
    }
}
