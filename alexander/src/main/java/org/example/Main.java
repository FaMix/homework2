package org.example;

import java.awt.*;
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

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.html")) {
            for (Path filePath : stream) {
                System.out.print(filePath.getFileName() + " ---> "); //comprobacion de que el path es correcto
                String resultado = xpath(filePath);
                System.out.println(resultado);
            }
        } catch (IOException e){
            System.out.println("Error al leer los archivos htmls" + e.getMessage());
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

    public static String xpath(Path filePath){// COGER LOS XPATH Y PASARLOS AL MAIN PARA HACER EL INDEX
        //Recuerda que el titulo es del tipo h1class

        try {
            String htmlContenido = Files.readString(filePath);

            Document jsoupDoc = Jsoup.parse(htmlContenido);

            W3CDom w3cDom = new W3CDom();
            org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);

            XPath xpath = XPathFactory.newInstance().newXPath();

            //TITULOS
            //si son con span y/o br
            String expresionXpath1 = "normalize-space(//h1[@class='ltx_title ltx_title_document']/span)";
            XPathExpression expression = xpath.compile(expresionXpath1);
            String expresionXPathTexto = "normalize-space(//h1[@class='ltx_title ltx_title_document']/span/following-sibling::text())";
            XPathExpression expressionTexto = xpath.compile(expresionXPathTexto);
            String expresionXPathBr = "normalize-space(//h1[@class='ltx_title ltx_title_document']/br/following-sibling::text())";
            XPathExpression expressionBr = xpath.compile(expresionXPathBr);
            String resultado = (String) expression.evaluate(w3cDoc, XPathConstants.STRING);
            String resultadoTexto = (String) expressionTexto.evaluate(w3cDoc, XPathConstants.STRING);
            String resultadoBr = (String) expressionBr.evaluate(w3cDoc, XPathConstants.STRING);
            // Evaluar la expresión XPath y obtener el texto directamente
            if (resultadoTexto != null && !resultadoTexto.isEmpty()) {
                resultado = resultado + resultadoTexto;
            }
            if (resultadoBr != null && !resultadoBr.isEmpty() && !resultado.isEmpty()){
                resultado = resultado + " " +resultadoBr;
            }

            //si son solamente texto
            String expresionXpath2 = "normalize-space(//h1[@class='ltx_title ltx_title_document']/text())";
            XPathExpression expression2 = xpath.compile(expresionXpath2);
            String expresionXpath3 = "normalize-space(//h1[@class='ltx_title ltx_title_document']/br/following-sibling::text())";
            XPathExpression expression3 = xpath.compile(expresionXpath3);
            String resultado2 = (String) expression2.evaluate(w3cDoc, XPathConstants.STRING);
            String resultado3 = (String) expression3.evaluate(w3cDoc, XPathConstants.STRING);

            // Evaluar la expresión XPath y obtener el texto directamente
            if (resultado3 != null && !resultado3.isEmpty()) {
                resultado2 = resultado2 + " " + resultado3;
            }
            //-------------------------------------------------------------------------------------------

            //en orden para que no afecte, con sus caso
            if (resultado.isEmpty()) {
                if (resultado2.isEmpty()) {
                    return "NO TITLE";
                } else {
                    return resultado2;
                }
            } else {
                return resultado; // Retornar el texto extraído
            }

            //return expression.evaluate(jsoupDoc);

        } catch (IOException | XPathExpressionException e){
            return "Error al procesar el archivo";
        }
    }
}
