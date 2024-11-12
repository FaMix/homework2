package com.example;

import java.io.*;
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

import java.nio.file.Path;
import java.nio.file.Paths;

public class search_engine {

    public static void main(String[] args) {
        try {
            // Leer las cadenas desde 'input.txt'
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

            String line1 = reader.readLine();
            String line2 = reader.readLine();
            String line3 = reader.readLine();
            reader.close();

            // Verificar si las líneas son válidas antes de procesarlas
            if (line1 == null || line2 == null || line3 == null) {
                System.err.println("Error: El archivo input.txt no tiene el formato esperado.");
                return;
            }

            // Extraer los valores después de ": "
            String cadena1 = line1.split(": ")[1];
            String cadena2 = line2.split(": ")[1];
            String cadena3 = line3.split(": ")[1];

            // Configurar el directorio del índice preexistente
            Path path = Paths.get("C:/Users/saioa/OneDrive/Escritorio/ROMA_TRE/Ingegneria_dei_dati/homework_2/web_3/src/main/resources/idx2");
            try (Directory directory = FSDirectory.open(path);
                 IndexReader indexReader = DirectoryReader.open(directory);
                 BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {

                IndexSearcher searcher = new IndexSearcher(indexReader);

                // Realizar las búsquedas y guardar resultados en output.txt

                // Búsqueda en el campo "author" usando cadena1
                Query query1 = new TermQuery(new Term("author", cadena1));
                writer.write("Resultados para búsqueda en 'author': " + cadena1 + "\n");
                runQuery(searcher, query1, writer);

                // Búsqueda en el campo "topic" usando cadena2
                Query query2 = new TermQuery(new Term("topic", cadena2));
                writer.write("Resultados para búsqueda en 'topic': " + cadena2 + "\n");
                runQuery(searcher, query2, writer);

                // Búsqueda en el campo "title" usando cadena3
                Query query3 = new TermQuery(new Term("title", cadena3));
                writer.write("Resultados para búsqueda en 'title': " + cadena3 + "\n");
                runQuery(searcher, query3, writer);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: El archivo input.txt no tiene el formato esperado.");
        }
    }

    private static void runQuery(IndexSearcher searcher, Query query, BufferedWriter writer) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        if (hits.scoreDocs.length == 0) {
            writer.write("No se encontraron resultados para esta consulta.\n\n");
        } else {
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                Document doc = searcher.doc(hits.scoreDocs[i].doc);
                writer.write("Document ID: " + hits.scoreDocs[i].doc + 
                             ", Title: " + doc.get("title") + 
                             ", Score: " + hits.scoreDocs[i].score + "\n");
            }
            writer.write("\n"); // Separador entre resultados de consultas
        }
    }
}
