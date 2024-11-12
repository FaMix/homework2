package homework2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class homework2 {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		Directory directory = FSDirectory.open(Paths.get("../lucene-index")); // Define where to save Lucene index
		
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
		
        File input = new File("../htmls2/2107.01477.html");
        
        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(input, null);
       
        W3CDom w3cDom = new W3CDom();
        org.w3c.dom.Document xmlDoc = w3cDom.fromJsoup(jsoupDoc);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        String authorExpression = "//span[@class='ltx_personname']//text()[not(ancestor::sup) and not( ancestor::span[contains(@class,'ltx_note')] ) and not(ancestor::math)]"; // Modifica l'espressione in base al tuo file HTML
        XPathExpression xPathAuthorExpression = xpath.compile(authorExpression);
        
        String abstractExpression = "//div[@class='ltx_abstract']//text()";
        XPathExpression xPathAbstractExpression = xpath.compile(abstractExpression);
        
        String contentExpression = "//section[@class='ltx_section']//div[@class='ltx_para']//text() | //section[@class='ltx_section']//h2[contains(@class,'ltx_title')]//text()";
        XPathExpression xPathContentExpression = xpath.compile(contentExpression);

        NodeList nodes = (NodeList) xPathContentExpression.evaluate(xmlDoc, XPathConstants.NODESET);

        String result = "";
        // Stampa i risultati
        for (int i = 0; i < nodes.getLength(); i++) {
        	result = result + nodes.item(i).getTextContent();
        }
		
        //String output = result.replaceAll("\\r?\\n", "");
        System.out.println(result);
	}
}
