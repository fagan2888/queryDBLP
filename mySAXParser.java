package sourceCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class mySAXParser extends DefaultHandler
{

	String url;
	Index index;
	private String tempVal; //<tag>tempVal</tag>
	private Article tempArticle;
	private ArrayList<String> tmpAuthors; // an individual article may have multiple authors

	public mySAXParser(String url, Index index)
	{
		this.url =  url;
		this.index = index;
	}
	public void run()
	{
		parseDocument();
	}

	private void parseDocument()
	{
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
		{
			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			// parse the file and also register this class for call backs
			sp.parse(url,this);

		} catch (SAXException se)
		{
			se.printStackTrace();
		} catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		} catch (IOException ie)
		{
			ie.printStackTrace();
		}
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings"))
		{
			// create a new instance of article
			tempArticle = new Article();
			tmpAuthors = new ArrayList<String>(); 
			tempArticle.setKey(attributes.getValue("key"));
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		tempVal = new String(ch, start, length);
		
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{

		if (qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings"))
		{
			tempArticle.setAuthors(tmpAuthors);
			try
			{
				index.indexFile(tempArticle);					
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		} else if (qName.equalsIgnoreCase("title"))
		{
			tempArticle.setTitle(tempVal);
		} else if (qName.equalsIgnoreCase("journal")||qName.equalsIgnoreCase("booktitle"))
		{
			tempArticle.setVenue(tempVal);
		} else if (qName.equalsIgnoreCase("year"))
		{
			tempArticle.setYear(tempVal);
		}
		else if (qName.equalsIgnoreCase("author"))
		{
			tmpAuthors.add(tempVal);
		}
	}
}
