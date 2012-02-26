package com.celebcam;

import java.io.File;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import com.celebcam.R;

public class CelebCamEmailActivity extends Activity{
	
	  private String CALLBACKURL = "app://twitter";
	  private String consumerKey = "CwHVcw1ALd2wPnNEuZoMpA";
	  private String consumerSecret = "kkDFN1nPM9wLmsM8wVH6FJMeHTenLNJID3YfZE7zXw";
	 
	  private OAuthProvider httpOauthprovider = new DefaultOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
	  private CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
	  
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email);
	}

	public void sendEmail (View button) {

		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				getResources().getString(R.string.email_subject));

		emailIntent.setType("image/jpg");
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources()
		//		.getString(R.string.email_message));
        
		Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "DemoPicture.jpg"));

		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(emailIntent, "Send mail"));

	}
	
	public void authent(View button){
		   
		   try {
			   String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
			   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
			   button.getContext().startActivity(intent);
		   } catch (Exception e) {
			   Log.w("oauth fail", e);
			   Toast.makeText(button.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		   }
	   }
	   
	  @Override
	  protected void onNewIntent(Intent intent) {
	      super.onNewIntent(intent);

	      Uri uri = intent.getData();

	      //Check if you got NewIntent event due to Twitter Call back only

	      if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

	          String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

	          try {
	              // this will populate token and token_secret in consumer

	              httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
	              String userKey = httpOauthConsumer.getToken();
	              String userSecret = httpOauthConsumer.getTokenSecret();

	              // Save user_key and user_secret in user preferences and return

	              SharedPreferences settings = getBaseContext().getSharedPreferences("your_app_prefs", 0);
	              SharedPreferences.Editor editor = settings.edit();
	              editor.putString("user_key", userKey);
	              editor.putString("user_secret", userSecret);
	              editor.commit();

	          } catch(Exception e){

	          }
	      } else {
	          // Do something if the callback comes from elsewhere
	      }

	  }
 

	
	
	
	
	
}
