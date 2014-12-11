package com.romanostrechlis.rssnews.managefeeds;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.auxiliary.ManageCustomArrayAdapter;
import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.legacy.NodeListActivity;

/**
 * Shows {@link ListView} with all the RssFeed objects in the database,
 * presenting as well their enabled status with an icon.
 * 
 * <p> By clicking an item in the list the status changes both in ListView 
 * and the database. When we return to the NodeListActivity the changes the
 * enabled = false objects are not present in the list.
 * 
 * @author Romanos Trechlis
 *
 */
public class ManageActivity extends Activity{

	// private String LOGCAT = "ManageActivity";
	private RssFeed mFeed;
	final ArrayList<RssFeed> mList = new ArrayList<RssFeed>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		DatabaseHandler db = DatabaseHandler.getInstance(this);
		Helper.readRSSAll(db);
		final ListView lvManage = (ListView) findViewById(R.id.lvManage);
		
		// Log.d(LOGCAT, "readRSSManage executed, now for the loop. #of items in ITEMS_TOTAL: " 
		//		+ String.valueOf(Helper.ITEMS_TOTAL.size()));
		if (!Helper.ITEMS_TOTAL.isEmpty()) {
			for (RssFeed tmpItem : Helper.ITEMS_TOTAL) {
				// Log.d(LOGCAT, "Enabled: " + tmpItem.getEnabled());
				mList.add(tmpItem);
			}
			/** 
			 * We create the array using the complete list in {@link Helper.ITEMS_TOTAL}, 
			 * but we use the new list to {@link Content.commitData()}.
			 */
			final ManageCustomArrayAdapter adapter = new ManageCustomArrayAdapter(this, android.R.layout.simple_list_item_1, Helper.ITEMS_TOTAL);
			lvManage.setAdapter(adapter);

			lvManage.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					DatabaseHandler db = DatabaseHandler.getInstance(ManageActivity.this);
					if (position == ListView.INVALID_POSITION) {
					} else {
						mFeed = (RssFeed) parent.getItemAtPosition(position);
						// Log.d(LOGCAT, "mItem.getEnabled(): " + String.valueOf(mFeed.getEnabled()));
						if (mFeed.getEnabled()) {
							mFeed.setEnabled(false);
							db.updateRssFeed(mFeed);
						} else {
							mFeed.setEnabled(true);
							db.updateRssFeed(mFeed);
						}	
					}
					lvManage.setAdapter(adapter);
				}
			});
		}
		
		// TODO Change the order of appearance of RssFeeds in NodeListActivity
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Helper.FLAG_NEW = true; // for when there is a permanent change in visibility
			NavUtils.navigateUpTo(this,
					new Intent(this, NodeListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
