package com.manga.mangastreamnotifier;

import java.util.Calendar;


import com.manga.mangastreamnotifier.service.RssNotificationService;
import databasehelper.MangaItemSQLiteHelper;

import about.AboutDialog;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class MainActivity.
 */
public class MainActivity extends ListActivity {

	/** The adapter. */
	MangaListAdapter adapter;

	/** The Constant TAG. */
	private static final String TAG = "MainActivity";

	/** The m_dialog. */
	ProgressDialog m_dialog;

	/** The alarm. */
	private AlarmManager alarm;

	/** The pintent. */
	private PendingIntent pintent;

	/** The db. */
	MangaItemSQLiteHelper db;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int resultCode = bundle.getInt(RssNotificationService.RESULT);
				if (resultCode == RESULT_OK) {
					Toast.makeText(MainActivity.this, "New Chapters Available", Toast.LENGTH_LONG).show();
					loadFromDatabase();
				}
			}
		}
	};

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
		if (id == R.id.About) {
			AboutDialog about = new AboutDialog(this);
			about.setTitle("about this app");
			about.show();
			return true;

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
		registerReceiver(receiver, new IntentFilter(RssNotificationService.NOTIFICATION));
		if (adapter.getCount() == 0)
		{
			loadItems();
		}

	}

	/**
	 * Load from database.
	 */
	private void loadFromDatabase() {
		adapter.clear();
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
		unregisterReceiver(receiver);
	}

	/**
	 * Load items.
	 */
	private void loadItems() {
		Log.i(TAG, "loadItems()");
		// first check if there are any items in the database.
		if (db.getCount() == 0) {
			Toast.makeText(this, "Loading New Chapters", Toast.LENGTH_SHORT).show();
			// stops the service just in case the service is already resgistered
			stopNotifications();
			Intent serviceIntent = new Intent(getApplicationContext(), RssNotificationService.class);
			pintent = PendingIntent.getService(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			startNotifications();

		}

		else
		{
			loadFromDatabase();
		}
	}

	/**
	 * Start notifications.
	 */
	private void startNotifications() {
		Calendar cal = Calendar.getInstance();

		Log.i(TAG, "Registering new service");
		alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds TODO: need to change this to every 30
		// minutes
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				(120 * 5) * 1000, pintent);
	}

	/**
	 * Stop notifications.
	 */
	private void stopNotifications() {
		alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}

}
