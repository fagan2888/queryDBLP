package sourceCode;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.*;
//import org.apache.lucene.search.grouping;

public class AdvanceSearch {
	static String fileAddr = "/Users/x/Desktop/DBLP_Data/";
	static String dataName = fileAddr + "sample.xml";
	static String indexFile = fileAddr + "index";
	static String[] fields = new String[5]; //title,author,venue,year,key
	private static int topic_num=50;
	private static int topic_vec=500;
	private static int emp_vlength=300;//empirical characteristic vector length 
	static String stoplist="123newperformanceabstractapproachapplicationsanalysisusingsystemsmethoddatamodelstimesfrominformationstudybasedoverviathroughin";
	//can be better
	//research topic search
	public static void rt_search(Directory indexDir, int uniqueTermsNum) throws Exception
	{ 
		// query initialize
		DirectoryReader reader = DirectoryReader.open(indexDir);
		IndexSearcher isearcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		TopDocs hits = null;
		
		//input query
		headinfo1();
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		in.close();
		System.out.println("searching for top-10 research topics for year "+input+"...");

			fields=new String[]{"year"};
			QueryParser queryParser = new MultiFieldQueryParser(fields,analyzer);
			Query query = queryParser.parse(input);//query of the year
			hits = isearcher.search(query, uniqueTermsNum);
			
			Terms tms = SlowCompositeReaderWrapper.wrap(reader).terms("title"); 
			TermsEnum iterator = tms.iterator();
	        BytesRef byteRef = null;

	        int counter1=1;
	        int counter2=1;
	        int hit_num=1;
	        long temp;
	        String tempp;
	        String[] temp_tm= new String [uniqueTermsNum];
	        long[] temp_docCount= new long[uniqueTermsNum];
	        while((byteRef = iterator.next()) != null) {
	        	
	        	String tm = byteRef.utf8ToString();
	        	temp_tm[counter1]=tm;
	        	temp_docCount[counter1]=0;
	        	counter1++;
	        }
		//temp term_list built
	        hit_num=counter1;
	        long flag=1;
		for (ScoreDoc scoreDoc : hits.scoreDocs)
			{
			flag++;
			if(flag%1000==0)
			{System.out.println(flag/1000+"k documents processed...");}
			Document doc = isearcher.doc(scoreDoc.doc);
			String tt[]=doc.get("title").split(" ");
            for (counter2=1;counter2<=hit_num;counter2++)
            {
            	
            	for (String str:tt)
            	{  if(str.equals(temp_tm[counter2]))
            		{temp_docCount[counter2]++;
            		}
            	}          	
            }
            }
        // sorting temp_list
		for (hit_num=0;  hit_num < counter1 -1;  hit_num++ )
        {
        	for( counter2=0;  counter2 < counter1 -hit_num -1;  counter2++ )
        	{
               if ( temp_docCount[counter2 ] < temp_docCount[counter2+1] )
               {
                       temp = temp_docCount[ counter2 ];   
                       temp_docCount[ counter2 ] = temp_docCount[ counter2+1 ];
                       temp_docCount[ counter2+1 ] = temp; 
                       tempp=temp_tm[ counter2]; 
                       temp_tm[ counter2 ] = temp_tm[ counter2+1 ];
                       temp_tm[ counter2+1 ] = tempp;
              } 
        	} 
	    }
		for (counter2=1;counter2<=topic_num;counter2++)
			{
				if (!stoplist.contains(temp_tm[counter2]))
				System.out.println( temp_tm[ counter2]+","+Long.toString(temp_docCount[ counter2]));			
			}		
		}	
	
