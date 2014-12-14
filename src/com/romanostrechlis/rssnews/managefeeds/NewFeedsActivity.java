package com.romanostrechlis.rssnews.managefeeds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.romanostrechlis.rssnews.MainActivity;
import com.romanostrechlis.rssnews.R;
import com.romanostrechlis.rssnews.auxiliary.DatabaseHandler;
import com.romanostrechlis.rssnews.auxiliary.Helper;
import com.romanostrechlis.rssnews.content.RssFeed;
/**  
 * NewFeeds class implements adding a new RSS feed to SD card.
 * 
 * <p>It takes the name, the url and the category from user and
 * saves it to {@link #getAbsolutePath()}/rssnews/customFeeds.dat
 * 
 * @author Romanos Trechlis
 * @version 0.1v Noe 18, 2014.
 */
public class NewFeedsActivity extends Activity {
	EditText etName;
	EditText etUrl;
	EditText etCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newfeeds);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		etName = (EditText) findViewById(R.id.etName);
		etUrl = (EditText) findViewById(R.id.etUrl);
		etCategory = (EditText) findViewById(R.id.etCategory);



		Button btnAdd = (Button) findViewById(R.id.btnAddFeed);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatabaseHandler db = DatabaseHandler.getInstance(NewFeedsActivity.this);
				RssFeed mFeed = new RssFeed();
				int id = db.getRssFeedCount() + 1;
				mFeed.setId(String.valueOf(id));
				mFeed.setName(etName.getText().toString());
				String url = etUrl.getText().toString();
				if (!url.substring(0, 6).equals("http://") || !url.substring(0, 7).equals("https://"))
					url = "http://" + url;
				mFeed.setUrl(url);
				mFeed.setCategory(etCategory.getText().toString());
				mFeed.setContent("");
				Boolean statusDownload = Helper.checkDownloadFeedContent(mFeed, db, getApplicationContext());

				if (statusDownload) {
					Boolean statusDBCommit = Helper.addItemFeed(db, mFeed);
					if (statusDBCommit) {
						Toast.makeText(NewFeedsActivity.this, "Success", Toast.LENGTH_LONG).show();
						etName.setText("");
						etUrl.setText("");
						etCategory.setText("");
					} else 
						Toast.makeText(NewFeedsActivity.this, "Faillure: RSS did't commit to database!!!", Toast.LENGTH_LONG).show();
					// Helper.FLAG_NEW = true;
				} else
					Toast.makeText(NewFeedsActivity.this, "Faillure: Re-check the RSS URL!!!", Toast.LENGTH_LONG).show();
			}
		});
	}

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
			NavUtils.navigateUpTo(this,
					new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
