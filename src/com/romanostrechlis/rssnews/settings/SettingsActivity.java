package com.romanostrechlis.rssnews.settings;

import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.legacy.NodeListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 * Enables the change of time interval between updates.
 * 
 * <p> Changes are between one minute and an hour.
 * 
 * @author Romanos Trechlis
 *
 */
public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		NumberPicker npInterval = (NumberPicker)findViewById(R.id.npUpdateInterval);
		npInterval.setMaxValue(60);
		npInterval.setMinValue(1);
		npInterval.setValue(Helper.getUpdateInterval(this)/60000);
		npInterval.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				Helper.setUpdateInterval(SettingsActivity.this, newVal * 60000); /** Sets new interval */
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this,
					new Intent(this, NodeListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
