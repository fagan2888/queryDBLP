package sourceCode;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;

public class Index
{
	private IndexWriter writer;
	private CharArraySet stopWords;
	int cnt = 0;
	public Index(Directory indexDir) throws IOException
	{
		HashSet<String> set = new HashSet<>();
		stopWords = new CharArraySet(set, true);
		//Analyzer analyzer = new StandardAnalyzer(stopWords);//without stopwords
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
		cfg.setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(indexDir, cfg);
	}
	public IndexWriter getIndexWriter()
	{
		return writer;
	}
	public Document getDocument(Article article) throws Exception
	{
		Document doc = new Document();
		//Field.TermVector tv;
		//tv= Field.TermVector.YES;
		//doc.add(new TextField("title", article.getTitle(), Field.Store.YES)); // token
		doc.add(new Field("title", article.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES)); 
		//doc.add(new TextField("venue", article.getVenue(), Field.Store.YES));
		doc.add(new Field("venue", article.getVenue(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
		//doc.add(new StringField("key", article.getKey(), Field.Store.YES)); // not
		doc.add(new Field("key", article.getKey(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));																	// token
		//doc.add(new StringField("year", article.getYear(), Field.Store.YES));
		doc.add(new Field("year", article.getYear(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
		for (String string : article.getAuthors())
		{
			doc.add(new TextField("author", string, Field.Store.YES)); // token
		}
		
		return doc;
	}

	public void indexFile(Article article) throws Exception
	{
		Document doc = getDocument(article);
		writer.addDocument(doc);
		if(cnt++%10000==0)
			System.out.println("have parsed "+cnt/10000 + "0 K documents");
	}

	public void close() throws IOException
	{
		writer.close();
	}

}
