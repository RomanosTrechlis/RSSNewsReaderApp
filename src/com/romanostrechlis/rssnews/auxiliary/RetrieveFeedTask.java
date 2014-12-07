package com.romanostrechlis.rssnews.auxiliary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

/**
 * RetrieveFeedTask implements the asynchronous functionality between application and servers.
 * 
 * <p>RetrieveFeedTask extends {@link AsyncTask}.
 * 
 * @author Romanos Trechlis 
 */
class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... urls) {
        try {
            URL url= new URL(urls[0]);
            URLConnection urlCon = url.openConnection();
	        BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
	        StringBuilder sbResponse = new StringBuilder();

			String sLine;
			while((sLine = br.readLine()) != null) {
	            sbResponse.append(sLine);
	        }
	        String result = sbResponse.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}