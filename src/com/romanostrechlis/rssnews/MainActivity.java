package com.romanostrechlis.rssnews;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.ExpCustomListAdapter;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.auxiliary.UpdateService;
import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.managefeeds.ManageActivity;
import com.romanostrechlis.rssnews.managefeeds.NewFeedsActivity;
import com.romanostrechlis.rssnews.settings.SettingsActivity;

/**
 * List of RSS feeds grouped by category using expandable list view.
 * 
 * @author Romanos Trechlis
 *
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private ExpandableListView expListView;
	private DatabaseHandler db;
	private ExpCustomListAdapter adapter = null;
	
	List<String> listParents;
	HashMap<String, List<RssFeed>> childItems = new HashMap<String, List<RssFeed>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		expListView = (ExpandableListView) findViewById(R.id.expListView);
		expListView.setGroupIndicator(null);
		expListView.setClickable(true);

		db = DatabaseHandler.getInstance(this);
		Helper.createDB(db, this.getAssets(), this);
		setGroupParents();
		setChildData();

		while (!Helper.isConnected(this)) {
			Toast.makeText(this, "You don't have Internet connection!", Toast.LENGTH_LONG).show();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				Toast.makeText(this, "Error" + e, Toast.LENGTH_LONG).show();
			}
		}

		// correct implementation with service
		if (!UpdateService.isRunning()) {
			Intent startService = new Intent(this, UpdateService.class);
			// Log.d(TAG, "Starting Service");
			this.startService(startService);
		}

		adapter = new ExpCustomListAdapter(listParents, childItems, this);
		expListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();	
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				RssFeed feed = (RssFeed)adapter.getChild(groupPosition, childPosition);
				// Log.d(TAG, feed.getName());
				Intent intent = new Intent(MainActivity.this, DetailActivity.class);
				intent.putExtra("feedId", feed.getId());
				startActivity(intent);
				return false;
			}
		});
		
		/** When one group is expanded the others are collapsed */
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
		    int previousItem = -1;

		    @Override
		    public void onGroupExpand(int groupPosition) {
		    	Helper.setSavedGroupPosition(groupPosition);
		        if(groupPosition != previousItem )
		        	expListView.collapseGroup(previousItem );
		        previousItem = groupPosition;
		        // Log.d(TAG, "groupPosition: " + groupPosition);
		    }
		    
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Log.d(TAG, "onResume, groupPosition: " + Helper.getSavedGroupPostion());
		expListView.expandGroup(Helper.getSavedGroupPostion());
		setGroupParents();
		setChildData();
		adapter.notifyDataSetChanged();
	}

	private void setGroupParents() {
		listParents = db.getCategories();
		// Log.d(TAG, listParents.toString());
	}

	private void setChildData() {
		List<RssFeed> list;
		for (String s : listParents) {
			try {
				list =  db.getEnabledByCategory(s);
				childItems.put(s, list);
				Helper.makeUpdateList(list);
			} catch (NullPointerException e){

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
		} else if (id == R.id.add_rssFeed) {
			startActivity(new Intent(this, NewFeedsActivity.class));
		} else if (id == R.id.manage_rss) {
			startActivity(new Intent(this, ManageActivity.class));
		} else if (id == R.id.update) {
			Helper.downloadContent(db, getApplicationContext());
			adapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

}
