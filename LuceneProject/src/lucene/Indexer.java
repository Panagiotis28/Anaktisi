package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;


public class Indexer {
	
	
	private static IndexWriter writer;
	private static Analyzer analyzer = new StandardAnalyzer();
	private static FSDirectory directory;
	
	public void initIndex(String indexDir) throws IOException {
		
		directory = FSDirectory.open(new File(indexDir).toPath());
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
		
	}
	public void addDocuments() throws IOException {
		
		FileReader fileReader = new FileReader(new File("C:\\Users\\Panagiotis\\Desktop\\anak\\euretirio.txt"));
		FileReader dateReader = new FileReader(new File("C:\\Users\\Panagiotis\\Desktop\\anak\\date.txt"));
        BufferedReader br = new BufferedReader(fileReader);
        BufferedReader br2 = new BufferedReader(dateReader);
        String line = null;
        
        while ((line = br.readLine()) != null) {
        	
        	Document doc = new Document();
        	
        	String date = br2.readLine();
        	//doc.add(new SortedDocValuesField("published_time_group", new BytesRef(date)));
        	doc.add(new Field("published_time",date,TextField.TYPE_STORED));

        	doc.add(new Field("filename",line,TextField.TYPE_STORED));
        	br.readLine();
        	
        	String contents = "";
        	String title = br.readLine();
        	contents+=title;
        	doc.add(new Field("title",title,TextField.TYPE_STORED));
        	br.readLine();
        	
        	String authors = br.readLine();
        	contents+=authors;
        	
        	doc.add(new Field("authors",authors,TextField.TYPE_STORED));
        	br.readLine();
        	
        	String ab = br.readLine();
        	contents+=ab;
        	doc.add(new Field("abstract",ab,TextField.TYPE_STORED));
        	br.readLine();
        	
        	String bodyText = br.readLine();
        	contents+=bodyText;
        	doc.add(new Field("bodyText",bodyText,TextField.TYPE_STORED));
        	br.readLine();
        	
        	doc.add(new Field("contents",contents,TextField.TYPE_STORED));       	
        	writer.addDocument(doc);
        	
        	
        }
        writer.close();
        directory.close();
        
		
	}
	
	
	
	

}
