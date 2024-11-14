package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
import org.springframework.web.bind.annotation.MatrixVariable;

public class SearchEngine {

    public static Map<String, List<String>> search(Map<String, Object> mapa) {
        //Scanner scanner = new Scanner(System.in);
        String[] fields = {"Title","Authors", "Abstract", "Content"};
        int fieldsLength = fields.length;
        String[][] arrays = new String[fields.length][];
        Map<String, List<String>> matrixResults = new HashMap<>();

        int index = 0;
        for (Map.Entry<String, Object> entry : mapa.entrySet()){
            String a= entry.getValue().toString();
            arrays[index] = a.split("\\s+");
            index++;
            if (index >= 4){
                break;
            }
        }

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
                    //System.out.println("\nResults for " + fields[j] + ": ");
                    PhraseQuery query = builders[j].build();
                    List<String> results = runQuery(searcher, query);
                    matrixResults.put(fields[j], results);
                    //matrixResults[j] = results.toArray(new String[0]);
                    //matrixResults.put(fields[j], results.toArray(new String[0]));
                }

                //matrixResults.forEach((key, value) -> System.out.println(key + " -> " + Arrays.toString((String[]) value)));

            }
        } catch (IOException e) {
            System.err.println("Error when trying to access the index: " + e.getMessage());
        }

        matrixResults.forEach((field, words) -> {
            System.out.println("[" + field + ", " + words + "]");
        });
        return matrixResults;
    }

    private static List<String> runQuery(IndexSearcher searcher, Query query) throws IOException {

        TopDocs hits = searcher.search(query, 10);
        List<String> values = new ArrayList<>();
        if (hits.scoreDocs.length == 0) {
            values.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
            return  values;
        } else {
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                Document doc = searcher.doc(hits.scoreDocs[i].doc);
                String valor = "<br><span style='color:green;'><b>Document ID:</b></span> " + hits.scoreDocs[i].doc +
                        ", DocName: " + doc.get("NameDoc") +
                        ", Title: " + doc.get("Title") +
                        ", Mark: " + hits.scoreDocs[i].score + "<br>";
                values.add(valor);
            }
            return values;
        }
    }
}