	public static void sim_search(Directory indexDir, int uniqueTermsNum) throws Exception
	{
		// query initialize
		DirectoryReader reader = DirectoryReader.open(indexDir);
		IndexSearcher isearcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		TopDocs hits = null;
		
		//input query
		headinfo2();
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		in.close();
		System.out.println("searching for top-10 similar publication vanue-year for "+input+"...");
		
		String[] entry = input.split(",");
		fields=new String[] {"venue","year"};
		QueryParser queryParser = new MultiFieldQueryParser(fields,analyzer);		
        TopDocs[] sharedHits= new TopDocs[fields.length];
		for(int i=0;i<fields.length;i++)
		{			
			Query query = queryParser.createPhraseQuery(fields[i], entry[i]);
			sharedHits[i]= isearcher.search(query, uniqueTermsNum);
		}
		hits = TopDocs.merge(topic_vec,sharedHits);
		String[] temp_tm=new String [uniqueTermsNum];
		long[] temp_dc= new long[uniqueTermsNum];
		for (int i=1; i<topic_vec;i++)
		{temp_dc[i]=0;}
		int flag=1;
		boolean have;
		//get term_vector for a venue-year pair
		for (ScoreDoc scoreDoc : hits.scoreDocs)
		{	
			Document doc = isearcher.doc(scoreDoc.doc);
			String[] queryset=doc.get("title").split(" ");
			for (int counter=1; counter<queryset.length;counter++)
			{   have=false;
				
				for (int asy=1; asy<flag+1; asy++)
				{
				if (queryset[counter].equals(temp_tm[asy]))
					have=true;
				    temp_dc[asy]++;
				}
				if(!have)
				{
					flag++;
					temp_tm[flag]=queryset[counter];
					temp_dc[flag]=1;
				}
			}			
	        int hit_num=1;
	        long temp;
	        String tempp;
	        int counter=0;
			for (hit_num=0;  hit_num < uniqueTermsNum -1;  hit_num++ )
	        {
	        	for( counter=0;  counter < counter -hit_num -1;  counter++ )
	        	{
	               if ( temp_dc[counter ] < temp_dc[counter+1] )
	               {
	                       temp = temp_dc[ counter ];   
	                       temp_dc[ counter ] = temp_dc[ counter+1 ];
	                       temp_dc[ counter+1 ] = temp; 
	                       tempp=temp_tm[ counter]; 
	                       temp_tm[ counter ] = temp_tm[ counter+1 ];
	                       temp_tm[ counter+1 ] = tempp;
	              } 
	        	} 
		    }			
		}
		System.out.println("venue documents analyzed, searching for similar venue...");
		int f =1;
		String[] tar_tm=new String [uniqueTermsNum];
		long[] tar_dc= new long[uniqueTermsNum];
		for (int j=1; j<topic_vec;j++)
		{tar_dc[j]=0;}
		
		for (int term_num=2; term_num<emp_vlength; term_num++)
		{   if(!stoplist.contains(temp_tm[term_num]))
			hits = isearcher.search(new TermQuery(new Term("title",temp_tm[term_num])), uniqueTermsNum);
			for (ScoreDoc t_scoreDoc : hits.scoreDocs)
			{	Document t_doc = isearcher.doc(t_scoreDoc.doc);
				String alt= t_doc.get("venue") +","+t_doc.get("year") ;				
				have=false;	
					for (int asy=1; asy<f+1; asy++)
					{
					if (alt.equals(tar_tm[asy]))
						have=true;
					    tar_dc[asy]++;
					}
					if(!have)
					{
						f++;
						tar_tm[f]=alt;
						tar_dc[f]=1;
					}
				}
			}
		System.out.println("searching for top hits...");
		int hit_num=1;
        long temp;
        String tempp;
        int counter=0;
			for (hit_num=0;  hit_num < uniqueTermsNum -1;  hit_num++ )
			{
				for( counter=0;  counter < counter -hit_num -1;  counter++ )
				{
					if ( tar_dc[counter ] < tar_dc[counter+1] )
					{
                       temp = tar_dc[ counter ];   
                       tar_dc[ counter ] = tar_dc[ counter+1 ];
                       tar_dc[ counter+1 ] = temp; 
                       tempp=tar_tm[ counter]; 
                       tar_tm[ counter ] = tar_tm[ counter+1 ];
                       tar_tm[ counter+1 ] = tempp;
					} 
				} 
			}		
		for (int i=2; i<12; i++)
			System.out.println(" "+tar_tm[i]);
		//System.out.println("Result "+(i-1)+" "+tar_tm[i]);
		}

	
	private static void headinfo1() {
		System.out.println("Query Format:");
		System.out.println("year e.g. 2010");	
		System.out.print("please input your query: ");
	}
	
	private static void headinfo2() {
		System.out.println("Query Format:");
		System.out.println("venue,year e.g. \"IEEE Trans. Knowl. Data Eng.\", 2012");	
		System.out.print("please input your query: ");
	}

public static void buildIndex(Directory indexDir) throws Exception
{
	// parse and build index should run concurrently, otherwise RAM won't be
	// enough.

	Index index = new Index(indexDir);
	mySAXParser parser = new mySAXParser(dataName, index);
	long timeBeforeIndex = System.currentTimeMillis();
	parser.run();
	index.close();
	System.out.println("\n" + "indexing finished");
	System.out.println();
	long timeAfterIndex = System.currentTimeMillis();
	long runTime = timeAfterIndex - timeBeforeIndex;
	System.out.println("indexing time: " + runTime + "ms");

}

public static void readIndex(Directory indexDir) throws Exception
{
	IndexReader reader = DirectoryReader.open(indexDir);
	Fields fields = MultiFields.getFields(reader);
	Set<BytesRef> uniqueTerms = new HashSet<BytesRef>();
	for (String field : fields)
	{
		System.out.println(field);
		Terms terms = fields.terms(field);
		TermsEnum termsEnum = terms.iterator();
		while (termsEnum.next() != null)
		{
			uniqueTerms.add(termsEnum.term());
		}
	}
	System.out.println(uniqueTerms.size());
}

public static void test_search(Directory indexDir) throws Exception
{   		DirectoryReader reader = DirectoryReader.open(indexDir);
	IndexSearcher isearcher = new IndexSearcher(reader);
	Analyzer analyzer = new StandardAnalyzer();
	TopDocs hits = null;


	//Scanner in = new Scanner(System.in);
	//String input = in.nextLine();
	//in.close();
	Terms tms = SlowCompositeReaderWrapper.wrap(reader).terms("venue"); 
	TermsEnum iterator = tms.iterator();
    BytesRef byteRef = null;
    int m=1;
    while((byteRef = iterator.next()) != null) {  	
    	String tm = byteRef.utf8ToString();
        System.out.println(tm+" : "+m);
        m++;
        }
    
    
    
    
	}

}


