package com.romanostrechlis.rssnews;

import java.util.List;

import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.content.RssItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Creates the layout for individual RSS items to appear.
 * 
 * @author Romanos Trechlis
 *
 */
public class DetailActivity extends Activity {

	private RssFeed mFeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_node_detail);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		DatabaseHandler db = DatabaseHandler.getInstance(this);
		mFeed = db.getRssFeed(Integer.parseInt(getIntent().getExtras().get("feedId").toString()));
		
		mFeed.setNewContent(false);
		db.updateRssFeed(mFeed);
		mFeed.setList(db.getFeedItems(Integer.parseInt(mFeed.getId())));
		createRSSLayout(this);
	}

	/**
	 * Method generates the necessary view elements so the rss feed can be viewed.
	 * 
	 * @param context	the current activity from {@link #getActivity()}
	 * @param rootView	
	 */
	private void createRSSLayout(Context context) {
		final LinearLayout linear = (LinearLayout) findViewById(R.id.linearLayout);
		if (mFeed != null) {
			// getting feed from internet
			// this line gets the feed 
			// Content.feedContent(mItem);
			List<RssItem> col = mFeed.getList();
			ScrollView sv = new ScrollView(context);
			LinearLayout linearWrapper = new LinearLayout(context);
			linearWrapper.setOrientation(LinearLayout.VERTICAL);
			linearWrapper.setPadding(10, 0, 0, 10);
			if (!col.isEmpty()) {
				for (int i = 0; i < col.size(); i++) {
					final RssItem rf = col.get(i);
					LinearLayout ll = new LinearLayout(context);
					ll.setOrientation(LinearLayout.VERTICAL);
					ll.setPadding(0, 0, 0, 15);
					// Title
					TextView title = new TextView(context);
					title.setTypeface(null, Typeface.BOLD);

					title.setTextSize(14);
					title.setText(rf.getTitle());
					ll.addView(title);
					// Description
					TextView description = new TextView(context);
					description.setTextSize(12);
					String htmlNoImg = "", htmlNoA = "", htmlNoStrong = "";
					htmlNoImg = rf.getDescription().replaceAll("</?img[^>]*?>", "").replaceAll("<img[^>]*?>.*?</img[^>]*?>", "");
					htmlNoA = htmlNoImg.replaceAll("</?a[^>]*?>", "").replaceAll("<a[^>]*?>.*?</a[^>]*?>", "");
					htmlNoStrong = htmlNoA.replaceAll("</?strong[^>]*?>", "");
					// Check regex: http://www.regexplanet.com/advanced/java/index.html
					// Log.d(TAG, htmlNoA);
					description.setText(Html.fromHtml(htmlNoStrong));
					ll.addView(description);

					// Link
					String url = "<a href='" + rf.getLink() + "'>Read More</a>";
					TextView link = new TextView(context);
					link.setClickable(true);

					link.setMovementMethod(LinkMovementMethod.getInstance());
					link.setText(Html.fromHtml(url));
					// Log.d(TAG, url);
					ll.addView(link);
					linearWrapper.addView(ll);
				}
			} 
			sv.addView(linearWrapper);
			linear.addView(sv);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
