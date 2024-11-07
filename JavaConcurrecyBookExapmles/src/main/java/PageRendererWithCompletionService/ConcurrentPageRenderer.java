package PageRendererWithCompletionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentPageRenderer {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws InterruptedException {
        ConcurrentPageRenderer renderer = new ConcurrentPageRenderer();
        renderer.renderPage();
    }

    public void renderPage() throws InterruptedException {
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        List<Callable<String>> renderTasks = List.of(
                this::renderHeader,
                this::renderContent,
                this::renderSidebar,
                this::renderFooter
        );

        // Submit all tasks to the CompletionService
        for (Callable<String> task : renderTasks) {
            completionService.submit(task);
        }

        // Collect results as they complete
        StringBuilder fullPage = new StringBuilder();
        for (int i = 0; i < renderTasks.size(); i++) {
            try {
                Future<String> result = completionService.take(); // blocks until a task completes
                fullPage.append(result.get());
            } catch (ExecutionException e) {
                System.err.println("Error during page rendering: " + e.getMessage());
            }
        }

        System.out.println("Full Page Rendered:");
        System.out.println(fullPage);

        // Shutdown the executor service after rendering is complete
        executor.shutdown();
    }

    private String renderHeader() throws InterruptedException {
        simulateRenderingTime(500); // Simulate rendering time
        return "<header>Header Content</header>\n";
    }

    private String renderContent() throws InterruptedException {
        simulateRenderingTime(1000); // Simulate rendering time
        return "<main>Main Content</main>\n";
    }

    private String renderSidebar() throws InterruptedException {
        simulateRenderingTime(300); // Simulate rendering time
        return "<aside>Sidebar Content</aside>\n";
    }

    private String renderFooter() throws InterruptedException {
        simulateRenderingTime(200); // Simulate rendering time
        return "<footer>Footer Content</footer>\n";
    }

    // Simulate rendering delay
    private void simulateRenderingTime(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}
