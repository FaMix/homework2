package homework2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchEngine {

    public static void main(String[] args) {
        try {
            // Read the strings from 'input.txt'
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

            String line1 = reader.readLine();
            String line2 = reader.readLine();
            String line3 = reader.readLine();
            reader.close();

            // Check if the lines are valid before processing them
            if (line1 == null || line2 == null || line3 == null) {
                System.err.println("Error: The file input.txt does not have the expected format.");
                return;
            }

            // Extract the values after ": "
            String string1 = line1.split(": ")[1];
            String string2 = line2.split(": ")[1];
            String string3 = line3.split(": ")[1];

            // Set up the pre-existing index directory
            Path path = Paths.get("resources/idx2.txt");
            try (Directory directory = FSDirectory.open(path);
                 IndexReader indexReader = DirectoryReader.open(directory);
                 BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {

                IndexSearcher searcher = new IndexSearcher(indexReader);

                // Perform searches and save results in output.txt

                // Search in the "author" field using string1
                Query query1 = new TermQuery(new Term("author", string1));
                writer.write("Results for search in 'author': " + string1 + "\n");
                runQuery(searcher, query1, writer);

                // Search in the "topic" field using string2
                Query query2 = new TermQuery(new Term("topic", string2));
                writer.write("Results for search in 'topic': " + string2 + "\n");
                runQuery(searcher, query2, writer);

                // Search in the "title" field using string3
                Query query3 = new TermQuery(new Term("title", string3));
                writer.write("Results for search in 'title': " + string3 + "\n");
                runQuery(searcher, query3, writer);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: The file input.txt does not have the expected format.");
        }
    }

    private static void runQuery(IndexSearcher searcher, Query query, BufferedWriter writer) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        if (hits.scoreDocs.length == 0) {
            writer.write("No results found for this query.\n\n");
        } else {
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                Document doc = searcher.doc(hits.scoreDocs[i].doc);
                writer.write("Document ID: " + hits.scoreDocs[i].doc + 
                             ", Title: " + doc.get("title") + 
                             ", Score: " + hits.scoreDocs[i].score + "\n");
            }
            writer.write("\n"); // Separator between query results
        }
    }
}
