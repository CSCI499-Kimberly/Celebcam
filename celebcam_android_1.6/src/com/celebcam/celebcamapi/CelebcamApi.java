package com.celebcam.celebcamapi;

import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONObject;

import com.celebcam.CelebCamImageBundle;
import com.celebcam.celebcamapi.HttpRequestTask;

import android.graphics.Bitmap;
import android.util.Log;


/**
 * Used to interface with Celebcam remote Web Api
 * 
 * @author William Locke
 * @todo Implement exceptions
 */
public class CelebcamApi {
	public String baseUrl = "http://pure-summer-1996.herokuapp.com/";
	public Object delegate;
	public String callback;
	
	/**  
	 *  Initiates a remote request for json serialized list of cutouts.
	 *  Json object is returned using a callback function. 
	 *  @param  delegate Object to be called back to. Important: delegate 
	 *  must implement 'getCutoutsLoaded(JSONObject jsonObject)' function
	 */
	public void getCutouts(Object delegate){
		this.getCutouts(delegate, "getCutoutsLoaded");
	}
	public void getCutouts(Object delegate, String callback){
        this.delegate = delegate;
        this.callback = callback;
		String apiPath = "api/cutouts";
        
		String url = baseUrl + apiPath;
		HttpRequestTask requestTask = new HttpRequestTask(this, callback, url);
        requestTask.execute();
	}
	
	public void getCutoutsLoaded(HttpRequestTask httpRequestTask){
		java.lang.reflect.Method method = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(httpRequestTask.response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		try {
			method = this.delegate.getClass().getMethod(this.callback, new Class[] { jsonObject.getClass() });
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		
		try {
			if(method != null){
				method.invoke(this.delegate, jsonObject);
			}else{
				Log.d("CelebcamApi", "Please implement " + this.callback + " in your delegate class");
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	public void uploadImage(Bitmap image, JSONObject imageTag, CelebCamAPIInterface requestOwner){

		(new UploadFileTask(requestOwner)).execute(new CelebCamImageBundle( image, imageTag));

	} 
}
