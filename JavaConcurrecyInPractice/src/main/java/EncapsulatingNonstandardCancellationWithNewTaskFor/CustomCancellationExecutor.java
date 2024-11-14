package EncapsulatingNonstandardCancellationWithNewTaskFor;

import java.net.Socket;
import java.util.concurrent.*;

public class CustomCancellationExecutor extends ThreadPoolExecutor {
    public CustomCancellationExecutor(int corePoolSize, int maximumPoolSize,
                                      long keepAliveTime, TimeUnit unit,
                                      BlockingQueue<Runnable> workQueue)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof NetworkTask) {
            return new CustomFutureTask<>((NetworkTask<T>) callable);
        } else {
            return super.newTaskFor(callable);
        }
    }

    static class NetworkTask<T> implements Callable<T> {
        private final Socket socket;

        public NetworkTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public T call() throws Exception {
            // Perform network operations with the socket
            // e.g., read from socket, write to socket, etc.
            // Simulate some network work
            Thread.sleep(9000);
            return (T) "Task completed!";
        }

        public void cancel() {
            try {
                socket.close(); // Close the socket to interrupt network operations
            } catch (Exception e) {
                System.out.println("Error closing socket on cancel.");
            }
        }
    }

    static class CustomFutureTask<T> extends FutureTask<T> {
        private final NetworkTask<T> task;

        public CustomFutureTask(NetworkTask<T> task) {
            super(task);
            this.task = task;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            task.cancel(); // Perform custom cancellation logic
            return super.cancel(mayInterruptIfRunning);
        }
    }

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        CustomCancellationExecutor executor =
                new CustomCancellationExecutor(2, 4,
                        60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
//NetworkTask -> represents a long-running network operation that uses a Socket
        NetworkTask<String> task = new NetworkTask<>(socket);
        Future<String> future = executor.submit(task);
        System.out.println("test1");
        // Simulate canceling the task before completion
        Thread.sleep(7000); // Wait for a moment before canceling


        future.cancel(true); // CustomFutureTask.cancel will be called, closing the socket
        System.out.println("test2");
        executor.shutdown();
    }
}
