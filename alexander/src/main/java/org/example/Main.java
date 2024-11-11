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
        //Directory directory = FSDirectory.open(Paths.get("../lucene-index")); // Define where to save Lucene index
        //CREAR EL DIRECTORIO PARA ACCEDER AL LOS HTMLS


        //PASAR LOS HTMLS A LA FUNCION XPATH PARA QUE EXTRAIGA EL TITULO
        Path dirPath = Paths.get("../htmls2");

        List<String> listaTitulos = new ArrayList<>();
        List<String> listaContenido = new ArrayList<>();
        List<String> listaAutores = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.html")) {

            for (Path filePath : stream) {
                Xpaths extraer = new Xpaths(filePath); //llamo al archivo Xpaths
                String titulos = filePath.getFileName() + " ---> " + extraer.titulos();
                listaTitulos.add(titulos);
                String autores = filePath.getFileName() + " ---> " + extraer.autores();
                listaAutores.add(autores);
                //String contenido = filePath.getFileName() + " ---> " + extraer.contenido();
                //listaContenido.add(contenido);
            }
        } catch (IOException e){
            System.out.println("Error al leer los archivos htmls" + e.getMessage());
        }
        for (String a : listaAutores) {
            System.out.println(a);
        }
        /*
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig()); // Define an IndexWriter
        Document doc1 = new Document();
        doc1.add(new TextField("titolo", "Come diventare un ingegnere dei dati, Data Engineer?", Field.Store.YES));
        doc1.add(new TextField("contenuto", "Sembra che oggigiorno tutti vogliano diventare un Data Scientist ...", Field.Store.YES));
        Document doc2 = new Document();
        doc2.add(new TextField("titolo", "Curriculum Ingegneria dei Dati - Sezione di Informatica e Automazione", Field.Store.YES));
        doc2.add(new TextField("contenuto", "Curriculum. Ingegneria dei Dati. Laurea Magistrale in Ingegneria Informatica ...", Field.Store.YES));
        writer.addDocument(doc1); // add Documents to be indexed
        writer.addDocument(doc2);
        writer.commit(); // persist changes to the disk
        writer.close();
        */
        System.out.println("Ciao");
    }
}
