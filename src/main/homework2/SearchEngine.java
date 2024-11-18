package main.homework2;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class SearchEngine {

	public Map<String, List<String>> search(Map<String, Object> inputMap, String typeOfSearch) {
		String[] fields = {"Title", "Authors", "Abstract", "Content"};
		Analyzer[] analyzers = {new WhitespaceAnalyzer(), new StandardAnalyzer(), new EnglishAnalyzer(), new EnglishAnalyzer()};
		Map<String, List<String>> matrixResults = new HashMap<>();
		Path indexPath = Paths.get("../lucene-index");

		try (Directory directory = FSDirectory.open(indexPath);
				IndexReader indexReader = DirectoryReader.open(directory)) {

			IndexSearcher searcher = new IndexSearcher(indexReader);
			int index = 0;

			if (typeOfSearch.equals("Separated")){

				for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
					if (index >= fields.length || index >= analyzers.length) {
						break;
					}
					String valor = entry.getValue().toString();
					if (valor == null || valor.trim().isEmpty()) {
						System.out.println("Skipping empty or null value for key: " + entry.getKey());
						List<String> notFound = new ArrayList<>();
						notFound.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
						matrixResults.put(entry.getKey(),notFound);
						index++;
						continue;
					}
					String userQuery = QueryParser.escape(valor);
					QueryParser queryParser = new QueryParser(fields[index], analyzers[index]);
					Query query = queryParser.parse(userQuery);
					List<String> results = executeQuery(searcher, query);
					matrixResults.put(fields[index], results);
					index++;
				}
			} else if(typeOfSearch.equals("All together")){
				BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
				for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
					String field = entry.getKey();
					String value = entry.getValue().toString();

					if (value == null || value.trim().isEmpty()) {
						System.out.println("Skipping empty or null value for key: " + field);
						index++;
						continue;
					}
					String escapedQuery = QueryParser.escape(value);
					QueryParser queryParser = new QueryParser(fields[index], analyzers[index]);
					Query subQuery = queryParser.parse(escapedQuery);
					booleanQueryBuilder.add(subQuery, BooleanClause.Occur.MUST);
					index++;
				}
				Query combinedQuery = booleanQueryBuilder.build();
				System.out.println("Combined query: " + combinedQuery);
				List<String> results = executeQuery(searcher, combinedQuery);
				matrixResults.put("AllTogether", results);
			}
		} catch (IOException e) {
			System.err.println("Error accessing the index: " + e.getMessage());
		} catch (ParseException e) {
			System.err.println("Error parsing the query: " + e.getMessage());
		}
		matrixResults.forEach((key, value) -> System.out.println(key + " -> " + value));

		return matrixResults;
	}


	private static List<String> executeQuery(IndexSearcher searcher, Query query) throws IOException {
		TopDocs hits = searcher.search(query, 10);
		List<String> values = new ArrayList<>();
		if (hits.scoreDocs.length == 0) {
			values.add("<span style='color:red;'><b>No results found for this query :(</b></span>");
			return values;
		}

		for (int i = 0; i < hits.scoreDocs.length; i++) {
			Document doc = searcher.doc(hits.scoreDocs[i].doc);
			String valor = String.format(
					"<br><span style='color:green;'><b>Document ID:</b></span> %d, " +
					"<span style='color:green;'><b>DocName:</b></span> %s, " +
					"<span style='color:green;'><b>Title:</b></span> %s, " +
					"<span style='color:green;'><b>Mark:</b></span> <b>%f</b><br>",
					hits.scoreDocs[i].doc,
					doc.get("NameDoc") != null ? doc.get("NameDoc") : "N/A",
					doc.get("Title") != null ? doc.get("Title") : "N/A",
					hits.scoreDocs[i].score
			);
			values.add(valor);
		}
		return values;
	}
}
