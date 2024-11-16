package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

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
import org.apache.lucene.queryparser.classic.*;

public class SearchEngine {

    public static Map<String, List<String>> search(Map<String, Object> inputMap) {
        String[] fields = {"Title", "Authors", "Abstract", "Content"};
        Analyzer[] analyzers = {new WhitespaceAnalyzer(), new StandardAnalyzer(), new EnglishAnalyzer(), new EnglishAnalyzer()};
        Map<String, List<String>> matrixResults = new HashMap<>();
        Path indexPath = Paths.get("../lucene-index");

        try (Directory directory = FSDirectory.open(indexPath);
             IndexReader indexReader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(indexReader);
            int index = 0;

            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                if (index >= fields.length || index >= analyzers.length) {
                    break;
                }
                String valor = entry.getValue().toString();
                if (valor == null || valor.trim().isEmpty()) {
                    System.out.println("Skipping empty or null value for key: " + entry.getKey());
                    List<String> notFound = new ArrayList<>();
                    notFound.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
                    matrixResults.put(entry.getKey(),notFound);
                    index++;
                    continue;
                }
                String userQuery = QueryParser.escape(valor);
                QueryParser queryParser = new QueryParser(fields[index], analyzers[index]);
                Query query = queryParser.parse(userQuery);
                List<String> results = runQuery(searcher, query);
                matrixResults.put(fields[index], results);
                index++;
            }

        } catch (IOException e) {
            System.err.println("Error accessing the index: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Error parsing the query: " + e.getMessage());
        }
        matrixResults.forEach((key, value) -> System.out.println(key + " -> " + value));

        return matrixResults;
    }

    private static List<String> runQuery(IndexSearcher searcher, Query query) throws IOException {

        TopDocs hits = searcher.search(query, 10);
        List<String> values = new ArrayList<>();
        if (hits.scoreDocs.length == 0) {
            values.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
            return values;
        } else {
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                Document doc = searcher.doc(hits.scoreDocs[i].doc);
                String valor = "<br><span style='color:green;'><b>Document ID:</b></span> " + hits.scoreDocs[i].doc +
                        ", <span style='color:green;'><b>DocName</b></span>: " + doc.get("NameDoc") +
                        ", <span style='color:green;'><b>Title:</b></span> " + doc.get("Title") +
                        ", <span style='color:green;'><b>Mark:</b></span> " + "<b> "+hits.scoreDocs[i].score + "</b>" + "<br>";
                values.add(valor);
            }
            return values;
        }
    }
}
