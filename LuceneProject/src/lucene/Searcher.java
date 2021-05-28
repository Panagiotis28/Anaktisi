package lucene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;


public class Searcher {

	
	private static int counter = 0;
	private static IndexReader ireader;
	private static Analyzer analyzer;
	private static IndexSearcher isearcher;
	private static ScoreDoc[] hitsField;


	public void searchPhraseQuery(String field, String q) throws IOException, InvalidTokenOffsetsException {
		
		ireader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData")));
		isearcher = new IndexSearcher(ireader);

		analyzer = new StandardAnalyzer();
		
		Query query = checkBooleanClause(field, q);
		

		TopDocs docs = isearcher.search(query, ireader.numDocs());
		ScoreDoc[] hits = docs.scoreDocs;
		hitsField = hits;
		
		
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

		List<String> test = analyzeSynonyms(q, customAnalyzer());

		JMenu historyWord = new JMenu();
		historyWord.setText(q);
	
		button(historyWord,test);

		printResults(isearcher, hits, field, q, highlighter);
		ireader.close();

		
	}
	
	public static Query checkBooleanClause(String field, String q){
		if(q.contains("AND NOT")) {
			
			String split[] = q.split("\\s+");
			QueryParser parser = new QueryParser(field, analyzer);
			Query query1 = parser.createBooleanQuery(field, split[0]);
			Query query2 = parser.createBooleanQuery(field, split[3]);
			BooleanQuery query = new BooleanQuery.Builder().
					add(query1, BooleanClause.Occur.MUST).add(query2, BooleanClause.Occur.MUST_NOT).build();
			q.replace("AND", "");
			q.replace("NOT", "");
			
			return query;
			
		}else if(q.contains("OR NOT")) {
			
			String split[] = q.split("\\s+");
			QueryParser parser = new QueryParser(field, analyzer);
			Query query1 = parser.createBooleanQuery(field, split[0]);
			Query query2 = parser.createBooleanQuery(field, split[3]);
			BooleanQuery query = new BooleanQuery.Builder().
					add(query1, BooleanClause.Occur.SHOULD).add(query2, BooleanClause.Occur.MUST_NOT).build();
			q.replace("OR", "");
			q.replace("NOT", "");
			return query;
		}else if(q.contains("AND")) {
			
			String split[] = q.split("\\s+");
			QueryParser parser = new QueryParser(field, analyzer);
			Query query1 = parser.createBooleanQuery(field, split[0]);
			Query query2 = parser.createBooleanQuery(field, split[2]);
			BooleanQuery query = new BooleanQuery.Builder().
					add(query1, BooleanClause.Occur.MUST).add(query2, BooleanClause.Occur.MUST).build();
			q.replace("AND", "");
			return query;
			
		}else if(q.contains("OR")) {
			
			String split[] = q.split("\\s+");
			QueryParser parser = new QueryParser(field, analyzer);
			Query query1 = parser.createBooleanQuery(field, split[0]);
			Query query2 = parser.createBooleanQuery(field, split[2]);
			BooleanQuery query = new BooleanQuery.Builder().
					add(query1, BooleanClause.Occur.SHOULD).add(query2, BooleanClause.Occur.SHOULD).build();
			q.replace("OR", "");
			return query;
			
		}else if(q.contains("NOT")) {
			
			
			String split[] = q.split("\\s+");
			QueryParser parser = new QueryParser(field, analyzer);
			
			Query query1 = new MatchAllDocsQuery();
			Query query2 = parser.createBooleanQuery(field, split[1]);
			BooleanQuery query = new BooleanQuery.Builder()
					.add(query1, BooleanClause.Occur.SHOULD)
					.add(query2, BooleanClause.Occur.MUST_NOT).build();
			q.replace("NOT", "");
			return query;
		
		}else {
			QueryParser parser = new QueryParser(field, analyzer);
			parser.setPhraseSlop(0);
			Query query=parser.createPhraseQuery(field,q);
			return query;
		}
		
	}

	public void searchBooleanIndex(String field1, String field2, String q) throws IOException, ParseException {
		ireader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData")));
		isearcher = new IndexSearcher(ireader);
		analyzer = new StandardAnalyzer();

		BooleanClause.Occur[] flags = { BooleanClause.Occur.MUST,BooleanClause.Occur.MUST };
		Query query = MultiFieldQueryParser.parse(q, new String[] { field1, field2 }, flags, analyzer);
		TopDocs docs = isearcher.search(query, ireader.numDocs());
		ScoreDoc[] hits = docs.scoreDocs;
		hitsField=hits;
		List<String> test = analyzeSynonyms(q, customAnalyzer());

		JMenu historyWord = new JMenu();
		historyWord.setText(q);
		
		
		button(historyWord,test);
		printResults(isearcher, hits, field1, field2, q);
		ireader.close();

	}

	

	public static void searchIndex(String field, String q) throws IOException, ParseException, InvalidTokenOffsetsException {

		ireader = DirectoryReader.open(FSDirectory.open(Paths.get("C:\\Users\\Panagiotis\\Desktop\\anak\\indexData")));
		isearcher = new IndexSearcher(ireader);

		analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser(field, analyzer);
		Query query = parser.parse(q);

		TopDocs docs = isearcher.search(query, ireader.numDocs());
		ScoreDoc[] hits = docs.scoreDocs;
		
		hitsField=hits;
		
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		
		List<String> test = analyzeSynonyms(q, customAnalyzer());

		JMenu historyWord = new JMenu();
		historyWord.setText(q);
		
		
		button(historyWord,test);
		
		printResults(isearcher, hits, field, q, highlighter);
		ireader.close();

	}
	
