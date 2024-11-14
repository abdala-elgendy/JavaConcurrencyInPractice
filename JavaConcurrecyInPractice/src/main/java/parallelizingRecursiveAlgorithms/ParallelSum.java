package parallelizingRecursiveAlgorithms;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class ParallelSum extends RecursiveTask<Long> {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 500;

    public ParallelSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;

        // Base case: directly compute if below threshold
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                System.out.println(array[i]);
                sum += array[i];
            }
            return sum;
        }

        // Recursive case: split the task
        int mid = start + length / 2;
        ParallelSum leftTask = new ParallelSum(array, start, mid);
        ParallelSum rightTask = new ParallelSum(array, mid, end);

        // Fork left task to execute asynchronously
        leftTask.fork();

        // Compute right task synchronously
        long rightResult = rightTask.compute();

        // Join the left task’s result
        long leftResult = leftTask.join();

        // Combine the results
        return leftResult + rightResult;
    }

    public static void main(String[] args) {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1; // Fill array with numbers 1 to 10000
        }

        ForkJoinPool pool = new ForkJoinPool();
        ParallelSum task = new ParallelSum(array, 0, array.length);
        long result = pool.invoke(task);

        System.out.println("Sum: " + result);
    }
}

