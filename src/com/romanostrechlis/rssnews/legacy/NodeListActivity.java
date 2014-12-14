package com.romanostrechlis.rssnews.legacy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.R.id;
import com.romanostrechlis.rssnews.R.layout;
import com.romanostrechlis.rssnews.R.menu;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.auxiliary.UpdateService;
import com.romanostrechlis.rssnews.managefeeds.ManageActivity;
import com.romanostrechlis.rssnews.managefeeds.NewFeedsActivity;
import com.romanostrechlis.rssnews.settings.SettingsActivity;


/**
 * An activity representing a list of Nodes. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NodeDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NodeListFragment} and the item details (if present) is a
 * {@link NodeDetailFragment}.
 * <p>
 * This activity also implements the required {@link NodeListFragment.Callbacks}
 * interface to listen for item selections.
 * 
 * @deprecated
 * @author.comment This is auto-generated code from eclipse master/detail design pattern
 */
public class NodeListActivity extends Activity implements
NodeListFragment.Callbacks {

	private Thread thread;
	private static final String TAG = "NodeListActivity";
	DatabaseHandler db;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_node_list);
		// Log.d(LOGCAT, "onCreate");
		db = DatabaseHandler.getInstance(this);
		Context context = this;
		// code changed, Flags in helper class are extinct.
		if (true) {//Helper.FLAG_NEW) {
			AssetManager assetManager = getAssets();
			// Helper.readRSS(db, assetManager, context);
			// Helper.FLAG_NEW = false;

			if (findViewById(R.id.node_detail_container) != null) {
				// The detail container view will be present only in the
				// large-screen layouts (res/values-large and
				// res/values-sw600dp). If this view is present, then the
				// activity should be in two-pane mode.
				mTwoPane = true;

				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				((NodeListFragment) getFragmentManager().findFragmentById(
						R.id.node_list)).setActivateOnItemClick(true);
			}
		}
		// Log.d(LOGCAT, "So far so good!!!");

		// FIXME Add functionality on changing the order of appearance in the list.
		while (!Helper.isConnected(this)) {
			Toast.makeText(this, "You don't have Internet connection!", Toast.LENGTH_LONG).show();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				Toast.makeText(this, "Error" + e, Toast.LENGTH_LONG).show();
			}
		}

		// correct implementation with service
		if (UpdateService.isRunning()) {
			Intent startService = new Intent(this, UpdateService.class);
			Log.d(TAG, "Starting Service");
			context.startService(startService);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Method implements a background process that updates the rss feed content.
	 */
	private void bcgUpdate() {
		// Log.d(LOGCAT, String.valueOf(Thread.activeCount()));
		thread = new Thread() {
			@Override
			public void run() {
				super.run();
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				try {
					while(true) {
						Helper.downloadContent(db, getApplicationContext());
						//Log.d(LOGCAT, String.valueOf(Thread.activeCount()));
						Thread.sleep(60000);//Helper.INTERVAL);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * Callback method from {@link NodeListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(NodeDetailFragment.ARG_ITEM_ID, id);
			NodeDetailFragment fragment = new NodeDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
			.replace(R.id.node_detail_container, fragment).commit();


		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, NodeDetailActivity.class);
			detailIntent.putExtra(NodeDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	/**
	 * Checking if there is external storage so it can
	 * decide whether it will show additional Menu items.
	 */
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
		}
		return super.onOptionsItemSelected(item);
	}


}
