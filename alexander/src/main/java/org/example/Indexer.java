package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.*;


public class Indexer {

    public void index(String filePath,List<String> nombres ,List<String> titulos, List<String> autores, List<String> contenido, List<String> abstracto) throws IOException{
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath)); // Define where to save Lucene index

            Analyzer defaultAnalyzer = new StandardAnalyzer();

            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
            perFieldAnalyzers.put("Tittle", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("Authors", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("Abstract", new EnglishAnalyzer());
            perFieldAnalyzers.put("Content", new EnglishAnalyzer());


            Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setCodec(new SimpleTextCodec()); // Configuración de SimpleTextCodec para texto plano
            IndexWriter writer = new IndexWriter(directory, config);

            for (int i=0; i<titulos.size(); i++){
                Document doc = new Document();
                doc.add(new StringField("NameDoc", nombres.get(i), Field.Store.YES));
                doc.add(new StringField("Tittle", titulos.get(i), Field.Store.YES));
                doc.add(new StringField("Authors", autores.get(i), Field.Store.YES));
                doc.add(new TextField("Abstract", abstracto.get(i), Field.Store.YES));
                doc.add(new TextField("Content", contenido.get(i), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.commit(); // persist changes to the disk
            writer.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
