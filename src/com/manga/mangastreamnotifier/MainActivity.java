package com.manga.mangastreamnotifier;

import android.app.ListActivity;
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

	/* (non-Javadoc)
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
		loadItems();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
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

	// from here down is used to test code
	/**
	 * Load items.
	 */
	private void loadItems() {
		Log.i(TAG, "loadItems()");
		
		
		adapter.add(new MangaItem("Dragon Ball Minus Special Omake Story", "Rocket Child of Destiny", "Fri, 25 Apr 2014 "));
		adapter.add(new MangaItem("History's Strongest Disciple Kenichi 567", "Rocket Child of Destiny", "Fri, 25 Apr 2014"));
		adapter.add(new MangaItem("Toriko 275", "Life and Honor", "Fri, 25 Apr 2014"));
		adapter.add(new MangaItem("One Piece 745", "The Bewitching Fog!!", "Fri, 25 Apr 2014 "));
		adapter.add(new MangaItem("Bleach 578", "THE UNDEAD 5", "Fri, 25 Apr 2014 "));
		adapter.add(new MangaItem("Naruto 674", "Sasuke's Rinnegan...!!", "Fri, 25 Apr 2014 "));
		adapter.add(new MangaItem("History's Strongest Disciple Kenichi 566", "Cage of Battle", "Fri, 25 Apr 2014 "));

		

	}

	

}
