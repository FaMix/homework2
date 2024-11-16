package org.example;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.*;//libreria para abrir el path de los htmls
import java.util.Collections;
import java.util.concurrent.*;

import org.apache.lucene.util.ThreadInterruptedException;
import org.jsoup.Jsoup; //librerias para hacer los xpath
import org.jsoup.nodes.Document;
import org.jsoup.helper.W3CDom;
import javax.xml.xpath.*;
import org.w3c.dom.NodeList;


//import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class Main {

    private static double start_time, end_time;


    public static void main(String[] args) throws IOException {

        String path = "../lucene-index";
        Path dirPath = Paths.get("../htmls2");

        List<String> listaNombres = Collections.synchronizedList(new ArrayList<>());
        List<String> listaTitulos = Collections.synchronizedList(new ArrayList<>());
        List<String> listaContenido = Collections.synchronizedList(new ArrayList<>());
        List<String> listaAutores = Collections.synchronizedList(new ArrayList<>());
        List<String> listaAbstract = Collections.synchronizedList(new ArrayList<>());

        start_time = System.nanoTime();
        //int nThreads = Runtime.getRuntime().availableProcessors();
        //ExecutorService executor = Executors.newFixedThreadPool(nThreads);



        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.html")) {
            for (Path filePath : stream) {

                    try {
                        Xpaths extraer = new Xpaths(filePath);

                        listaNombres.add(filePath.getFileName().toString());
                        listaTitulos.add(extraer.titulos());
                        listaAutores.add(extraer.autores());
                        listaContenido.add(extraer.contenido());
                        listaAbstract.add(extraer.abstracto());
                    } catch (Exception e) {
                        System.out.println("Error when procesing the HTML file -> " + listaNombres.get(listaNombres.size() - 1) +" ---> Error: "+ e.getMessage());
                    }

            }
        } catch (IOException e) {
            System.out.println("Error when reading HTML files: " + e.getMessage());
        }

//        executor.shutdown();
//        try{
//            executor.awaitTermination(1, TimeUnit.HOURS);
//        }catch (Exception e){
//            System.out.println("Error when waiting threads -> " + e.getMessage());
//        };

        end_time = System.nanoTime();
        double xpath_time = (end_time - start_time)/1000000;

        start_time =0;
        end_time =0;

        start_time = System.nanoTime();
        Indexer index = new Indexer();
        index.index(path, listaNombres, listaTitulos, listaAutores, listaAbstract, listaContenido);
        end_time = System.nanoTime();
        double indexer_time = (end_time - start_time)/1000000;

        System.out.println("Xpath time = "+ xpath_time + " ms");
        System.out.println("Indexer time = "+ indexer_time + " ms");
        System.out.println("Ciao");
    }
}
