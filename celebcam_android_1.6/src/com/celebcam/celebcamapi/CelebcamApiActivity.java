package com.celebcam.celebcamapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.celebcam.celebcamapi.DownloadFileTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CelebcamApiActivity extends Activity {
	/** Called when the activity is first created. */

	// Associate a data + item(s) object with each activity
	public JSONObject data;
	public JSONArray items;
	public CelebcamApi celebcamApi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		celebcamApi = new CelebcamApi();
		this.getCutouts();
	}
	
	public void getCutouts(){
		celebcamApi.getCutouts(this);
		
	}
	
	public void getCutoutsLoaded(JSONObject jsonObject) throws JSONException{
		Log.d("CelebcamApi", "getCutouts Finished");
		this.data = jsonObject;
		this.items = this.data.getJSONArray("items");
		
		this.layoutItems();
	}

	public void layoutItems() throws JSONException{
		Log.d("CelebcamApi", "layoutItems");
		
		Log.d("CelebcamApi", "Item 0: " + this.items.getJSONObject(0));
		
		JSONObject item = items.getJSONObject(0);
		String imageLink = item.getString("image_link");
		Log.d("CelebcamApi", "image_link: " + imageLink);
		
		this.downloadImage(imageLink);
		
	}
	
	public void downloadImage(String imageLink){
		Log.d("CelebcamApi", "downloadImage");
		Log.d("CelebcamApi", "imageLink: " + imageLink);
		
		DownloadFileTask downloadFileTask = new DownloadFileTask(this, "downloadImageLoaded", imageLink);
		downloadFileTask.execute();
		
	}
	
	public void downloadImageLoaded(DownloadFileTask downloadFileTask){
		Log.d("CelebcamApi", "downloadImageLoaded");
		
	
	}


}