/*
 * 
 */
package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


/**
 * The Class RssFeedPullParser.
 */
public class RssFeedPullParser {

	/** The input. */
	InputStream input;

	/** The tag. */
	private String TAG = "RssFeedPullParser";

	/** The parser. */
	XmlPullParser parser = null;
	
	/** The factory. */
	private XmlPullParserFactory factory;

	/**
	 * Instantiates a new rss feed pull parser.
	 *
	 * @param input the input
	 */
	public RssFeedPullParser(InputStream input) {
		super();
		this.input = input;
	}

	/**
	 * Instantiates a new rss feed pull parser.
	 *
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public RssFeedPullParser(HttpURLConnection input) throws IOException {
		super();
		this.input = input.getInputStream();
	}

	/**
	 * Gets the input.
	 *
	 * @return the input
	 */
	public InputStream getInput() {
		return input;
	}

	/**
	 * Sets the input.
	 *
	 * @param input the new input
	 */
	public void setInput(InputStream input) {
		this.input = input;
	}

	/**
	 * Sets the input.
	 *
	 * @param input the new input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void setInput(HttpURLConnection input) throws IOException {
		this.input = input.getInputStream();
	}

	/**
	 * Instantiates a new rss feed pull parser.
	 */
	public RssFeedPullParser() {
		super();
	}

	/**
	 * Gets the all chapters.
	 *
	 * @return the all chapters
	 * @throws XmlPullParserException the xml pull parser exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<MangaItem> getAllChapters() throws XmlPullParserException, IOException {

		return parse();
	}

	/**
	 * Inits the.
	 *
	 * @throws XmlPullParserException the xml pull parser exception
	 */
	public void init() throws XmlPullParserException
	{
		factory = XmlPullParserFactory.newInstance();
		parser = factory.newPullParser();
		parser.setInput(input, null);
	}

	/**
	 * Parses the.
	 *
	 * @return the list
	 * @throws XmlPullParserException the xml pull parser exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private List<MangaItem> parse() throws XmlPullParserException, IOException {
		List<MangaItem> entries = new Vector<MangaItem>();
		init();
		int eventType = parser.getEventType();
		boolean done = false;

		MangaItem item = null;

		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			String tagName = parser.getName();
			if (tagName != null)
			{
				Log.i(TAG, tagName);
			}
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (tagName.equals("item")) {
					item = readItem();
					entries.add(item);
				}

				break;
			case XmlPullParser.END_TAG:
				if (tagName.equals("channel")) {
					done = true;
				}
				break;
			}
			eventType = parser.next();
		}

		return entries;
	}
	
	/**
	 * Gets the all items.
	 *
	 * @return the all items
	 * @throws XmlPullParserException the xml pull parser exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<MangaItem> getAllItems() throws XmlPullParserException, IOException {
		return parse();
	}
	
	 
	
	/**
	 * Read next item.
	 *
	 * @return the manga item
	 * @throws XmlPullParserException the xml pull parser exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MangaItem readNextItem() throws XmlPullParserException, IOException {
		if (parser == null)
		{
			init();
		}
		return readItem();
	}

	/**
	 * Read item.
	 *
	 * @return the manga item
	 * @throws XmlPullParserException the xml pull parser exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private MangaItem readItem() throws XmlPullParserException, IOException {
		boolean done = false;
		String title = null;
		String link = null;
		String description = null;
		String pubDate = null;
		int eventType = parser.getEventType();
		MangaItem item = null;
		Log.i(TAG, "readItem");

		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			String tagName = parser.getName();
			if (tagName != null)
			{
				// Log.i(TAG, tagName);
			}
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (tagName != null)
				{
					if (tagName.equals("item")) {

					}
					if (tagName.equals("title")) {
						title = parser.nextText().toString();
						Log.i(TAG, "title:" + title);
					}
					if (tagName.equals("link")) {
						link = parser.nextText().toString();
						Log.i(TAG, "link:" + link);
					}
					if (tagName.equals("description")) {
						description = parser.nextText().toString();
						Log.i(TAG, "description:" + description);
					}

					if (tagName.equals("pubDate")) {
						pubDate = parser.nextText().toString();
						Log.i(TAG, "pubDate:" + pubDate);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (tagName.equals("item")) {
					item = new MangaItem(title, description, link, pubDate);
					done = true;
					Log.i(TAG, "new item");
				}
				break;
			}
			eventType = parser.next();
		}
		return item;
	}
	
	/**
	 * Disconect.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void disconect() throws IOException {
		input.close();
		
	}

}
