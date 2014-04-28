package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * The Class RssFeedReader.
 */
public class RssFeedReader {

	/** The user agent. */
	private final String USER_AGENT = "Mozilla/5.0";

	/** The tag. */
	private String TAG = "RssFeedReader";

	/** The feedurl. */
	private String feedurl;

	/**
	 * Instantiates a new rss feed reader.
	 * 
	 * @param url
	 *            the url
	 */
	public RssFeedReader(String url) {
		Log.i(TAG, "RssFeedReader");
		setFeedUrl(url);
	}

	/**
	 * Sets the feed url.
	 * 
	 * @param url
	 *            the new feed url
	 */
	public void setFeedUrl(String url) {
		feedurl = url;
	}

	/**
	 * Gets the feed url.
	 * 
	 * @return the feed url
	 */
	public String getFeedUrl() {
		return feedurl;
	}

	/**
	 * Parses the.
	 * 
	 * @return the vector
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Vector<MangaItem> parse(HttpURLConnection connection)
			throws SAXException, IOException {
		Log.i(TAG, "parse");

		Vector<MangaItem> toReturn = new Vector<MangaItem>();
		if (connection != null) {

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = null;

			try {
				builder = builderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				Log.w(TAG, e.toString());
			}

			org.w3c.dom.Document document = builder.parse(connection
					.getInputStream());

			Element rootElement = document.getDocumentElement();

			NodeList channel = rootElement.getChildNodes();
			Log.i(TAG, channel.toString());
			NodeList nodes = null;

			for (int i = 0; i < channel.getLength(); i++) {
				Node node = channel.item(i);
				Log.i(TAG, node.getNodeName());
				if (node.getNodeName().equals("channel")) {
					nodes = node.getChildNodes();
				}

			}

			Log.i(TAG, "nodes Length: " + nodes.getLength());

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeName().equals("item")) {
					Log.i(TAG, node.getNodeName());
					toReturn.addAll(travereseNodes(node));

				}

			}
		}

		return toReturn;

	}

	/**
	 * Send get.
	 * 
	 * @param url
	 *            the url
	 * @return the http url connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public HttpURLConnection sendGet() throws IOException {
		Log.i(TAG, "sendGet");
		URL obj = new URL(feedurl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();

		Log.i(TAG, "Sending 'GET' request to URL : " + feedurl);
		Log.i(TAG, "Response Code : " + responseCode);

		if (responseCode > 200) {
			throw new IOException("Response code  "
					+ Integer.toString(responseCode));
		}

		return con;

	}

	/**
	 * Traverese nodes.
	 * 
	 * @param node
	 *            the node
	 * @return the vector
	 */
	private Vector<MangaItem> travereseNodes(Node node) {
		Log.i(TAG, "travereseNodes");
		NodeList nodes = node.getChildNodes();

		Vector<MangaItem> list = new Vector<MangaItem>();
		MangaItem item = new MangaItem();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			Log.i(TAG, n.getNodeName() + ":" + n.getTextContent());
			
			if (n.getNodeName().equals("title")) {

				item.setTitle(n.getTextContent());
			}

			else if (n.getNodeName().equals("description")) {
				item.setDescription(n.getTextContent());
			}

			else if (n.getNodeName().equals("pubDate")) {
				item.setDate(n.getTextContent());
			}
		}
		Log.i(TAG, "List add" + item.toString());
		list.add(item);
		return list;
	}
}
