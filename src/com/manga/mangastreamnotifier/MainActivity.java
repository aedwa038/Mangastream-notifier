package com.manga.mangastreamnotifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import com.manga.mangastreamnotifier.service.RssNotificationService;
import com.manga.util.RssFeedPullParser;
import com.manga.util.RssFeedUrlConnection;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * The Class MainActivity.
 */
public class MainActivity extends ListActivity {

	/** The adapter. */
	MangaListAdapter adapter;

	/** The Constant TAG. */
	private static final String TAG = "MainActivity";

	/** The url. */
	private static String url = "http://mangastream.com/rss";

	/** The m_dialog. */
	ProgressDialog m_dialog;

	/** The alarm. */
	private AlarmManager alarm;
	
	/** The pintent. */
	private PendingIntent pintent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new MangaListAdapter(this);
		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);
		LayoutInflater inflater = this.getLayoutInflater();
		View footer = inflater.inflate(R.layout.footer_view, null);
		TextView footerView = (TextView) footer.findViewById(R.id.footerView);
		getListView().addFooterView(footerView);
		getListView().setAdapter(adapter);
		// getListView().setSelector(findViewById(R.id.l))

		cache = new Vector<MangaItem>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.Reload) {
			adapter.clear();
			cache.clear();
			refresh();
			return true;
		} else if (id == R.id.Filter) {

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refresh.
	 */
	private void refresh()
	{
		m_dialog = new ProgressDialog(this);
		new MyTask().execute(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (adapter.getCount() == 0) {
			loadItems();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (cache.size() > 0) {
			saveToFile();
		}
	}

	/**
	 * Load items.
	 */
	private void loadItems() {
		Log.i(TAG, "loadItems()");
		// TODO this will probably change to a proper datasource in the future
		if (file.exists()) {
			loadChapters();
			adapter.addItemList(cache);
		}
		else
		{
			m_dialog = new ProgressDialog(this);
			new MyTask().execute(url);
		}
	}

	/**
	 * The Class MyTask.
	 */
	private class MyTask extends AsyncTask<String, Void, List<MangaItem>> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// initialize the dialog
			m_dialog.setTitle("Searching...");
			m_dialog.setMessage("Please wait while searching...");
			m_dialog.setIndeterminate(true);
			m_dialog.setCancelable(true);
			m_dialog.show();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected List<MangaItem> doInBackground(String... params) {
			Log.i(TAG, "doInBackground");
			RssFeedPullParser reader = new RssFeedPullParser();
			Log.i(TAG, params[0]);
			Vector<MangaItem> list = new Vector<MangaItem>();
			RssFeedUrlConnection connection = null;
			connection = new RssFeedUrlConnection(params[0]);
			try {
				reader.setInput(connection.getInputStream());
				try {
					list.addAll(reader.getAllItems());

				} catch (XmlPullParserException e) {
					Log.e(TAG, e.toString());
				}
			} catch (IOException e) {
				Log.w(TAG, e.toString());
			}
			connection.closeConnection();
			return list;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(List<MangaItem> result) {
			adapter.addItemList(result);
			cache.addAll(result);
			m_dialog.dismiss();
			
			//Initialize Service Intent
			Intent serviceIntent = new Intent(getApplicationContext(), RssNotificationService.class);
			pintent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, 0);
			startNotifications();
		}

	}

	/**
	 * Start notifications.
	 */
	private void startNotifications() {
		Calendar cal = Calendar.getInstance();

		alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				60 * 1000, pintent);
	}

	/**
	 * Stop notifications.
	 */
	private void stopNotifications() {
		alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}

	/*
	 * Used for testing only. added until a proper data source is ready.
	 */

	/** The cache. */
	private static Vector<MangaItem> cache;

	/** The file. */
	static File file = new File("Items.txt");

	/**
	 * Load chapters.
	 * 
	 * @return the vector
	 */
	public Vector<MangaItem> loadChapters()
	{
		BufferedReader reader = null;
		Vector<MangaItem> entries = new Vector<MangaItem>();

		try {
			FileInputStream fis = openFileInput(file.getName());
			reader = new BufferedReader(new InputStreamReader(fis));

			String line = null;

			while (null != (line = reader.readLine())) {

				String subString = line.substring(1, line.length() - 1);
				String[] stringArray = subString.split("::");

				String[] title = stringArray[0].split("=");
				String[] description = stringArray[1].split("=");
				String[] date = stringArray[2].split("=");
				String[] url = stringArray[3].split("=");

				entries.add(new MangaItem(title[1], description[1], url[1], date[1]));

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return entries;
	}

	/**
	 * Save to file.
	 */
	public void saveToFile()
	{

		PrintWriter writer = null;
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			} else
			{
				file.delete();
			}

			file.createNewFile();
			FileOutputStream outputStream = openFileOutput(file.getName(), MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(outputStream)));
			for (MangaItem mangaItem : cache)
			{
				writer.println(mangaItem.toString());

			}

			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
