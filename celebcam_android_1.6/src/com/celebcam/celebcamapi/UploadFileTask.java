package com.celebcam.celebcamapi;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;


import com.celebcam.CelebCamImageBundle;


import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;


public class UploadFileTask extends AsyncTask<CelebCamImageBundle, String, String > {

	private CelebCamAPIInterface mRequestOwner;
	private String mUploadUrl = "ftp://celebcam@rpmmovement.com:csci499spring2012@rpmmovement.com/celebcam.jpg";
 
    public UploadFileTask (CelebCamAPIInterface requestOwner)
    {
    	Log.d("UploadTask", "Constructor");
    	mRequestOwner = requestOwner;
    }


	protected String doInBackground(CelebCamImageBundle...bundle) {
		String link = "NOT IMPLEMENTED";
		
		Log.d("UploadTask", "doInBackground");
		
		try {
			URL url = new URL(mUploadUrl);
			URLConnection urlConnection = url.openConnection();

			urlConnection.setDoOutput(true);
			
			urlConnection.connect();

			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

			bundle[0].getBitmap().compress(CompressFormat.JPEG, 100, out);
			
			out.close();
			
		} catch (Exception e) {}
		
		return link;

	}
	protected void onProgressUpdate(String... progress) {
		super.onProgressUpdate(progress);
	}

	protected void onPostExecute(String link) {
		Log.d("UploadTask", "onPostExecute");
		
		mRequestOwner.onServerResponse( link );	
	}

}