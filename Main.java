package sourceCode;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

public class Main
{
	static String fileAddr = "/Users/x/Desktop/DBLP_Data/";
	static String dataName = fileAddr + "dblp.xml";
	static String indexFile = fileAddr + "index"; // define the file folder
													// storing index.

	public static void main(String[] args)
	{
		try
		{

			Directory indexDir = GetIndexDir.getIndexDir(indexFile);

			/*
			 * build index
			 */
			//buildIndex(indexDir);

			int uniqueTermsNum=readIndex(indexDir);
			//Search.search(indexDir);
			
			//search for research topic certain year
			//AdvanceSearch.rt_search(indexDir,uniqueTermsNum);
			
			//search for similar venue-year
			AdvanceSearch.sim_search(indexDir,uniqueTermsNum);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

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

	public static int readIndex(Directory indexDir) throws Exception
	{
		IndexReader reader = DirectoryReader.open(indexDir);
		Fields fields = MultiFields.getFields(reader);
		Set<BytesRef> uniqueTerms = new HashSet<BytesRef>();
		for (String field : fields)
		{
			//System.out.println(field);
			Terms terms = fields.terms(field);
			TermsEnum termsEnum = terms.iterator();
			while (termsEnum.next() != null)
			{
				uniqueTerms.add(termsEnum.term());
			}
		}
		return(uniqueTerms.size());
	}
}
