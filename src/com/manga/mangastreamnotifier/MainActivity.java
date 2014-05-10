package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import com.manga.mangastreamnotifier.service.RssNotificationService;
import com.manga.util.RssFeedPullParser;
import com.manga.util.RssFeedUrlConnection;

import databasehelper.MangaItemSQLiteHelper;

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

	/** The db. */
	MangaItemSQLiteHelper db;

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
		db = new MangaItemSQLiteHelper(getApplicationContext());

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
			loadItems();
			return true;
		} else if (id == R.id.Filter) {

		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		loadItems();
	}

	/**
	 * Load from database.
	 */
	private void loadFromDatabase() {
		adapter.addItemList(db.getAllMangaItems());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Load items.
	 */
	private void loadItems() {
		Log.i(TAG, "loadItems()");
		//first check if there are any items in the database.
		if (db.getCount() == 0) {
			//stops the service
			stopNotifications();
			//downloads the latest chapters from the site
			m_dialog = new ProgressDialog(this);
			new MyTask().execute(url);
		}
		else
		{
			loadFromDatabase();
		}

		
	}

	/**
	 * The Class MyTask. This class will download the latest chapters from the the rssfeed
	 * and places them in the database.
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
			} finally {
				connection.closeConnection();
			}
			return list;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(List<MangaItem> result) {
			m_dialog.dismiss();
			if (result.size() != 0)
			{
				db.addMangaList(result);
				adapter.addItemList(db.getAllMangaItems());
			}
			// Initialize Service Intent
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
		// Start every 30 seconds TODO: need to change this to every 30 minutes
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

}
