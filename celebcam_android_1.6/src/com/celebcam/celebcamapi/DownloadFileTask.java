package com.celebcam.celebcamapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

/** 
 * AsyncTask for downloading a remote file 
 * 
 * @author William Locke
 * @todo Implement exceptions, handle errors downloading
 */  
class DownloadFileTask extends AsyncTask<String, String, String> {

	public byte[] fileData;
	public Object delegate;
	public String callback;
	public String urlString;
	
	/** 
	 * Constructor for initiating necessary data needed for downloading
	 * a file.
	 * 
	 * @param delegate The object that callback will be called upon 
	 * @param callback The name of the callback method that will be called. Must
	 * be implemented by delegate class 
	 * @param urlString The url for the remote file to be downloaded
	 */ 
    public DownloadFileTask (Object delegate, String callback, String urlString)
    {
    	this.delegate = delegate;
    	this.callback = callback;
    	this.urlString = urlString;
    }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... aurl) {
		int count;

		try {
			URL url = new URL(this.urlString);
			URLConnection conexion = url.openConnection();
			conexion.connect();

			int lenghtOfFile = conexion.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream output = new ByteArrayOutputStream();     
			
			byte data[] = new byte[1024];
			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+(int)((total*100)/lenghtOfFile));
				output.write(data, 0, count);
			}
			
			fileData = output.toByteArray(); 
			
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {}
		return null;

	}
	protected void onProgressUpdate(String... progress) {
		// Progress indicator can be implented here
		// Log.d("CelebcamApi",progress[0]);
	}

	@Override
	protected void onPostExecute(String unused) {
		java.lang.reflect.Method method = null;
		
		try {
			method = delegate.getClass().getMethod(callback, new Class[] { this.getClass() });
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}

		try {
			if(method != null){
				method.invoke(delegate, this);
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}		
	}
}