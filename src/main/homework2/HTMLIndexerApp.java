package main.homework2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HTMLIndexerApp {

    private static double start_time, end_time;

    public static void main(String[] args) throws IOException {
        String indexPath = "../lucene-index";
        Path htmlDirectoryPath = Paths.get("../htmls2");

        // Thread-safe lists to store extracted data
        List<String> fileNames = Collections.synchronizedList(new ArrayList<>());
        List<String> titles = Collections.synchronizedList(new ArrayList<>());
        List<String> contents = Collections.synchronizedList(new ArrayList<>());
        List<String> authors = Collections.synchronizedList(new ArrayList<>());
        List<String> abstracts = Collections.synchronizedList(new ArrayList<>());

        // Start timing for XPath extraction
        start_time = System.nanoTime();

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Process HTML files in the directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(htmlDirectoryPath, "*.html")) {
            for (Path filePath : stream) {
                executor.submit(() -> processHtmlFile(filePath, fileNames, titles, contents, authors, abstracts));
            }
        } catch (IOException e) {
            System.err.println("Error when reading HTML files: " + e.getMessage());
        }

        // Wait for all tasks to finish
        shutdownExecutor(executor);

        // Calculate and log XPath processing time
        end_time = System.nanoTime();
        double xpathTime = (end_time - start_time) / 1_000_000.0;

        // Start timing for indexing
        start_time = System.nanoTime();

        // Index the extracted data
        Indexer indexer = new Indexer();
        indexer.index(indexPath, fileNames, titles, authors, contents, abstracts);

        end_time = System.nanoTime();
        double indexerTime = (end_time - start_time) / 1_000_000.0;

        // Log execution times
        System.out.printf("XPath Extraction Time: %.2f ms%n", xpathTime);
        System.out.printf("Indexing Time: %.2f ms%n", indexerTime);
    }

    private static void processHtmlFile(Path filePath, List<String> fileNames, List<String> titles, List<String> contents, List<String> authors, List<String> abstracts) {
        try {
            XPathExtractor extractor = new XPathExtractor(filePath);
            fileNames.add(filePath.getFileName().toString());
            titles.add(extractor.extractTitle());
            authors.add(extractor.extractAuthors());
            contents.add(extractor.extractContent());
            abstracts.add(extractor.extractAbstract());
        } catch (Exception e) {
            System.err.println("Error processing file " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("Executor did not terminate within the specified time.");
            }
        } catch (InterruptedException e) {
            System.err.println("Error waiting for threads to complete: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
