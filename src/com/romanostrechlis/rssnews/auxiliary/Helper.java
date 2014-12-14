package com.romanostrechlis.rssnews.auxiliary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.content.RssItem;

/**
 * Provides static methods for the application
 * 
 * <p><h3>Methods Implemented</h3>
 * <ul>
 * <li>{@link #addItemFeed(RssFeed)}: add a new RssFeed to the database.</li>
 * <li>{@link #createDB(DatabaseHandler, AssetManager, Context)}: populates database based on the json elements inside assets folder.</li>
 * <li>{@link #readRSSAll(DatabaseHandler)}: gets a list with all RssFeed objects in database.</li>
 * <li>{@link $makeUpdateList(List): populates the {@link #ITEMS} List for update purposes.</li>
 * <li>{@link #downloadContent(DatabaseHandler)}: downloads the content from the web and parse it into RssItem objects.</li>
 * <li>{@link #isConnected(Activity)}: checks whether application has Internet connection.</li>
 * <li>{@link #getUpdateInterval(Context)}: retrieves the number of milliseconds that the UpdateService sleeps.</li>
 * <li>{@link #setUpdateInterval(Context, int)}: sets the new interval that the UpdateService should sleep.</li>
 * </ul>
 * 
 * @author Romanos Trechlis
 */
public class Helper {

	public static List<RssFeed> ITEMS = new ArrayList<RssFeed>();
	public static Map<String, RssFeed> ITEM_MAP = new HashMap<String, RssFeed>();

	public static List<RssFeed> ITEMS_TOTAL = new ArrayList<RssFeed>();
	public static Map<String, RssFeed> ITEM_MAP_TOTAL = new HashMap<String, RssFeed>();

	/** variable sets the sleep time in ms */
	public static String PREFERENCES = "AppPreferences";
	private static SharedPreferences sharedPreferences;
	private static int DEF_INTERVAL = 60000; // 1min
	
	/**
	 * Retrieves the number of milliseconds that the UpdateService sleeps.
	 * 
	 * @param context
	 * @return number of milliseconds to sleep before the next update
	 */
	public static int getUpdateInterval(Context context) {
		sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		int interval = sharedPreferences.getInt("UpdateInterval", DEF_INTERVAL);
		Log.d(TAG, "New Interval: " + interval);
		return interval;
	}
	
	/**
	 * Sets the new interval that the UpdateService should sleep.
	 * 
	 * @param context
	 * @param newInterval
	 */
	public static void setUpdateInterval(Context context, int newInterval) {
		sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("UpdateInterval", newInterval);
		editor.commit();
		Log.d(TAG, "New Interval: " + getUpdateInterval(context));
	}
	private static final String TAG = "Content";

	/** Auto-generated code. */
	private static void addItem(RssFeed item) {
		Helper.ITEMS.add(item);
		Helper.ITEM_MAP.put(item.getId(), item);
	}

	/**
	 * Gets all RssFeed objects from database and populates ITEMS_TOTAL and ITEM_MAP_TOTAL.
	 * 
	 * <p>Used by ManageActivity.
	 * 
	 * @param db
	 */
	public static void readRSSAll(DatabaseHandler db) {
		Helper.ITEMS_TOTAL.clear();
		Helper.ITEM_MAP_TOTAL.clear();

		// Taking ALL RssFeed from the database and append them to Content.ITEMS_TOTAL
		// Log.d(LOGCAT, "Getting All RssFeed from db!!!");
		List<RssFeed> list = db.getAllRssFeed();
		// Log.d(LOGCAT, String.valueOf(list.size()));
		for (RssFeed mFeed : list) {
			// Log.d(LOGCAT, "mFeed.getEnabled(): " + String.valueOf(mFeed.getEnabled()));
			Helper.ITEMS_TOTAL.add(mFeed);
			Helper.ITEM_MAP_TOTAL.put(mFeed.getId(), mFeed);
		}
		// Log.d(LOGCAT, "ITEMS_TOTAL size: " + String.valueOf(Helper.ITEMS_TOTAL.size()));
	}

	/**
	 * Populates database based on the json elements inside assets folder
	 * 
	 * @param db
	 * @param assetManager
	 * @param context
	 */
	public static void createDB(DatabaseHandler db, AssetManager assetManager, Context context) {
		JSONObject json;
		RssFeed feed;
		BufferedReader br;
		Helper.ITEMS.clear();
		Helper.ITEM_MAP.clear();

		/** This code should only run the first time. */
		InputStream is;
		String line;
		if (db.getAllRssFeed().size() == 0) {
			// Log.d(LOGCAT, "This clause is not executed because db.getAllRssFeed().size() = " + db.getAllRssFeed().size());
			try {
				is = assetManager.open("rssfeeds.txt");
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					json = new JSONObject(line);
					feed = new RssFeed(json.get("id").toString(), 
							json.get("name").toString(), 
							json.get("url").toString(),
							json.get("category").toString());
					feed.setContent("");
					feed.setEnabled(true);
					feed.setNewContent(false);
					feed.setHashCode(0);
					// Log.d(LOGCAT, json.toString());
					db.addRssFeed(feed);
				}
				br.close();
				is.close();
			} catch (IOException | JSONException e) {
				Toast.makeText(context, "Error opening rssfeeds.txt", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Populates the {@link #ITEMS} List for update purposes.
	 * 
	 * @param list
	 */
	public static void makeUpdateList(List<RssFeed> list) {
		// Taking the enabled RssFeed from the database and append them to Content.ITEMS
		for (RssFeed mFeed : list) {
			addItem(mFeed);
		}
		// Log.d(LOGCAT, String.valueOf(Helper.ITEMS.size()));
	}

	/**
	 * Adds an RssItem to database.
	 * 
	 * @param item	rss feed item with the relevant information.
	 * @return true	if successfully adds the rss feed.
	 */
	public static Boolean addItemFeed(DatabaseHandler db, RssFeed mFeed) {
		mFeed.setEnabled(true);
		addItem(mFeed);
		return db.addRssFeed(mFeed);
	} 

	/**
	 * Checks whether application has Internet connection.
	 */
	public static boolean isConnected(Activity a){
		ConnectivityManager connMgr = (ConnectivityManager) a.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) 
			return true;
		else
			return false;    
	}

	/**
	 * Called by {@link #downloadContent(DatabaseHandler)}.
	 * 
	 * @param db
	 */
	private static void feedContent(RssFeed mFeed, DatabaseHandler db, Context context) {
		String data = null;
		// Log.d(LOGCAT, "We 've got so far!!!");
		RetrieveFeedTask rft = new RetrieveFeedTask();
		rft.execute(mFeed.getUrl());
		try {
			data = rft.get();
		} catch (InterruptedException | ExecutionException e) {
			Toast.makeText(context, "Error" + e, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} 
		mFeed.setContent(data);
		db.updateRssFeed(mFeed);
		mFeed.parseXML();
		for (RssItem ri : mFeed.getList()) {
			db.addRssItem(ri);
		}
	}

	/** 
	 * Downloads, parse and inserts content and RssItem objects to database
	 * 
	 * <p> Calls {@link #feedContent(RssFeed, DatabaseHandler)}.
	 * @param db
	 */
	public static void downloadContent(DatabaseHandler db, Context context) {
		db.dropRssItemTable();
		for (RssFeed mFeed : ITEMS) {
			Helper.feedContent(mFeed, db, context);
		}
	}
}
