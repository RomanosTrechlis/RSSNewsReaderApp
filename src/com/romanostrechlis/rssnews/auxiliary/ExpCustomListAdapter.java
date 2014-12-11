package com.romanostrechlis.rssnews.auxiliary;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.content.RssFeed;

/**
 * Custom BaseExpandableListAdapter for the creation of the List in MainActivity.
 * 
 * @author Romanos Trechlis
 *
 */
public class ExpCustomListAdapter extends BaseExpandableListAdapter {

	private List<String> parentItems;
	private Context context;
	private HashMap<String, List<RssFeed>> childMap;

	public ExpCustomListAdapter(List<String> parents, HashMap<String, List<RssFeed>> childMap, Context context) {
		this.parentItems = parents;
		this.childMap = childMap;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return this.parentItems.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.childMap.get(this.parentItems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.parentItems.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.childMap.get(this.parentItems.get(groupPosition))
				.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layout_parents, parent, false);
		}
		CheckedTextView ctv = (CheckedTextView)convertView.findViewById(R.id.ctv1);
		ctv.setText(parentItems.get(groupPosition));
		ctv.setChecked(isExpanded);

		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		//child = (List<String>) childItems.get(groupPosition);
		RssFeed feed = (RssFeed)getChild(groupPosition, childPosition);
		final String childText = feed.getName();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layout_row, null);
		}

		TextView tvExpLabel = (TextView) convertView.findViewById(R.id.tvExpLabel);
		ImageView ivExpIcon = (ImageView) convertView.findViewById(R.id.ivExpIcon);
		tvExpLabel.setText(childText);
		if (feed.getNewContent()) {
			ivExpIcon.setImageResource(android.R.drawable.button_onoff_indicator_on);
		} else {
			ivExpIcon.setImageResource(android.R.drawable.button_onoff_indicator_off);
		}
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
