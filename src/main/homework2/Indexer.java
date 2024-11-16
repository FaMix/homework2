package main.homework2;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Indexer {

    private static final int THREAD_TIMEOUT_HOURS = 1;

    /**
     * Indexes the provided data into a Lucene index.
     *
     * @param indexPath  the path to the directory where the Lucene index will be stored
     * @param fileNames  list of document names
     * @param titles     list of document titles
     * @param authors    list of document authors
     * @param contents   list of document contents
     * @param abstracts  list of document abstracts
     * @throws IOException if an I/O error occurs
     */
    public void index(String indexPath, List<String> fileNames, List<String> titles, List<String> authors, 
                      List<String> contents, List<String> abstracts) throws IOException {

        Path indexDirectoryPath = Paths.get(indexPath);
        try (Directory directory = FSDirectory.open(indexDirectoryPath);
             Analyzer analyzer = createAnalyzer();
             IndexWriter writer = createIndexWriter(directory, analyzer)) {

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int i = 0; i < titles.size(); i++) {
                final int index = i;
                executor.submit(() -> addDocument(writer, fileNames, titles, authors, contents, abstracts, index));
            }

            shutdownExecutor(executor);
            writer.commit();
        } catch (IOException e) {
            System.err.println("Failed to create or write to Lucene index: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates a PerFieldAnalyzerWrapper with specific analyzers for each field.
     *
     * @return the configured Analyzer
     */
    private Analyzer createAnalyzer() {
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        perFieldAnalyzers.put("Title", new WhitespaceAnalyzer());
        perFieldAnalyzers.put("Authors", new StandardAnalyzer());
        perFieldAnalyzers.put("Abstract", new EnglishAnalyzer());
        perFieldAnalyzers.put("Content", new EnglishAnalyzer());

        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers);
    }

    /**
     * Creates an IndexWriter with the specified directory and analyzer.
     *
     * @param directory the directory to store the index
     * @param analyzer  the analyzer for the index
     * @return the configured IndexWriter
     * @throws IOException if an I/O error occurs
     */
    private IndexWriter createIndexWriter(Directory directory, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCodec(new SimpleTextCodec());
        return new IndexWriter(directory, config);
    }

    /**
     * Adds a document to the Lucene index.
     *
     * @param writer    the IndexWriter
     * @param fileNames list of document names
     * @param titles    list of document titles
     * @param authors   list of document authors
     * @param contents  list of document contents
     * @param abstracts list of document abstracts
     * @param index     the current document index
     */
    private void addDocument(IndexWriter writer, List<String> fileNames, List<String> titles, List<String> authors,
                             List<String> contents, List<String> abstracts, int index) {
        try {
            Document doc = new Document();
            doc.add(new TextField("NameDoc", fileNames.get(index), Field.Store.YES));
            doc.add(new TextField("Title", titles.get(index), Field.Store.YES));
            doc.add(new TextField("Authors", authors.get(index), Field.Store.YES));
            doc.add(new TextField("Abstract", abstracts.get(index), Field.Store.YES));
            doc.add(new TextField("Content", contents.get(index), Field.Store.YES));
            synchronized (writer) {
                writer.addDocument(doc);
            }
        } catch (IOException e) {
            System.err.println("Error adding document to index (DocName: " + fileNames.get(index) + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the ExecutorService and waits for all tasks to complete.
     *
     * @param executor the ExecutorService to shut down
     */
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(THREAD_TIMEOUT_HOURS, TimeUnit.HOURS)) {
                System.err.println("Executor service did not terminate in the expected time.");
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interruption during executor shutdown: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
