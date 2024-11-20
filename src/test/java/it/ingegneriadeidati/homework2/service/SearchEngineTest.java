package it.ingegneriadeidati.homework2.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import it.ingegneriadeidati.homework2.ServerApplication;

@SpringBootTest
public class SearchEngineTest {
	
	private SearchEngine searchEngine;

	private final String SEPARATED = "Separated";
	private final String ALL_TOGETHER = "All together";
	private final String AUTHORS_1 = "Yilun Jin Xiguang Wei Yang Liu";
	private final String AUTHORS_2 = "Adit Jain, Cornell University";
	private final String AUTHORS_3 = "Zixi Wang";
	private final String AUTHORS_4 = "Jiaqi Wang Yuzhong";
	private final String AUTHORS_5 = "Fan Zhang";
	private final String AUTHORS_6 = "Juan Antonio";
	private final String TITLE_1 = "Textual and Tabular Data";
	private final String TITLE_2 = "Federated Learning";
	private final String TITLE_3 = "Rethinking";
	private final String TITLE_4 = "Exploiting Defenses against GAN-Based Feature Inference Attacks in Federated Learning";
	private final String TITLE_5 = "Curated Datasets and Neural Models for Machine Translation";
	private final String ABSTRACT_1 = "makes detecting these failures and attacks a challenging task";
	private final String ABSTRACT_2 = "The ability of zero-shot translation emerges when we train a multilingual model"
			+ " with certain translation directions; the model can then directly translate in unseen directions."
			+ " Alternatively, zero-shot translation can be accomplished by pivoting through a third language (e.g., English)."
			+ " In our work, we observe that both direct and pivot translations are noisy and achieve less satisfactory performance."
			+ " We propose EBBS, an ensemble method with a novel bi-level beam search algorithm, where each ensemble component explores"
			+ " its own prediction step by step at the lower level but they are synchronized by a “soft voting” mechanism"
			+ " at the upper level. Results on two popular multilingual translation datasets show that EBBS"
			+ " consistently outperforms direct and pivot translations as well as existing ensemble techniques."
			+ " Further, we can distill the ensemble’s knowledge back to the multilingual model to improve inference efficiency;"
			+ " profoundly, our EBBS-based distillation does not sacrifice, or even improves, the translation quality.";
	private final String ABSTRACT_3 = "The Mayan language family is spoken in an area";
	private final String CONTENT_1 = "mobile devices";
	private final String CONTENT_2 ="Most existing personalized federated learning approaches are based on intricate designs, "
			+ "which often require complex implementation and tuning.";
	private final String CONTENT_3 = "a language family with an ancient history";

	@Before
	public void setUp() {
		this.searchEngine = new SearchEngine();
	}
	
	@Test
    public void contextLoads() {
        assertNotNull("SearchEngine should not be null", searchEngine);
    }

	@Test
	public void testSearchByAuthors() {
		Map<String, Object> authorsMap = new LinkedHashMap<>();
		authorsMap.put("Title", "");
		authorsMap.put("Authors", AUTHORS_1);
		authorsMap.put("Abstract", "");
		authorsMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(authorsMap, SEPARATED);
		List<String> results = searchResult.get("Authors");
		assertTrue("Expected document 1901.08755.html not found", results.get(0).contains("1901.08755.html"));
	}

	@Test
	public void testSearchByTitle() {
		Map<String, Object> titleMap = new LinkedHashMap<>();
		titleMap.put("Title", TITLE_1);
		titleMap.put("Authors", "");
		titleMap.put("Abstract", "");
		titleMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(titleMap, SEPARATED);
		List<String> results = searchResult.get("Title");
		assertTrue("Expected document 2005.08314v1.html not found", results.get(0).contains("2005.08314v1.html"));
	}

	@Test
	public void testSearchByAbstract() {
		Map<String, Object> abstractMap = new LinkedHashMap<>();
		abstractMap.put("Title", "");
		abstractMap.put("Authors", "");
		abstractMap.put("Abstract", ABSTRACT_1);
		abstractMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(abstractMap, SEPARATED);
		List<String> results = searchResult.get("Abstract");
		assertTrue("Expected document 2110.14074.html not found", results.get(0).contains("2110.14074.html"));
	}

