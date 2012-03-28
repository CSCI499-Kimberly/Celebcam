package com.celebcam;

import java.io.File;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.celebcam.R;


public class PersistOptionsActivity extends Activity implements OnSharedPreferenceChangeListener{

	private static final String TAG = "PersistOptionsActivity";

	private static final String callbackURL    = "oob";
	private static final String consumerKEY    = "8JlWrU08QfMLG3SwVUU4LQ";
	private static final String consumerSECRET = "NrHaVm2My0t2wf2nUaMt2Vno98mPHzg8YOgHBxlt1M";

	
	private Twitter twitter;
	private RequestToken reqTOKEN;

	EditText textField;

	private CelebCamEnum mLaunch;
	
	SharedPreferences prefs;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.persist_options);

        Log.i(TAG, "STARTED - onCreate() ");
      		
            prefs = PreferenceManager.getDefaultSharedPreferences(this); //
            prefs.registerOnSharedPreferenceChangeListener(this);

            mLaunch = CelebCamEnum.NONE;
            
            if( prefs.getString("twitter_pin", "NOT_SET").equals("NOT_SET"))
            	mLaunch = CelebCamEnum.TWITTER_PREF;
            	
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG, "STARTED - onResume()");

		ConfigurationBuilder cb;
		switch( mLaunch )
		{
		case NONE:
			cb = new ConfigurationBuilder();
	  		cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(consumerKEY)
			  .setOAuthConsumerSecret(consumerSECRET)
			  .setOAuthAccessToken(prefs.getString("oauth_access_token", ""))
			  .setOAuthAccessTokenSecret(prefs.getString("oauth_access_secret", ""));
			
			twitter = new TwitterFactory(cb.build()).getInstance();
			break;
		case TWITTER_PREF:
			cb = new ConfigurationBuilder();
			
	  		cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(consumerKEY)
			  .setOAuthConsumerSecret(consumerSECRET);
	  		
	  		twitter = new TwitterFactory(cb.build()).getInstance();
			authenticate_pre_pin();
			break;
		default:
			authenticate_post_pin(prefs.getString("twitter_pin", ""));
			break;
		}
		
		mLaunch = CelebCamEnum.PERSIST_OPTIONS;
  		

	}

    public void postTweet(View button) {
    	
    	textField = (EditText)findViewById(R.id.theTextField);
    	
        String fieldContents = textField.getText().toString();
        
		if ( prefs.getString("twitter_pin","NOT_SET").equals("NOT_SET") ){
			
			Toast.makeText(this, "After you recieved your pin, you will have to enter it in the settings menus.", Toast.LENGTH_LONG).show();
			authenticate_pre_pin();
			mLaunch = CelebCamEnum.SETTINGS_PREF;
		}
		else if( fieldContents.length() <= 0 ){
			Toast.makeText(this, "No text entered.", Toast.LENGTH_SHORT).show();
		}
		else
		{
			sendTweet( fieldContents );
		}

    }

	private void authenticate_pre_pin(){

		try {
			reqTOKEN = twitter.getOAuthRequestToken(callbackURL);

			Intent intent = new Intent(this, TwitterPrefActivity.class);
			
			intent.putExtra("request_token", reqTOKEN.getAuthenticationURL());
			
			startActivityForResult(intent, RESULT_OK);

		   } catch (Exception e) {
			   Log.d(TAG, "Authentication failed.");
		   }
	   }
	
	private void authenticate_post_pin( String oauthVerifier ){

		try {
			
			Log.i(TAG, "trying access token"); 
			Log.d(TAG, "twitter pin" + prefs.getString("twitter_pin", "NOT_SET"));
			
			AccessToken at = twitter.getOAuthAccessToken(reqTOKEN, oauthVerifier);
			
			Log.d(TAG, "AccessToken : " + at.toString());
			
			Editor edit = prefs.edit();
			edit.putString("oauth_access_token", at.getToken());
			edit.putString("oauth_access_secret", at.getTokenSecret());
			edit.commit();
			
			twitter.setOAuthAccessToken(at);
			

		   } catch (Exception e) {
			   Log.d(TAG, "Authentication failed.");
		   }
	   }


	private void sendTweet(String fieldContents){ 

		try {

			Log.e("Login", "Twitter Initialised");
			twitter.updateStatus( fieldContents );

			Toast.makeText(this, "Tweet Successful!", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Post Sent");

		} catch (TwitterException e) {
			Toast.makeText(this, "Tweet error, try again later", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Post NOT Sent");
			Log.e(TAG, "Post NOT Sent", e);


		}

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

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		
	}

}