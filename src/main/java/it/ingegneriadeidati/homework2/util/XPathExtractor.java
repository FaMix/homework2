package it.ingegneriadeidati.homework2.util;

import java.io.IOException;
import java.nio.file.*; // Library for opening file paths
import org.jsoup.Jsoup; // Libraries for XPath
import org.jsoup.nodes.Document;
import org.jsoup.helper.W3CDom;
import javax.xml.xpath.*;
import org.w3c.dom.NodeList;

public class XPathExtractor {

    private Path filePath;
    private String htmlContent;
    private Document jsoupDoc;
    private W3CDom w3cDom = new W3CDom();
    private org.w3c.dom.Document w3cDoc;
    private XPath xpath = XPathFactory.newInstance().newXPath();

    // Constructor
    public XPathExtractor(Path filePath) {
        try {
            this.filePath = filePath;
            this.htmlContent = Files.readString(this.filePath);
            this.jsoupDoc = Jsoup.parse(this.htmlContent);
            this.w3cDoc = this.w3cDom.fromJsoup(this.jsoupDoc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extract title from the document
    public String extractTitle() {
        try {
            String xpathExpression = "//h1[@class='ltx_title ltx_title_document']//text()";
            XPathExpression expression = this.xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expression.evaluate(this.w3cDoc, XPathConstants.NODESET);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeText = nodes.item(i).getTextContent().trim();
                if (!nodeText.isEmpty()) {
                    result.append(nodeText).append(" ");
                }
            }
            String output = result.toString().replaceAll("\\s+", " ").trim();

            return output.isEmpty() ? "NO TITLE" : output;

        } catch (XPathExpressionException e) {
            return "Error processing the file";
        }
    }

    // Extract abstract from the document
    public String extractAbstract() {
        try {
            String xpathExpression = "//div[@class='ltx_abstract']//text()";
            XPathExpression expression = this.xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expression.evaluate(this.w3cDoc, XPathConstants.NODESET);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeText = nodes.item(i).getTextContent().trim();
                if (!nodeText.isEmpty()) {
                    result.append(nodeText).append(" ");
                }
            }
            String output = result.toString().replaceAll("\\r?\\n", " ").trim();
            output = output.replaceAll("\\s+", " ").trim();

            return output.isEmpty() ? "NO ABSTRACT" : output;

        } catch (XPathExpressionException e) {
            return "Error processing the file";
        }
    }

    // Extract content from the document
    public String extractContent() {
        try {
            String xpathExpression = "//section[@class='ltx_section']//div[@class='ltx_para']//text() | //section[@class='ltx_section']//h2[contains(@class,'ltx_title')]//text()";
            XPathExpression expression = this.xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expression.evaluate(this.w3cDoc, XPathConstants.NODESET);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeText = nodes.item(i).getTextContent().trim();
                if (!nodeText.isEmpty()) {
                    result.append(nodeText).append(" ");
                }
            }
            String output = result.toString().replaceAll("\\s+", " ").trim();

            return output.isEmpty() ? "NO CONTENT" : output;

        } catch (XPathExpressionException e) {
            return "Error processing the file";
        }
    }

    // Extract authors from the document
    public String extractAuthors() {
        try {
            String xpathExpression = "//span[@class='ltx_personname']//text()[not(ancestor::sup) and not(ancestor::span[contains(@class,'ltx_note')]) and not(ancestor::math)]";
            XPathExpression expression = this.xpath.compile(xpathExpression);
            NodeList nodes = (NodeList) expression.evaluate(this.w3cDoc, XPathConstants.NODESET);

            StringBuilder result = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeText = nodes.item(i).getTextContent().trim();
                if (!nodeText.isEmpty()) {
                    result.append(nodeText).append(" ");
                }
            }
            String output = result.toString().replaceAll("\\r?\\n", " ").trim();
            output = output.replaceAll("\\s+", " ").trim();

            return output.isEmpty() ? "NO AUTHORS" : output;

        } catch (XPathExpressionException e) {
            return "Error processing the file";
        }
    }
}