	@Test
	public void testSearchByContent() {
		Map<String, Object> contentMap = new LinkedHashMap<>();
		contentMap.put("Title", "");
		contentMap.put("Authors", "");
		contentMap.put("Abstract", "");
		contentMap.put("Content", CONTENT_1);
		Map<String, List<String>> searchResult = searchEngine.search(contentMap, SEPARATED);
		List<String> results = searchResult.get("Content");
		assertTrue("Expected document 2309.13643.html not found", results.get(0).contains("2309.13643.html"));
	}

	@Test
	public void testSearchByAuthorsAndInstitution() {
		Map<String, Object> authorsMap = new LinkedHashMap<>();
		authorsMap.put("Title", "");
		authorsMap.put("Authors", AUTHORS_2);
		authorsMap.put("Abstract", "");
		authorsMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(authorsMap, SEPARATED);
		List<String> results = searchResult.get("Authors");
		assertTrue("Expected document 2308.08825.html not found", results.get(0).contains("2308.08825.html"));
	}

	@Test
	public void testSearchByTitleAndAuthors() {
		Map<String, Object> twoFieldsMap = new LinkedHashMap<>();
		twoFieldsMap.put("Title", TITLE_2);
		twoFieldsMap.put("Authors", AUTHORS_3);
		twoFieldsMap.put("Abstract", "");
		twoFieldsMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(twoFieldsMap, ALL_TOGETHER);
		List<String> results = searchResult.get("AllTogether");
		assertTrue("Expected document 2312.05761.html not found", results.get(0).contains("2312.05761.html"));
	}

	@Test
	public void testSearchByTitleAuthorsAndContent() {
		Map<String, Object> threeFieldsMap = new LinkedHashMap<>();
		threeFieldsMap.put("Title", TITLE_3);
		threeFieldsMap.put("Authors", AUTHORS_4);
		threeFieldsMap.put("Abstract", "");
		threeFieldsMap.put("Content", CONTENT_2);
		Map<String, List<String>> searchResult = searchEngine.search(threeFieldsMap, ALL_TOGETHER);
		List<String> results = searchResult.get("AllTogether");
		assertTrue("Expected document 2401.15874.html not found", results.get(0).contains("2401.15874.html"));
	}

	@Test
	public void testSearchByFullAbstract() {
		Map<String, Object> fullAbstractMap = new LinkedHashMap<>();
		fullAbstractMap.put("Title", "");
		fullAbstractMap.put("Authors", "");
		fullAbstractMap.put("Abstract", ABSTRACT_2);
		fullAbstractMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(fullAbstractMap, SEPARATED);
		List<String> results = searchResult.get("Abstract");
		assertTrue("Expected document 2403.00144.html not found", results.get(0).contains("2403.00144.html"));
	}
	
	@Test
	public void testSearchByAuthorsAndFullTitle() {
		Map<String, Object> twoFieldsMap = new LinkedHashMap<>();
		twoFieldsMap.put("Title", TITLE_4);
		twoFieldsMap.put("Authors", AUTHORS_5);
		twoFieldsMap.put("Abstract", "");
		twoFieldsMap.put("Content", "");
		Map<String, List<String>> searchResult = searchEngine.search(twoFieldsMap, ALL_TOGETHER);
		List<String> results = searchResult.get("AllTogether");
		assertTrue("Expected document 2004.12571.html not found", results.get(0).contains("2004.12571.html"));
	}

	@Test
	public void testSearchByAllFields() {
		Map<String, Object> allFieldsMap = new LinkedHashMap<>();
		allFieldsMap.put("Title", TITLE_5);
		allFieldsMap.put("Authors", AUTHORS_6);
		allFieldsMap.put("Abstract", ABSTRACT_3);
		allFieldsMap.put("Content", CONTENT_3);
		Map<String, List<String>> searchResult = searchEngine.search(allFieldsMap, ALL_TOGETHER);
		List<String> results = searchResult.get("AllTogether");
		assertTrue("Expected document 2404.07673.html not found", results.get(0).contains("2404.07673.html"));
	}

	@Test
	public void testSearchAllDocuments() {
		Directory directory;
		IndexReader indexReader;
		try {
			directory = FSDirectory.open(Paths.get("../lucene-index"));
			indexReader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			Query allDocsQuery = new MatchAllDocsQuery();
			var topDocs = searcher.search(allDocsQuery, 8190);
			assertEquals(8190, topDocs.totalHits.value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
