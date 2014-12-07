package com.romanostrechlis.rssnews.auxiliary;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.content.RssFeed;

/**
 * Manages the appearance of {@link ListView} in ManageActivity.
 */
public class ManageCustomArrayAdapter extends ArrayAdapter<RssFeed> {

	private final Context context;
	private final List<RssFeed> objects;
	
	public ManageCustomArrayAdapter(Context context, int resource, List<RssFeed> objects) {
		super(context, resource, objects);
		this.context = context;
		this.objects = objects;
	}

	/**
	 * Sets the layout of ManageActivity.
	 */
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_manage_listview_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvLabel);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivIcon);
		
		RssFeed feed = objects.get(position);
		textView.setText(feed.getName());
		if (feed.getEnabled()) {
			imageView.setImageResource(android.R.drawable.button_onoff_indicator_on);
		} else {
			imageView.setImageResource(android.R.drawable.button_onoff_indicator_off);
		}

		return rowView;
	}



}
