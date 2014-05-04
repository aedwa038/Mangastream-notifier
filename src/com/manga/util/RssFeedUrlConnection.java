package com.manga.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class RssFeedUrlConnection {
	
	/** The user agent. */
	private final String USER_AGENT = "Mozilla/5.0";
	/** The tag. */
	private String TAG = "RssFeedReader";

	/** The feedurl. */
	private String feedurl;
	HttpURLConnection connection;
	
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
	    connection = (HttpURLConnection) obj.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = connection.getResponseCode();

		Log.i(TAG, "Sending 'GET' request to URL : " + feedurl);
		Log.i(TAG, "Response Code : " + responseCode);

		if (responseCode > 200) {
			throw new IOException("Response code  "
					+ Integer.toString(responseCode));
		}

		return connection;

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
		connection.disconnect();
	}
	/**
	 * @return the feedurl
	 */
	public String getFeedurl() {
		return feedurl;
	}

	public RssFeedUrlConnection() {
		super();
	}

	public RssFeedUrlConnection(String feedurl) {
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
