package com.celebcam.celebcamapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;

/** 
 * AsyncTask for retrieving remote resource
 * 
 * @author William Locke
 */ 
class HttpRequestTask extends AsyncTask<String, String, String>{
	public Object delegate;
	public String callback;
	public String response;
	public String method;
	public String baseUrl;
	
	/** 
	 * Constructor for initiating necessary data needed for retrieving remote string.
	 * 
	 * @param delegate The delegate that callback will be called upon 
	 * @param callback The name of the callback method that will be called. Must
	 * be implemented by delegate class 
	 * @param baseUrl The url for the remote resource to be retrieved
	 */ 
    public HttpRequestTask (Object delegate, String callback, String baseUrl)
    {
    	this.delegate = delegate;
    	this.callback = callback;
    	this.baseUrl = baseUrl;
    	
    } 
	
	protected String doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		try {
			String url = new String();
			
			// Option to add get parameters will be added here
			url = this.baseUrl;

			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				responseString = out.toString();
			} else{
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		this.response = result;
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
