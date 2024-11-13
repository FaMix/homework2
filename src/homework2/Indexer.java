package homework2;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


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
