package com.romanostrechlis.rssnews.managefeeds;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.content.RssFeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditRssFeedActivity extends Activity {

	private EditText etName;
	private EditText etUrl;
	private EditText etCategory;
	private RssFeed rf;
	private DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editfeed);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		int id = Integer.parseInt(intent.getExtras().get("itemId").toString());
		
		db = DatabaseHandler.getInstance(this);
		rf = db.getRssFeed(id);
		
		etName = (EditText)findViewById(R.id.etSaveName);
		etName.setText(rf.getName());
		etUrl = (EditText)findViewById(R.id.etSaveUrl);
		etUrl.setText(rf.getUrl());
		etCategory = (EditText)findViewById(R.id.etSaveCategory);
		etCategory.setText(rf.getCategory());
		
		Button btnSave = (Button)findViewById(R.id.btnSaveChanges);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name, url, category;
				name = etName.getText().toString();
				url = etUrl.getText().toString();
				category = etCategory.getText().toString();
				
				rf.setName(name);
				rf.setUrl(url);
				rf.setCategory(category);
				
				db.updateRssFeed(rf);
				
				startActivity(new Intent(EditRssFeedActivity.this, ManageActivity.class));
				finish();
			}
		});
		
		Button btnDelete = (Button)findViewById(R.id.btnDeleteFeed);
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				db.deleteRssFeed(rf);
				startActivity(new Intent(EditRssFeedActivity.this, ManageActivity.class));
				finish();
			}
		});
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// Helper.FLAG_NEW = true; // for when there is a permanent change in visibility
			NavUtils.navigateUpTo(this,
					new Intent(this, ManageActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
