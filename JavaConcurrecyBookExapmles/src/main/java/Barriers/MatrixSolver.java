package Barriers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MatrixSolver {
    private final int[][] matrix;
    private final CyclicBarrier barrier;

    public MatrixSolver(int rows, int columns, int numberOfThreads) {
        matrix = new int[rows][columns];
        barrier = new CyclicBarrier(numberOfThreads,
                () -> System.out.println("All threads reached the barrier, proceeding to next phase."));
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Worker(i)).start();
        }
    }

    private class Worker implements Runnable {
        private final int row;

        Worker(int row) {
            this.row = row;
        }

        @Override
        public void run() {
            try {
                for (int phase = 0; phase < 3; phase++) { // Assume 3 phases
                    System.out.println("Thread working on row " + row + ", phase " + phase);
                    processRow(row); // Simulate processing of this row
                    barrier.await(); // Wait for other threads at barrier

                }
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void processRow(int row) {
            // Simulate processing time
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int rows = 3, columns = 5, threads = 4;
        new MatrixSolver(rows, columns, threads);
    }
}

