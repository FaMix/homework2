package main.homework2;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class SearchEngine {

    public static Map<String, List<String>> search(Map<String, Object> inputMap) {
        // Fields to search in
        String[] fields = {"Title", "Authors", "Abstract", "Content"};
        int fieldsCount = fields.length;
        String[][] tokenizedFields = new String[fieldsCount][];
        Map<String, List<String>> searchResults = new HashMap<>();

        // Tokenize input map values
        int index = 0;
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String value = entry.getValue().toString();
            tokenizedFields[index] = value.split("\\s+");
            index++;
            if (index >= fieldsCount) {
                break;
            }
        }

        try {
            // Path to the directory containing the Lucene index
            Path indexPath = Paths.get("../lucene-index");

            try (Directory directory = FSDirectory.open(indexPath);
                 IndexReader indexReader = DirectoryReader.open(directory)) {

                IndexSearcher searcher = new IndexSearcher(indexReader);

                // Construct and execute queries for each field
                for (int j = 0; j < fieldsCount; j++) {
                    PhraseQuery.Builder queryBuilder = new PhraseQuery.Builder();
                    String[] terms = tokenizedFields[j];

                    // Build phrase query for the current field
                    for (int i = 0; i < terms.length; i++) {
                        queryBuilder.add(new Term(fields[j], terms[i]), i);
                    }

                    PhraseQuery query = queryBuilder.build();
                    List<String> results = executeQuery(searcher, query);
                    searchResults.put(fields[j], results);
                }
            }
        } catch (IOException e) {
            System.err.println("Error accessing the index: " + e.getMessage());
        }

        // Print results
        searchResults.forEach((field, results) ->
                System.out.println("[" + field + ", " + results + "]")
        );

        return searchResults;
    }

    private static List<String> executeQuery(IndexSearcher searcher, Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        List<String> results = new ArrayList<>();

        if (hits.scoreDocs.length == 0) {
            results.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
        } else {
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                String result = String.format(
                        "<br><span style='color:green;'><b>Document ID:</b></span> %d, " +
                                "DocName: %s, Title: %s, Mark: %.2f<br>",
                        scoreDoc.doc,
                        doc.get("NameDoc"),
                        doc.get("Title"),
                        scoreDoc.score
                );
                results.add(result);
            }
        }

        return results;
    }
}
