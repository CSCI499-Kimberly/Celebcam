package com.celebcam.celebcamapi;

import org.json.JSONObject;

public interface CelebCamAPIInterface {

	public void onServerResponse(String response);
	public void getCutoutsLoaded(JSONObject jsonObject);
}
