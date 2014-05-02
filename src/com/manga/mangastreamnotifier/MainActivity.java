package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Vector;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
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
		//getListView().setSelector(findViewById(R.id.l))

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
		// TODO Auto-generated method stub
		super.onResume();

		if (adapter.getCount() == 0) {
			loadItems();

		}
	}

	/**
	 * Load items.
	 */
	private void loadItems() {
		Log.i(TAG, "loadItems()");
		m_dialog = new ProgressDialog(this);
		new MyTask().execute(url);
	}

	/**
	 * The Class MyTask.
	 */
	private class MyTask extends AsyncTask<String, Void, List<MangaItem>> {
		
		 /* (non-Javadoc)
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

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected List<MangaItem> doInBackground(String... params) {
			Log.i(TAG, "doInBackground");
			RssFeedPullParser reader = new RssFeedPullParser();
			Log.i(TAG, params[0]);
			Vector<MangaItem> list = new Vector<MangaItem>();
			RssFeedReader connection = null;
			connection = new RssFeedReader(params[0]);
			try {
				reader.setInput(connection.getInputStream());
				try {
					list.add(reader.readNextItem());
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				Log.w(TAG, e.toString());
			}
			connection.closeConnection();
			return list;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(List<MangaItem> result) {
			adapter.addItemList(result);
			m_dialog.dismiss();
		}

	}
}
