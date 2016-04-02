package sourceCode;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

/*
 * To be continued...
 */
public class Search
{
	static int topN = 10;// configure records number to be shown; bug fixed
	static String[] fields = new String[5]; //title,author,venue,year,key
	public static void search(Directory indexDir) throws Exception
	{
		
		
		// query initialize
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher isearcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		TopDocs hits = null;
		
		//input query
		presentFormat();
		System.out.print("please input your query: ");
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();// bug fixed
		
		// parse query first step
		if(!input.contains(":"))
		{
			// query without specified fields
			fields=new String[]{"title","author","key","venue","year"};
		}
		else{
			// query with specified fields
			String[] terms = input.split(":");

			fields = terms[0].split(",");
			input = terms[1];
		}
		
		QueryParser queryParser = new MultiFieldQueryParser(fields,analyzer);
		//search multi-word term 
		if(input.contains(","))
		{
			// phrase query
			
			TopDocs[] sharedHits= new TopDocs[fields.length];
			for(int i=0;i<fields.length;i++)
			{
				
				Query query = queryParser.createPhraseQuery(fields[i], input);
				sharedHits[i]= isearcher.search(query, topN);
			}
			hits = TopDocs.merge(topN,sharedHits);
			
		}
		else{
			Query query = queryParser.parse(input);
			hits = isearcher.search(query, topN);

		}
		int cnt = 1;
		for (ScoreDoc scoreDoc : hits.scoreDocs)
		{
			Document doc = isearcher.doc(scoreDoc.doc);
			System.out.println("Rank "+(cnt++)+" (Score=="+scoreDoc.score+")"+doc.get("key"));
			System.out.println("Title: "+doc.get("title"));
			System.out.println("Authors: "+Arrays.toString(doc.getValues("author")));
			System.out.println("Venue: "+doc.get("venue"));
		}
		
	}
	public static void presentFormat()
	{
		System.out.println("Query Format:");
		System.out.println("1 - query WITHOUT specified fields:");
		System.out.println("\tterm1\n"+"\t\"term1\"\n"+"\tterm1 AND term2\n"+"\tterm1 or term2\n"+"\tterm1 term2\n");
		System.out.println("2 - query WITH specified fields:");
		System.out.println("\tfield1:term1\n"+"\tfield1:\"term1\"\n"+"\tfield1,field2:term1\n"+"\tfield1,field2:term1 AND term2\n"+"\tfield1,field2:term1 or term2\n"+"\tfield1,field2:term1 term2\n");
		
	}
}
