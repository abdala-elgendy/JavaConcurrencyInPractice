package Latches;

import java.util.concurrent.CountDownLatch;

// example on latches and where I can use

public class ServiceInitializationExample {

    private static class Service implements Runnable {
        private CountDownLatch latch;
        private final String serviceName;

        public Service(CountDownLatch latch, String serviceName) {
            this.latch = latch;
            this.serviceName = serviceName;
        }
         public Service( String serviceName) {

            this.serviceName = serviceName;
        }

        @Override
        public void run() {
            try {
                System.out.println(serviceName + " is starting up...");
                Thread.sleep((long) (Math.random() * 6000)); // Simulate startup time
                System.out.println(serviceName + " has started.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
            if(latch!=null)   latch.countDown(); // Indicate service has finished starting
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numberOfServices = 3;
        CountDownLatch latch = new CountDownLatch(numberOfServices);

        // Start each service in its own thread
        new Thread(new Service( latch,"Service 1")).start();
        new Thread(new Service(latch, "Service 2")).start();
        new Thread(new Service(latch,"Service 3")).start();

        System.out.println("Main thread waiting for services to start...");
        latch.await(); // Wait until all services have started
        System.out.println("All services have started. Main thread can proceed.");
    }
}
