package sourceCode;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class GetIndexDir
{
	public static Directory getIndexDir(String dir) throws Exception
	{	
			Path path = FileSystems.getDefault().getPath(dir);
			Directory indexDir = FSDirectory.open(path);
			return indexDir;	
	}
}
