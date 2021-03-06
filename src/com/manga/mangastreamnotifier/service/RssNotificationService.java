package com.manga.mangastreamnotifier.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import com.manga.mangastreamnotifier.MainActivity;
import com.manga.mangastreamnotifier.MangaItem;
import com.manga.mangastreamnotifier.R;
import com.manga.util.RssFeedPullParser;
import com.manga.util.RssFeedUrlConnection;

import databasehelper.MangaItemSQLiteHelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * The Class RssNotificationService.
 */
public class RssNotificationService extends IntentService {

	/** The tag. */
	private static String TAG = "RssNotificationService";

	/** The chapterlimit. */
	private static int CHAPTERLIMIT = 36;

	/** The db. */
	MangaItemSQLiteHelper db;

	/** The reader. */
	RssFeedPullParser reader = null;

	/** The Constant NOTIFICATION. */
	public static final String NOTIFICATION = "com.manga.mangastreamnotifier.service";

	/** The Constant RESULT. */
	public static final String RESULT = "result";

	/** The url. */
	private static String url = "http://mangastream.com/rss";

	/** The pattern. */
	long[] pattern = { 500, 500, 500, 500, 500, 500, 500, 500, 500 };

	/** The Notification id. */
	int NotificationId = 1;

	/**
	 * Instantiates a new rss notification service.
	 */
	public RssNotificationService() {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent");
		Vector<MangaItem> latestResults = getLatestFromFeed();

		if (latestResults.size() != 0)
		{
			db.addMangaList(latestResults);
			updateUser(latestResults);
		}
		else
		{
			Log.i(TAG, "no new chapters");
		}

		if (db.getCount() > CHAPTERLIMIT)
		{
			purgeOldItems();
		}
	}

	/**
	 * Gets the latest from feed.
	 * 
	 * @return the latest from feed
	 */
	private Vector<MangaItem> getLatestFromFeed()
	{
		Vector<MangaItem> latestResults = new Vector<MangaItem>();
		HashSet<String> latestChapter = null;
		db = new MangaItemSQLiteHelper(getApplicationContext());
		if (db.getCount() == 0)
		{
			latestChapter = new HashSet<String>();
		}
		else
		{
			latestChapter = db.getTitles();
		}

		RssFeedUrlConnection feed = new RssFeedUrlConnection(url);

		try {
			reader = new RssFeedPullParser(feed.getInputStream());

			MangaItem latestFromFeed = reader.readLatestChapter();
			while (latestFromFeed != null && !latestChapter.contains(latestFromFeed.getTitle()))
			{
				Log.i(TAG, latestFromFeed.getTitle());
				latestChapter.add(latestFromFeed.getTitle());
				latestResults.add(latestFromFeed);
				latestFromFeed = reader.readLatestChapter();
			}
			reader.disconect();
			feed.closeConnection();

		} catch (IOException e) {
			Log.e(TAG, "Error initializing rss reader" + e.toString());
		} catch (XmlPullParserException e) {

			Log.e(TAG, "Error rss reader");
		}

		return latestResults;
	}

	/**
	 * Purge old items.
	 */
	private void purgeOldItems()
	{
		while (db.getCount() > CHAPTERLIMIT)
		{
			db.removeOldestManga();
		}
	}

	/**
	 * Update user.
	 * 
	 * @param latestResults
	 *            the latest results
	 */
	private void updateUser(Vector<MangaItem> latestResults)
	{
		if (!isForeground(getApplicationContext().getPackageName()))
		{
			if (latestResults.size() == 1)
			{
				showNotification(latestResults.lastElement());
			}
			else
			{
				showNotification(latestResults);
			}
		}
		else
		{
			notifyActivity(Activity.RESULT_OK);
		}

		Log.i(TAG, " activity running: " + isForeground(getApplicationContext().getPackageName()));

	}

	/**
	 * Checks if is foreground.
	 * 
	 * @param PackageName
	 *            the package name
	 * @return true, if is foreground
	 */
	public boolean isForeground(String PackageName) {
		Log.i(TAG, PackageName);
		// Get the Activity Manager
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// Get a list of running tasks, we are only interested in the last one,
		// the top most so we give a 1 as parameter so we only get the topmost.
		List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

		// Get the info we need for comparison.
		ComponentName componentInfo = task.get(0).topActivity;

		// Check if it matches our package name.
		if (componentInfo.getPackageName().equals(PackageName))
			return true;

		// If not then our app is not on the foreground.
		return false;
	}

	/**
	 * Show notification.
	 * 
	 * @param latest
	 *            the latest
	 */
	private void showNotification(MangaItem latest) {
		Log.i(TAG, "showNotification id:");
		NotificationCompat.Builder mBuilder = generateNotificaton(latest.getTitle());
		// Creates an Intent that shows the title and a description of the feed
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setContentText(latest.getDescription());

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NotificationId, mBuilder.build());
	}

	/**
	 * Show a list of of the latest chapters as one notification.
	 * 
	 * @param latest
	 *            the latest
	 */
	private void showNotification(Vector<MangaItem> latest) {
		Log.i(TAG, "showNotification id:");
		NotificationCompat.Builder mBuilder = generateNotificaton("New Chapters Available");

		// Creates an Intent that shows the title and a description of the feed
		Intent resultIntent = new Intent(this, MainActivity.class);

		NotificationCompat.InboxStyle inboxStyle =
				new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(" New Chapters Available From MangaSteam");
		for (MangaItem mangaItem : latest) {
			inboxStyle.addLine(mangaItem.getTitle() + " Avaialable");
		}
		inboxStyle.setSummaryText(latest.size() + " New chapters Avaialble");

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setStyle(inboxStyle);

		mBuilder.setContentText(latest.size() + "New Chapters avaialable");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NotificationId, mBuilder.build());
	}

	/**
	 * Generate notificaton.
	 * 
	 * @param title
	 *            the title
	 * @return the notification compat. builder
	 */
	private NotificationCompat.Builder generateNotificaton(String title)
	{
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title).setContentText(title)
				.setAutoCancel(true);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(alarmSound);
		mBuilder.setLights(Color.BLUE, 500, 500);
		mBuilder.setVibrate(pattern);
		return mBuilder;
	}

	/**
	 * Notify activity.
	 * 
	 * @param result
	 *            the result
	 */
	private void notifyActivity(int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}

}
