package com.romanostrechlis.rssnews.auxiliary;

import java.util.ArrayList;
import java.util.List;

import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.content.RssItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Handles all transaction between the application and the SQLite database.
 * 
 * <p>There are methods implemented that aren't used by the application.
 * This class is meant to implement additional functionality for the
 * application.
 * 
 * <h3>Methods Implemented and Used.</h3>
 * <ul>
 * <li>{@link #dropRssItemTable()}: First drops the RssItem table and then re-creates it.</li>
 * <li>{@link #addRssFeed(RssFeed)}: Adds an RssFeed.</li>
 * <li>{@link #addRssItem(RssItem)}: Adds an RssItem.</li>
 * <li>{@link #getAllRssFeed()}: Returns the list of all RssFeed objects stored in database.</li>
 * <li>{@link #getEnabled()}: Returns a list with only the RssFeed objects that have 'true' in enabled column.</li>
 * <li>{@link #updateRssFeed(RssFeed)}: Updates an RssFeed.</li>
 * <li>{@link #getRssFeedCount()}: Returns the number of RssFeed objects stored in database.</li>
 * <li>{@link #getInstance(Context)}: Returns an instance of DatabaseHandler.</li>
 * <li>{@link #getFeedItems(int)}: Returns all RssItem objects with given parent id.</li>
 * <li>{@link #getEnabledByCategory(String)}: Returns a list of RssFeed objects where enabled is true and category is given.</li>
 * <li>{@link #getCategories()}: Rerurns a list of String with distinct categories.</li>
 * </ul>
 * 
 * @author Romanos Trechlis
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	private static DatabaseHandler sInstance;

	public static DatabaseHandler getInstance(Context context) {

		// Use the application context, which will ensure that you 
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DatabaseHandler(context.getApplicationContext());
		}
		return sInstance;
	}

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "RSSNewsDB";

	private String TAG = "DatabaseHandler";

	/** RssFeed */
	private static String RSS_FEED = "RssFeed";
	private static String FEED_ID = "feedId";
	private static String NAME = "name";
	private static String URL = "url";
	private static String ENABLED = "enabled";
	private static String CATEGORY = "category";
	private static String NEW_CONTENT = "newContent";
	private static String CONTENT = "content";
	private static String HASH_CODE = "hashCode";
	/** RssItem */
	private static String RSS_ITEM = "RssItem";
	private static String ITEM_ID = "itemId";
	private static String TITLE = "title";
	private static String DESCRIPTION = "description";
	private static String LINK = "link";
	private static String PARENT = "parentRssFeed";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** 
	 * Creates the DB Schema.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		/** RSS ITEM */
		String CREATE_RSS_ITEM_TABLE = "CREATE TABLE " + RSS_ITEM + "("
				+ ITEM_ID + " INTEGER PRIMARY KEY," 
				+ TITLE + " TEXT,"
				+ DESCRIPTION + " TEXT," 
				+ LINK + " TEXT,"
				+ PARENT + " INTEGER," 
				+ " FOREIGN KEY(" + PARENT + ") REFERENCES " + RSS_FEED + "(" + FEED_ID + ") )";
		db.execSQL(CREATE_RSS_ITEM_TABLE);

		/** RSS Complete Library */
		String CREATE_RSS_FEED_TABLE = "CREATE TABLE " + RSS_FEED + "("
				+ FEED_ID + " INTEGER PRIMARY KEY," 
				+ NAME + " TEXT,"
				+ URL + " TEXT," 
				+ ENABLED + " TEXT,"
				+ CATEGORY + " TEXT," 
				+ NEW_CONTENT + " TEXT,"
				+ CONTENT + " TEXT," 
				+ HASH_CODE + " INTEGER" + ")";
		db.execSQL(CREATE_RSS_FEED_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO No need for it yet
	}

	/**
	 * Drops and re-creates the RssItem database.
	 * This function is used by the application to handle updates.
	 */
	public void dropRssItemTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + RSS_ITEM );

		/** RSS ITEM */
		String CREATE_RSS_ITEM_TABLE = "CREATE TABLE " + RSS_ITEM + "("
				+ ITEM_ID + " INTEGER PRIMARY KEY," 
				+ TITLE + " TEXT,"
				+ DESCRIPTION + " TEXT," 
				+ LINK + " TEXT,"
				+ PARENT + " INTEGER," 
				+ " FOREIGN KEY(" + PARENT + ") REFERENCES " + RSS_FEED + "(" + FEED_ID + ") )";
		db.execSQL(CREATE_RSS_ITEM_TABLE);
	}

	/**
	 * Adds a new RssFeed.
	 * 
	 * @param feed
	 * @return true
	 */
	public Boolean addRssFeed(RssFeed feed) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(FEED_ID, Integer.parseInt(feed.getId()));
		values.put(NAME, feed.getName()); 
		values.put(URL, feed.getUrl()); 
		values.put(ENABLED, String.valueOf(feed.getEnabled())); 
		values.put(CATEGORY, feed.getCategory()); 
		values.put(NEW_CONTENT, String.valueOf(feed.getNewContent())); 
		values.put(CONTENT, feed.getContent()); 
		values.put(HASH_CODE, feed.getHashCode()); 
		// Log.d(LOGCAT, values.toString());
		// Inserting Row
		db.insert(RSS_FEED, null, values);
		return true;
	}

	/**
	 * Adds a new RssItem
	 * 
	 * @param item
	 */
	public void addRssItem(RssItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TITLE, item.getTitle()); 
		values.put(DESCRIPTION, item.getDescription()); 
		values.put(LINK, item.getLink()); 
		values.put(PARENT, item.getParent()); 
		// Log.d(LOGCAT, values.toString());
		// Inserting Row
		db.insert(RSS_ITEM, null, values);
	}

	/**
	 * Searches for a given RssFeed based on its id.
	 * 
	 * @param id
	 * @return RssFeed object with given id.
	 */
	public RssFeed getRssFeed(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(RSS_FEED,
				new String[] { FEED_ID, NAME, URL, ENABLED, CATEGORY, NEW_CONTENT, CONTENT, HASH_CODE },
				FEED_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		RssFeed feed = new RssFeed(cursor.getString(0), 					// FEED_ID: Integer
				cursor.getString(1), 					// NAME
				cursor.getString(2), 					// URL
				Boolean.valueOf(cursor.getString(3)), 	// ENABLED: Boolean
				cursor.getString(4), 					// CATEGORY
				Boolean.valueOf(cursor.getString(5)), 	// NEW_CONTENT: Boolean
				cursor.getString(6), 					// CONTENT
				Integer.parseInt(cursor.getString(7)));	// HASH_CODE: Integer
		// cursor.close();
		return feed;
	}


	public List<String> getCategories() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT distinct " + CATEGORY + " FROM " + RSS_FEED;
		Cursor cursor = db.rawQuery(selectQuery, null);
		List<String> list = new ArrayList<String>();
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		return list;
	}


	/**
	 * Gets all RssFeed objects with enabled column equal to true.
	 * Used by NodeListActivity.
	 * 
	 * @deprecated
	 * @return List 	of RssFeed objects
	 */
	public List<RssFeed> getEnabled() {
		List<RssFeed> rssEnabledList = new ArrayList<RssFeed>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + RSS_FEED + " WHERE " + ENABLED + " = ?";
		SQLiteDatabase db = this.getWritableDatabase();
		String[] selectionArgs = {"true"};
		Cursor cursor = db.rawQuery(selectQuery, selectionArgs);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RssFeed feed = new RssFeed(cursor.getString(0), 					// FEED_ID: Integer
						cursor.getString(1), 					// NAME
						cursor.getString(2), 					// URL
						Boolean.valueOf(cursor.getString(3)), 	// ENABLED: Boolean
						cursor.getString(4), 					// CATEGORY
						Boolean.valueOf(cursor.getString(5)), 	// NEW_CONTENT: Boolean
						cursor.getString(6), 					// CONTENT
						Integer.parseInt(cursor.getString(7)));	// HASH_CODE: Integer
				rssEnabledList.add(feed);
			} while (cursor.moveToNext());
		}
		// cursor.close(); 
		return rssEnabledList;
	}

	public List<RssFeed> getEnabledByCategory(String category) {
		List<RssFeed> list = new ArrayList<RssFeed>();
		String selectQuery = "SELECT * FROM " + RSS_FEED + " WHERE " + CATEGORY + " = ? AND " + ENABLED + " = ?";
		SQLiteDatabase db = this.getWritableDatabase();
		
		String[] selectionArgs = {category, "true"};
		Cursor cursor = db.rawQuery(selectQuery, selectionArgs);
		// Log.d(TAG, "db.getByCategory()");
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				// Log.d(TAG, cursor.toString());
				RssFeed feed = new RssFeed(cursor.getString(0), // FEED_ID: Integer
						cursor.getString(1), 					// NAME
						cursor.getString(2), 					// URL
						Boolean.valueOf(cursor.getString(3)), 	// ENABLED: Boolean
						cursor.getString(4), 					// CATEGORY
						Boolean.valueOf(cursor.getString(5)), 	// NEW_CONTENT: Boolean
						cursor.getString(6), 					// CONTENT
						Integer.parseInt(cursor.getString(7)));	// HASH_CODE: Integer
				list.add(feed);
			} while (cursor.moveToNext());
		}

		return list;
	}

	/**
	 * Gets all RssFeed objects. Used by ManageActivity.
	 * 
	 * @return List 	of all RssFeed objects
	 */
	public List<RssFeed> getAllRssFeed() {
		// Log.d(LOGCAT, "getAllRssFeed()");
		List<RssFeed> rssEnabledList = new ArrayList<RssFeed>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + RSS_FEED;
		// Log.d(LOGCAT, selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RssFeed feed = new RssFeed(cursor.getString(0), 					// FEED_ID: Integer
						cursor.getString(1), 					// NAME
						cursor.getString(2), 					// URL
						Boolean.valueOf(cursor.getString(3)), 	// ENABLED: Boolean
						cursor.getString(4), 					// CATEGORY
						Boolean.valueOf(cursor.getString(5)), 	// NEW_CONTENT: Boolean
						cursor.getString(6), 					// CONTENT
						Integer.parseInt(cursor.getString(7)));	// HASH_CODE: Integer

				rssEnabledList.add(feed);
			} while (cursor.moveToNext());
		}
		// cursor.close(); 
		return rssEnabledList;
	}

	/**
	 * Gets all RssItem objects with a given parent id.
	 * Used by NodeDetailActivity.
	 * 
	 * @return List 	of RssItem objects
	 */
	public List<RssItem> getFeedItems(int feedId) {
		List<RssItem> rssEnabledList = new ArrayList<RssItem>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + RSS_ITEM + " WHERE " + PARENT + " = " + String.valueOf(feedId);
		// Log.d(LOGCAT, selectQuery);

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do { 
				RssItem item = new RssItem(cursor.getString(0), 					// ITEM_ID: Integer
						cursor.getString(1), 					// TITLE
						cursor.getString(2), 					// DESCRIPTION
						cursor.getString(3),						// LINK
						cursor.getString(4));				 	// PARENT

				rssEnabledList.add(item);
			} while (cursor.moveToNext());
		}
		// cursor.close(); 
		return rssEnabledList;
	}

	/**
	 * Updates a single RssFeed object.
	 */
	public int updateRssFeed(RssFeed feed) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NAME, feed.getName()); 
		values.put(URL, feed.getUrl()); 
		values.put(ENABLED, String.valueOf(feed.getEnabled())); 
		values.put(CATEGORY, feed.getCategory()); 
		values.put(NEW_CONTENT, String.valueOf(feed.getNewContent())); 
		values.put(CONTENT, feed.getContent()); 
		values.put(HASH_CODE, feed.getHashCode()); 
		// updating row
		return db.update(RSS_FEED, values, FEED_ID + " = ?",
				new String[] { String.valueOf(feed.getId()) });
	}

	// Deleting single RssFeed
	private void deleteRssFeed(RssFeed feed) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(RSS_FEED, FEED_ID + " = ?",
				new String[] { String.valueOf(feed.getId()) });
	}

	// Deleting single RssItem
	private void deleteRssItem(RssItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(RSS_ITEM, ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getId()) });
	}

	// Getting RssItem Count
	private int getRssItemCount(int itemId) {
		String countQuery = "SELECT * FROM " + RSS_ITEM + " WHERE " + PARENT + " = " + String.valueOf(itemId);
		// Log.d(LOGCAT, countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// cursor.close();
		// return count
		return cursor.getCount();
	}

	/**
	 * Gets the number of rows in RssFeed table.
	 * 
	 * @return int	number of rows
	 */
	public int getRssFeedCount() {
		String countQuery = "SELECT * FROM " + RSS_FEED;
		// Log.d(LOGCAT, countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// cursor.close();
		// return count
		return cursor.getCount();
	}
}
