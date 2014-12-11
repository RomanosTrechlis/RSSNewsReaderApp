package com.romanostrechlis.rssnews.legacy;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.R.id;
import com.romanostrechlis.rssnews.R.layout;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.content.RssFeed;
import com.romanostrechlis.rssnews.content.RssItem;

/**
 * A fragment representing a single Node detail screen. This fragment is either
 * contained in a {@link NodeListActivity} in two-pane mode (on tablets) or a
 * {@link NodeDetailActivity} on handsets.
 * 
 * @author.comment This is auto-generated code from eclipse 
 * master/detail design pattern
 */
public class NodeDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	DatabaseHandler db;

	/**
	 * The dummy content this fragment is presenting.
	 */
	private RssFeed mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public NodeDetailFragment() {
	}
	
	private static final String TAG = "NodeDetailFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = Helper.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_node_detail,container, false);
		Context context = getActivity();
		
		
		mItem.setNewContent(false);
		db = DatabaseHandler.getInstance(context);
		db.updateRssFeed(mItem);
		mItem.setList(db.getFeedItems(Integer.parseInt(mItem.getId())));
		createRSSLayout(context, rootView);
		
		return rootView;
	}
	
	/**
	 * Method generates the necessary view elements so the rss feed can be viewed.
	 * 
	 * @param context	the current activity from {@link #getActivity()}
	 * @param rootView	
	 */
	private void createRSSLayout(Context context, View rootView) {
		final LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.linearFragment);
		if (mItem != null) {
			// getting feed from internet
			// this line gets the feed 
			// Content.feedContent(mItem);
			List<RssItem> col = mItem.getList();
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
}
