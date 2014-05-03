package com.manga.mangastreamnotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The Class MangaListAdapter.
 */
public class MangaListAdapter extends BaseAdapter {

	// List of ToDoItems
	/** The m items. */
	private List<MangaItem> mItems = new ArrayList<MangaItem>();

	/** The m context. */
	private Context mContext = null;

	/** The Constant TAG. */
	private static final String TAG = "MangaListAdapter";

	/**
	 * Instantiates a new manga list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public MangaListAdapter(Context context)
	{
		this.mContext = context;
		mItems = new Vector<MangaItem>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {

		Log.i(TAG, "getCount " + mItems.size());
		return mItems.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {

		Log.i(TAG, " getItem");
		return mItems.get(position);
	}

	/**
	 * Adds the item list.
	 * 
	 * @param items
	 *            the items
	 */
	public void addItemList(List<MangaItem> items)
	{
		mItems.addAll(items);
		notifyDataSetChanged();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {

		Log.i(TAG, " getItemId");
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, " getView");
		final MangaItem mangaItem = (MangaItem) getItem(position);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, null);

		final TextView titleView = (TextView) itemLayout.findViewById(R.id.Manga_title);
		final TextView description = (TextView) itemLayout.findViewById(R.id.Manga_description);
		final TextView date = (TextView) itemLayout.findViewById(R.id.manga_date);

		titleView.setText(mangaItem.getTitle());
		description.setText(mangaItem.getDescription());
		if (mangaItem.getDate() != null)
		{
			date.setText(mangaItem.getDate());
		}

		itemLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(mangaItem.getUrl()));
				mContext.startActivity(i);
			}
		});

		return itemLayout;
	}

	/**
	 * Adds the.
	 * 
	 * @param item
	 *            the item
	 */
	public void add(MangaItem item)
	{
		Log.i(TAG, " add");
		mItems.add(item);
		notifyDataSetChanged();
	}

	/**
	 * Removes the at.
	 * 
	 * @param position
	 *            the position
	 */
	public void removeAt(int position)
	{
		Log.i(TAG, " removeAt");
		mItems.remove(position);
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		Log.i(TAG, " clear");
		mItems.clear();
		notifyDataSetChanged();
	}
	
	public List<MangaItem> getAllChapters()
	{
		return mItems;
	}

}
