package CodeForAnEfficientScalableCache;

import java.util.Map;
import java.util.concurrent.*;

public class Memoizer<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(final A arg) throws InterruptedException {
        Future<V> future = cache.get(arg);

        if (future == null) {
            Callable<V> eval = () -> c.compute(arg);
            FutureTask<V> futureTask = new FutureTask<>(eval);
            future = cache.putIfAbsent(arg, futureTask);  // Attempt to put the new computation if absent

            if (future == null) {
                future = futureTask;
                futureTask.run(); // Run computation if this is the first time for this argument
            }
        }

        try {
            return future.get(); // Block until computation completes
        } catch (ExecutionException e) {
            throw new RuntimeException("Computation failed", e.getCause());
        }
    }
}

// Computable interface, representing a computation that takes an argument of type A and returns a result of type V
interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
