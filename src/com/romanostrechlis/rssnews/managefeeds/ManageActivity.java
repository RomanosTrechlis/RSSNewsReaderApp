package com.romanostrechlis.rssnews.managefeeds;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ListView;

import com.romanostrechlis.rssnews.MainActivity;
import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.auxiliary.ManageCustomArrayAdapter;
import com.romanostrechlis.rssnews.auxiliary.OnSwipeTouchListener;
import com.romanostrechlis.rssnews.content.RssFeed;

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

	private String TAG = "ManageActivity";
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
		lvManage.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		// Log.d(TAG, "readRSSManage executed, now for the loop. #of items in ITEMS_TOTAL: " 
		//		+ String.valueOf(Helper.ITEMS_TOTAL.size()));
		if (!Helper.ITEMS_TOTAL.isEmpty()) {
			for (RssFeed tmpItem : Helper.ITEMS_TOTAL) {
				// Log.d(TAG, "Enabled: " + tmpItem.getEnabled());
				mList.add(tmpItem);
			}
			/** 
			 * We create the array using the complete list in {@link Helper.ITEMS_TOTAL}, 
			 * but we use the new list to {@link Content.commitData()}.
			 */
			final ManageCustomArrayAdapter adapter = new ManageCustomArrayAdapter(this, android.R.layout.simple_list_item_1, Helper.ITEMS_TOTAL);
			lvManage.setAdapter(adapter);

			lvManage.setOnTouchListener(new OnSwipeTouchListener(ManageActivity.this) {

			    public void onSwipeRight() {
			        // Toast.makeText(ManageActivity.this, "right", Toast.LENGTH_SHORT).show();
			    	Intent intent = new Intent(ManageActivity.this, EditRssFeedActivity.class);
			    	int position = lvManage.pointToPosition(getItemX(), getItemY());
			    	intent.putExtra("itemId", adapter.getItem(position).getId());
			    	startActivity(intent);
			    }
			    public void onSwipeLeft() {
			        // Toast.makeText(ManageActivity.this, "left", Toast.LENGTH_SHORT).show();
			        DatabaseHandler db = DatabaseHandler.getInstance(ManageActivity.this);
			        int position = lvManage.pointToPosition(getItemX(), getItemY());
					if (position == ListView.INVALID_POSITION) {
					} else {
						mFeed = (RssFeed) adapter.getItem(position);
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
			NavUtils.navigateUpTo(this,
					new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
