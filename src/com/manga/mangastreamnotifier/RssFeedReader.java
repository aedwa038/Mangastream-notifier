package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class RssFeedReader {
	
	/** The user agent. */
	private final String USER_AGENT = "Mozilla/5.0";
	/** The tag. */
	private String TAG = "RssFeedReader";

	/** The feedurl. */
	private String feedurl;
	HttpURLConnection con;
	
	/**
	 * Send get.
	 * 
	 * @param url
	 *            the url
	 * @return the http url connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private HttpURLConnection sendGet() throws IOException {
		Log.i(TAG, "sendGet");
		URL obj = new URL(feedurl);
	    con = (HttpURLConnection) obj.openConnection();
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
	
	public HttpURLConnection getHttpUrlConnection () throws IOException
	{
		return sendGet();
	}
	
	public InputStream getInputStream() throws IOException
	{
		return sendGet().getInputStream();
	}
	
	public void closeConnection ()
	{
		con.disconnect();
	}
	/**
	 * @return the feedurl
	 */
	public String getFeedurl() {
		return feedurl;
	}

	public RssFeedReader() {
		super();
	}

	public RssFeedReader(String feedurl) {
		super();
		this.feedurl = feedurl;
	}

	/**
	 * @param feedurl the feedurl to set
	 */
	public void setFeedurl(String feedurl) {
		this.feedurl = feedurl;
	}

	/**
	 * @return the uSER_AGENT
	 */
	public String getUSER_AGENT() {
		return USER_AGENT;
	}


}