	public static void button(JMenu historyWord,List<String> test) {
		if(!checkIfExists(historyWord)) {
			GUI.getSingletonView().getHistoryMenu().add(historyWord);
		}

		for (int i = 0; i < test.size(); i++) {
			JButton button = new JButton();
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						counter = 0;
						searchIndex("contents", button.getText());
					} catch (IOException | ParseException e1) {

						e1.printStackTrace();
					} catch (InvalidTokenOffsetsException e1) {
						
						e1.printStackTrace();
					}
				}
			});
			button.setText(test.get(i).toString());
			historyWord.add(button);

		}
	}

	
	private static boolean checkIfExists(JMenu historyWord){
		for(int i=0; i<GUI.getSingletonView().getHistoryMenu().getMenuComponentCount();i++) {
			if (GUI.getSingletonView().getHistoryMenu().getMenuComponent(i) instanceof JMenuItem) {
				java.awt.Component comp = GUI.getSingletonView().getHistoryMenu().getMenuComponent(i);
	            JMenuItem menuItem1 = (JMenuItem) comp;
	            if(menuItem1.getText().equals(historyWord.getText())) {
	            	return true;
	            }
	        }
			
		}
		
		return false;
		
	}

	private static CustomAnalyzer customAnalyzer() throws IOException {
		Map<String, String> sargs = new HashMap<>();
		sargs.put("synonyms", "wn_s.pl");
		sargs.put("format", "wordnet");

		CustomAnalyzer.Builder builder = CustomAnalyzer.builder().withTokenizer(StandardTokenizerFactory.class)
				.addTokenFilter(LowerCaseFilterFactory.class)
				.addTokenFilter(StopFilterFactory.class)
				.addTokenFilter(SynonymGraphFilterFactory.class, sargs);
		return builder.build();
	}

	public static List<String> analyzeSynonyms(String text, Analyzer analyzer) throws IOException {
		List<String> result = new ArrayList<String>();
		TokenStream tokenStream = analyzer.tokenStream("", text);
		CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			result.add(attr.toString());
		}
		return result;
	}

	public static void printResults(IndexSearcher isearcher, ScoreDoc[] hits, String field, String q,Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {

		JTextArea textArea = GUI.getSingletonView().getTextArea();

		if (hits.length < counter + 10 && hits.length != 0) {

			textArea.append( "\n"+"There are " + hits.length + " results \n");
			textArea.append("QUERY: " + q + "\n");
			textArea.append("\n");

			for (int i = counter; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				if (field.equals("contents")) {
					textArea.append((i + 1) + ". " + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
				} else {
					textArea.append((i + 1) + ". " + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
					TokenStream tokenStream = TokenSources.getTokenStream(hitDoc, field, analyzer);
					String fragment = highlighter.getBestFragment(tokenStream, hitDoc.get(field));
					textArea.append("Highlighted Text:"+"\n");
					textArea.append(fragment+"\n");
					textArea.append("\n");
				}
			}
			counter = 0;
			return;

		} else if (hits.length == 0) {
			textArea.append("No more results \n");
			textArea.append("\n");
			counter = 0;
			return;
		} else {
			textArea.append("\n");
			textArea.append("QUERY: " + q + "\n");
			textArea.append("\n");

			for (int i = counter; i < counter + 10; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				if (field.equals("contents")) {
					textArea.append((i + 1) + ". " + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
				} else {
					textArea.append((i + 1) + ". " + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
					TokenStream tokenStream = TokenSources.getTokenStream(hitDoc, field, analyzer);
					String fragment = highlighter.getBestFragment(tokenStream, hitDoc.get(field));
					textArea.append("Highlighted Text:"+"\n");
					textArea.append(fragment+"\n");
					textArea.append("\n");
				}

			}
			counter = counter + 10;
		}

	}
	public static void printResults(IndexSearcher isearcher, ScoreDoc[] hits, String field1, String field2, String q)
			throws IOException {
		JTextArea textArea = GUI.getSingletonView().getTextArea();

		if (hits.length < counter + 10 && hits.length != 0) {

			textArea.append("There are " + hits.length + " results \n");
			textArea.append("\n");
			textArea.append("QUERY: " + q + "\n");
			textArea.append("\n");

			for (int i = counter; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				textArea.append((i + 1) + ".\n" + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
				//textArea.append(hitDoc.get(field2) + " score=" + hits[i].score + "\n");

			}
			counter = 0;
			return;
		} else if (hits.length == 0) {
			textArea.append("No more results \n");
			textArea.append("\n");
			counter = 0;
			return;
		} else {
			textArea.append("\n");
			textArea.append("QUERY: " + q + "\n");
			textArea.append("\n");
			for (int i = counter; i < counter + 10; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);
				textArea.append((i + 1) + ".\n" + hitDoc.get("filename") + " score=" + hits[i].score + "\n");
				//textArea.append(hitDoc.get(field2) + " score=" + hits[i].score + "\n");

			}
			counter = counter + 10;

		}

	}

	public static IndexReader getIreader() {
		return ireader;
	}
	public static IndexSearcher getIsearcher() {
		return isearcher;
	}

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		Searcher.counter = counter;
	}


	public static ScoreDoc[] getHits() {
		return hitsField;
	}


	public static void setHits(ScoreDoc[] hits) {
		Searcher.hitsField = hits;
	}
	
	

}
