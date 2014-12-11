package com.romanostrechlis.rssnews.auxiliary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Custom Service to handle the update process.
 * 
 * @author Romanos Trechlis
 *
 */
public class UpdateService extends Service {

	private static final String TAG = "UpdateService";
	private static UpdateService INSTANCE;
	private Thread thread;

	/**
	 * This is necessary because we create a new thread each time onStartCommand is called 
	 * and we need a method to control the creation of only one thread for updates.
	 * 
	 * @return true 	if service is already running
	 */
	public static Boolean isRunning() {
		if (INSTANCE == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		INSTANCE = this;
		update();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void update() {
		// Log.d(TAG, "Service started: " + String.valueOf(Thread.activeCount()));
		final DatabaseHandler db = DatabaseHandler.getInstance(this);
		thread = new Thread() {
			@Override
			public void run() {
				super.run();
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				try {
					while(true) {
						Helper.downloadContent(db, getApplicationContext());
						// Log.d(TAG, String.valueOf(Thread.activeCount()));
						Thread.sleep(Helper.INTERVAL);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

}
