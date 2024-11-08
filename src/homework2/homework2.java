package homework2;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class homework2 {

	public static void main(String[] args) throws IOException {
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
		
		System.out.println("Ciao");
	}
}
