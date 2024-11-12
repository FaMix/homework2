package org.example;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.*;//libreria para abrir el path de los htmls

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

    public static void main(String[] args) throws IOException {
        String path = "../lucene-index"; // Define where to save Lucene index

        //PASAR LOS HTMLS A LA FUNCION XPATH PARA QUE EXTRAIGA EL TITULO
        Path dirPath = Paths.get("../htmls2");

        List<String> listaNombres = new ArrayList<>();
        List<String> listaTitulos = new ArrayList<>();
        List<String> listaContenido = new ArrayList<>();
        List<String> listaAutores = new ArrayList<>();
        List<String> listaAbstract = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.html")) {

            for (Path filePath : stream) {
                Xpaths extraer = new Xpaths(filePath); //llamo al archivo Xpaths
                String nombres = filePath.getFileName() + "";
                listaNombres.add(nombres);
                String titulos = extraer.titulos();
                listaTitulos.add(titulos);
                String autores = extraer.autores();
                listaAutores.add(autores);
                String contenido = extraer.contenido();
                listaContenido.add(contenido);
                String abstracto = extraer.abstracto();
                listaAbstract.add(abstracto);

            }
        } catch (IOException e){
            System.out.println("Error al leer los archivos htmls" + e.getMessage());
        }

        Indexer index = new Indexer();
        index.index(path, listaNombres, listaTitulos, listaAutores, listaAbstract, listaContenido);
        System.out.println("Ciao");
    }
}
