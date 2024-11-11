package org.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class extraer {
    public static void main(String[] args) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get("../lucene-index")); // El mismo directorio donde guardaste el índice
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Usa el mismo analizador que usaste al indexar
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // Define la consulta
        String queryStr = "ingeniere"; // Cambia esto por lo que quieras buscar
        QueryParser parser = new QueryParser("contenuto", analyzer);
        Query query = parser.parse(queryStr);

        // Realiza la búsqueda y muestra los resultados
        TopDocs results = searcher.search(query, 300); // Busca los 10 primeros resultados
        System.out.println("Resultados encontrados: " + results.totalHits.value);

        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Titulo: " + doc.get("titolo"));
            System.out.println("Contenido: " + doc.get("contenuto"));
            System.out.println("----------");
        }

        reader.close();
        directory.close();
    }
}

