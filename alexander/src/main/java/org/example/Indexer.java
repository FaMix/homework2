package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
            //int nThreads = Runtime.getRuntime().availableProcessors();
            //ExecutorService executor = Executors.newFixedThreadPool(nThreads);

            Directory directory = FSDirectory.open(Paths.get(filePath)); // Define where to save Lucene index

            Analyzer defaultAnalyzer = new StandardAnalyzer();

            Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
            perFieldAnalyzers.put("Title", new WhitespaceAnalyzer());
            perFieldAnalyzers.put("Authors", new StandardAnalyzer());
            perFieldAnalyzers.put("Abstract", new EnglishAnalyzer());
            perFieldAnalyzers.put("Content", new EnglishAnalyzer());


            Analyzer analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer, perFieldAnalyzers);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setCodec(new SimpleTextCodec()); // We prefer to create files in binary rather than on .scf or plain text, because it is more efficient
            IndexWriter writer = new IndexWriter(directory, config);



            for (int i = 0; i < titulos.size(); i++) {
                final int index = i;

                    try {
                        Document doc = new Document();
                        doc.add(new TextField("NameDoc", nombres.get(index), Field.Store.YES));
                        doc.add(new TextField("Title", titulos.get(index), Field.Store.YES));
                        doc.add(new TextField("Authors", autores.get(index), Field.Store.YES));
                        doc.add(new TextField("Abstract", abstracto.get(index), Field.Store.YES));
                        doc.add(new TextField("Content", contenido.get(index), Field.Store.YES));

                        writer.addDocument(doc);

                    } catch (Exception e) {
                        System.out.println("Error when writing docs in the Indexer file");
                        e.printStackTrace();
                    }

            }

            //executor.shutdown();
//            try{
//                executor.awaitTermination(1, TimeUnit.HOURS);
//            }catch (Exception e){
//                System.out.println("Error when waiting threads -> " + e.getMessage());
//            };

            writer.commit(); // persist changes to the disk
            writer.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
