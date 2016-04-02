package sourceCode;
import java.util.ArrayList;

public class Article
{
	private String title;
	private ArrayList<String> authors;
	private String venue; //journal for article, booktile for inproceedings
	private String key;
	private String year;
	public Article(String title, ArrayList<String> authors, String venue, String key, String year)
	{
		this.title = title;
		this.authors = authors;
		this.venue = venue;
		this.key = key;
		this.year = year;
	}
	public Article(){
		
	}
	
	public String getTitle()
	{
		return title;
	}
	public ArrayList<String> getAuthors()
	{
		return authors;
	}
	public String getVenue()
	{
		return venue;
	}
	public String getKey()
	{
		return key;
	}
	public String getYear()
	{
		return year;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setAuthors(ArrayList<String> authors)
	{
		this.authors = authors;
	}
	public void setVenue(String venue)
	{
		this.venue = venue;
	}
	public void setKey(String key)
	{
		this.key = key;
	}
	public void setYear(String year)
	{
		this.year = year;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Article Details - ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Venue:" + getVenue());
		sb.append(", ");
		sb.append("key:" + getKey());
		sb.append(", ");
		sb.append("Authors:" + getAuthors());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(".");
		return sb.toString();
	}
}