package databasehelper;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.manga.mangastreamnotifier.MangaItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * The Class mangaItemSQLiteHelper.
 */
public class MangaItemSQLiteHelper extends SQLiteOpenHelper {

	/** The Constant TAG. */
	private static final String TAG = "mangaItemSQLiteHelper";
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "mangamanager";

	// Contacts table name
	/** The Constant TABLE_CONTACTS. */
	private static final String TABLE_MANGA = "manga";

	/** The Constant KEY_ID. */
	private static final String COLUMN_NAME_ID = "id";

	/** The Constant COLUMN_NAME_TITLE. */
	private static final String COLUMN_NAME_TITLE = "title";

	/** The Constant COLUMN_NAME_DESCRIPTION. */
	private static final String COLUMN_NAME_DESCRIPTION = "description";

	/** The Constant COLUMN_NAME_DATE. */
	private static final String COLUMN_NAME_DATE = "date";

	/** The Constant COLUMN_NAME_URL. */
	private static final String COLUMN_NAME_URL = "url";

	/** The Constant COLUMS. */
	private static final String[] COLUMS = { COLUMN_NAME_ID, COLUMN_NAME_TITLE, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_DATE, COLUMN_NAME_URL };

	/**
	 * Instantiates a new manga item sq lite helper.
	 * 
	 * @param context
	 *            the context
	 */
	public MangaItemSQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Instantiates a new manga item sq lite helper.
	 * 
	 * @param context
	 *            the context
	 * @param name
	 *            the name
	 * @param factory
	 *            the factory
	 * @param version
	 *            the version
	 */
	public MangaItemSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_MANGA + "("
				+ COLUMN_NAME_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME_TITLE + " TEXT,"
				+ COLUMN_NAME_DESCRIPTION + " TEXT," + COLUMN_NAME_DATE + " INTEGER," + COLUMN_NAME_URL + " TEXT, UNIQUE(" + COLUMN_NAME_TITLE + "," + COLUMN_NAME_DATE + ") ON CONFLICT REPLACE" + ")";

		Log.i(TAG, CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXITS" + TABLE_MANGA);
		onCreate(db);

	}

	/**
	 * Adds the manga item.
	 * 
	 * @param item
	 *            the item
	 */
	public void addMangaItem(MangaItem item)
	{
		Log.d("addBook", item.toString());
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(COLUMN_NAME_TITLE, item.getTitle());
		value.put(COLUMN_NAME_DESCRIPTION, item.getDescription());
		value.put(COLUMN_NAME_DATE, item.getpubDate().getTime());
		value.put(COLUMN_NAME_URL, item.getUrl());

		db.insertOrThrow(TABLE_MANGA, null, value);

	}

	/**
	 * Adds the manga list.
	 *
	 * @param entries the entries
	 */
	public void addMangaList(List<MangaItem> entries)
	{
		for (MangaItem mangaItem : entries) {
			addMangaItem(mangaItem);
		}
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		String countQuery = "SELECT * FROM " + TABLE_MANGA;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Delete manga item.
	 * 
	 * @param item
	 *            the item
	 */
	public void deleteMangaItem(MangaItem item)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MANGA, COLUMN_NAME_ID + " = ?",
				new String[] { String.valueOf(item.getId()) });

	}

	/**
	 * Gets the manga item.
	 * 
	 * @param id
	 *            the id
	 * @return the manga item
	 */
	public MangaItem getMangaItem(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_MANGA, COLUMS, "id = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		MangaItem item = new MangaItem();
		item.setId(Integer.parseInt(cursor.getString(0)));
		item.setTitle(cursor.getString(0));
		item.setDescription(cursor.getString(1));
		item.setpubDate(new Date(cursor.getLong(2)));
		item.setUrl(cursor.getString(3));

		return item;
	}

	/**
	 * Gets the all manga items.
	 *
	 * @return the all manga items
	 */
	public List<MangaItem> getAllMangaItems()
	{
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_MANGA +" ORDER BY "+ COLUMN_NAME_DATE +" DESC";
		List<MangaItem> items = new Vector<MangaItem>();

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		MangaItem item = null;
		if (cursor.moveToFirst()) {
			do {
				item = new MangaItem();
				item.setId(Integer.parseInt(cursor.getString(0)));
				item.setTitle(cursor.getString(1));
				item.setDescription(cursor.getString(2));
				item.setpubDate(new Date(cursor.getLong(3)));
				item.setUrl(cursor.getString(4));

				// add the item to the list
				items.add(item);
			} while (cursor.moveToNext());

		}

		return items;
	}

	/**
	 * Gets the latest chapter.
	 *
	 * @return the latest chapter
	 */
	public MangaItem getLatestChapter()
	{
		// 1. build the query
		String query = "SELECT * FROM " + TABLE_MANGA + " WHERE " + COLUMN_NAME_DATE + "=( SELECT MAX(" + COLUMN_NAME_DATE + ") FROM " + TABLE_MANGA + ")";

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		MangaItem item = null;
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			item = new MangaItem();
			item.setId(Integer.parseInt(cursor.getString(0)));
			item.setTitle(cursor.getString(1));
			item.setDescription(cursor.getString(2));
			item.setpubDate(new Date(cursor.getLong(3)));
			item.setUrl(cursor.getString(4));
		}
		return item;
	}
	
	
	/**
	 * Removes the oldest manga.
	 */
	public void removeOldestManga ()
	{
		// 1. build the query
				String query = "SELECT * FROM " + TABLE_MANGA + " WHERE " + COLUMN_NAME_DATE + "=( SELECT MIN(" + COLUMN_NAME_DATE + ") FROM " + TABLE_MANGA + ")";

				// 2. get reference to writable DB
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.rawQuery(query, null);
				MangaItem item = null;
				if (cursor != null && cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					item = new MangaItem();
					item.setId(Integer.parseInt(cursor.getString(0)));
					item.setTitle(cursor.getString(1));
					item.setDescription(cursor.getString(2));
					item.setpubDate(new Date(cursor.getLong(3)));
					item.setUrl(cursor.getString(4));
				}
				deleteMangaItem(item);
		
	}

}
