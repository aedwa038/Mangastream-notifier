package com.manga.mangastreamnotifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Vector;

import org.xml.sax.SAXException;

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
	private static String url = "http://mangastream.com/rss";
	ProgressDialog m_dialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new MangaListAdapter(getApplicationContext());
		// Put divider between ToDoItems and FooterView
		getListView().setFooterDividersEnabled(true);
		LayoutInflater inflater = this.getLayoutInflater();
		View footer = inflater.inflate(R.layout.footer_view, null);
		TextView footerView = (TextView) footer.findViewById(R.id.footerView);
		getListView().addFooterView(footerView);
		getListView().setAdapter(adapter);
		//loadItems();

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

	private class MyTask extends AsyncTask<String, Void, List<MangaItem>> {
		
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

		@Override
		protected List<MangaItem> doInBackground(String... params) {
			Log.i(TAG, "doInBackground");
			RssFeedReader reader = new RssFeedReader(params[0]);
			Log.i(TAG, params[0]);
			Vector<MangaItem> list = new Vector<MangaItem>();
			HttpURLConnection connection = null;
			try {
				connection = reader.sendGet();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.w(TAG, e1.toString());
			}
			try {
				list.addAll(reader.parse(connection));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				Log.w(TAG, e.toString());
			} catch (IOException e) {
				Log.w(TAG, e.toString());
			}

			return list;
		}

		protected void onPostExecute(List<MangaItem> result) {
			adapter.addItemList(result);
			m_dialog.dismiss();
		}

	}
}
