package FutureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

// Example: Using **FutureTask** for Lazy Initialization

public class LazyInitializationExample {

    private final FutureTask<String> initializationTask = new FutureTask<>(new Callable<String>() {
        @Override
        public String call() {
            return performExpensiveComputation();
        }
    });

     private final Thread initThread = new Thread(initializationTask);

    // Method to perform a costly computation
    private static String performExpensiveComputation() {
        try {
            System.out.println("Starting expensive computation...");
            Thread.sleep(2000); // Simulate long-running computation
            System.out.println("Computation complete.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Computed Result";
    }

    public String getResult() {
        // Start the computation if it hasn't started already
        if (!initializationTask.isDone()) {
            initThread.start();
        }

        try {
            // Return the result, blocking if necessary until the computation completes
            return initializationTask.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new RuntimeException("Computation failed", e.getCause());
        }
    }

    public static void main(String[] args) {
        LazyInitializationExample example = new LazyInitializationExample();
        System.out.println("Requesting the result...");

        // The computation will start only once and only when requested
        String result1 = example.getResult();// first get() will getResult and cached it in
        // initializationTask variable in class example

        String result2=example.getResult(); // secend get() will bring cached result and not call get() again

        System.out.println("Result: " + result1);
        System.out.println("Result: " + result1);
    }
}
