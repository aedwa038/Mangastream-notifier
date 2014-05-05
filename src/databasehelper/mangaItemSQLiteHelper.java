package databasehelper;

import com.manga.mangastreamnotifier.MangaItem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class mangaItemSQLiteHelper extends SQLiteOpenHelper {

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "mangamaneger";

	// Contacts table name
	/** The Constant TABLE_CONTACTS. */
	private static final String TABLE_CONTACTS = "mangas";

	/** The Constant KEY_ID. */
	private static final String KEY_ID = "id";

	private static final String KEY_TITLE = "title";

	private static final String KEY_DESCRIPTION = "description";

	private static final String KEY_DATE = "date";

	private static final String KEY_URL = "url";

	public mangaItemSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public boolean addMangaItem(MangaItem item)
	{
		return false;
	}

	public int getCount() {
		return 0;
	}

	public MangaItem getLatestChapter()
	{
		return null;
	}

	public void deleteMangaItem(MangaItem item)
	{

	}

	public MangaItem getMangaItem(int i)
	{
		return null;
	}

}
