package org.example;

import java.io.IOException;
import java.nio.file.*;//libreria para abrir el path de los htmls

import org.jsoup.Jsoup; //librerias para hacer los xpath
import org.jsoup.nodes.Document;
import org.jsoup.helper.W3CDom;
import javax.xml.xpath.*;
import org.w3c.dom.NodeList;


public class Xpaths {

    public Path filePath;
    public String htmlContenido;
    public Document jsoupDoc;
    public W3CDom w3cDom = new W3CDom();
    public org.w3c.dom.Document w3cDoc;
    public XPath xpath = XPathFactory.newInstance().newXPath();

    public Xpaths(Path filePath1){
        try{
            this.filePath = filePath1;
            this.htmlContenido = Files.readString(this.filePath);
            this.jsoupDoc = Jsoup.parse(this.htmlContenido);
            this.w3cDoc = this.w3cDom.fromJsoup(this.jsoupDoc);


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String titulos(){// COGER LOS XPATH Y PASARLOS AL MAIN PARA HACER EL INDEX
        //Recuerda que el titulo es del tipo h1class

        try {

            //TITULOS
            String expresionXpath1 = "//h1[@class='ltx_title ltx_title_document']//text()";
            XPathExpression expression = this.xpath.compile(expresionXpath1);
            NodeList nodes = (NodeList) expression.evaluate(this.w3cDoc, XPathConstants.NODESET);

            String resultado = "";

            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeText = nodes.item(i).getTextContent().trim();
                if (!nodeText.isEmpty()) {
                    resultado += nodeText + " ";
                }
            }
            resultado = resultado.replaceAll("\\s+", " ");  // Reemplaza múltiples espacios por uno solo
            resultado = resultado.trim(); // Elimina los espacios al principio y al final

            if (resultado.isEmpty()){
                return "NO TITLE";
            }
            return resultado;

        } catch (XPathExpressionException e){
            return "Error al procesar el archivo";
        }
    }

    public String contenido(){
        return "";
    }

    public String autores(){
        return "";
    }
}


