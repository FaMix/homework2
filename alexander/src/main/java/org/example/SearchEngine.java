package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchEngine {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] fields = {"Title","Authors", "Abstract", "Content"};
        int fieldsLength = fields.length;
        String[][] arrays = new String[fields.length][];


        for (int i=0; i<fieldsLength; i++){
            System.out.print("Value for " + fields[i].toLowerCase(Locale.ROOT) + ": ");
            String searchTerm = scanner.nextLine();
            arrays[i] = searchTerm.split("\\s+");
        }
        scanner.close();


        try {
            // Directorio donde se encuentra el índice binario
            Path indexPath = Paths.get("../lucene-index");
            try (Directory directory = FSDirectory.open(indexPath);
                 IndexReader indexReader = DirectoryReader.open(directory)) {

                IndexSearcher searcher = new IndexSearcher(indexReader);

                PhraseQuery.Builder[] builders = new PhraseQuery.Builder[fieldsLength];

                for (int j=0; j<fieldsLength; j++) {
                    builders[j] = new PhraseQuery.Builder();
                    String[] words = arrays[j];

                    for (int i = 0; i < words.length; i++) {
                        builders[j].add(new Term(fields[j], words[i]), i);
                    }
                    System.out.println("\nResults for " + fields[j] + ": ");
                    PhraseQuery query = builders[j].build();
                    runQuery(searcher, query);
                }

            }
        } catch (IOException e) {
            System.err.println("Error when trying to access the index: " + e.getMessage());
        }
    }

    private static void runQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        if (hits.scoreDocs.length == 0) {
            System.out.println("No results found for this query :(");
        } else {
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                Document doc = searcher.doc(hits.scoreDocs[i].doc);
                System.out.println("Document ID: " + hits.scoreDocs[i].doc +
                        ", DocName: " + doc.get("NameDoc") +
                        ", Title: " + doc.get("Title") +
                        ", Mark: " + hits.scoreDocs[i].score);
            }
        }
    }
}
