package com.celebcam;

import java.io.File;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
<<<<<<< HEAD
import twitter4j.conf.ConfigurationBuilder;
=======
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f

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
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;
<<<<<<< HEAD
=======
import android.widget.TextView;
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
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
	
<<<<<<< HEAD
=======
	private static final String TAG = "PersistOptions";

	private static final String callbackURL = "app://twitter";
	private static final String consumerKEY = "8JlWrU08QfMLG3SwVUU4LQ";
	private static final String consumerSECRET = "NrHaVm2My0t2wf2nUaMt2Vno98mPHzg8YOgHBxlt1M";
	
	private static final String userAccessTOKEN = "accessToken";
	private static final String userAccessTokenSECRET = "accessTokenSecret";
	private SharedPreferences mPrefs;

	
	private Twitter twitter;
	private RequestToken reqTOKEN;
	
	EditText textField;
	
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.persist_options);
<<<<<<< HEAD

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





=======
		
		
		
        Log.i(TAG, "STARTED - onCreate() ");

		mPrefs = getSharedPreferences("twitterPrefs", MODE_PRIVATE);
		Log.i(TAG, "Got Preferences");
        
		String link = "http://tinyurl.com/c3lcam";	//get URL from server
		
		String celebrityName = "Rihanna";	//get celebrity from server
		
    	textField = (EditText)findViewById(R.id.theTextField);
    	textField.setText("Check out this picture I took with " + celebrityName + ". " + link + ". #CelebCam" , TextView.BufferType.NORMAL );

		
        //Create new twitter item using 4jtwitter
      	twitter = new TwitterFactory().getInstance();
      	Log.i(TAG, "Got Twitter4j");
      		
      	// Tell twitter4j that we want to use it with our app
      	// Use twitter4j to authenticate
      	twitter.setOAuthConsumer(consumerKEY, consumerSECRET);
      	Log.i(TAG, "Inflated Twitter4j");
		
	}

	
    public void pressTweet(View button) {
      //  String fieldContents = textField.getText().toString();
    	
	//	Toast.makeText(this, textField.getText().toString(), Toast.LENGTH_SHORT).show();
//    	textField.invalidate();
		if ( textField.getText().toString().equals("") ){
			Toast.makeText(this, "type something", Toast.LENGTH_SHORT).show();
		}
		else{
			if (mPrefs.contains(userAccessTOKEN)) {
				Log.i(TAG, "Repeat User");
				loginAuthorisedUser();
			} else {
				Log.i(TAG, "New User");
				authent();
			}
			/*
			//Toast.makeText(this, fieldContents, Toast.LENGTH_SHORT).show();
			Log.i(TAG, "New User");
			authent();
			Log.i(TAG, "New User authentication");
			*/
		}

    }
	
	private void loginAuthorisedUser() {
		String token = mPrefs.getString(userAccessTOKEN, null);
		String secret = mPrefs.getString(userAccessTokenSECRET, null);

		// Create the twitter access token from the credentials we got previously
		AccessToken at = new AccessToken(token, secret);

		twitter.setOAuthAccessToken(at);
		
		Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show();
		
		sendTweet();
		Log.i(TAG, "Old User tweet sent");
		
	}
	
	private void authent(){
		Log.i(TAG, "STARTED - quthent()");
		try {
			reqTOKEN = twitter.getOAuthRequestToken(callbackURL);
			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reqTOKEN.getAuthenticationURL()));
			// Start new intent on Web browser
			startActivity(intent);
			Log.i(TAG, "Starting Oauth from web"); 
		   } catch (Exception e) {
			   Log.w("oauth fail", e);
			// Toast.makeText(button.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		   }
	   }
	
	
	private void sendTweet(){ 
		//Testing sending tweets after authenticated
	//	Toast.makeText(this, textField.getText().toString(), Toast.LENGTH_SHORT).show();
		
		
		try {
			twitter.updateStatus( textField.getText().toString() );

			Toast.makeText(this, "Tweet Successful!", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Post Sent");

		} catch (TwitterException e) {
			Toast.makeText(this, "Tweet error, try again later", Toast.LENGTH_SHORT).show();
			Log.i(TAG,  textField.getText().toString());
			Log.i(TAG, "Post NOT Sent");
			Log.e(TAG, "Post NOT Sent", e);


		}
		
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "STARTED - onNewIntent");
		Uri uri = intent.getData();
		Log.i(TAG, "Returned to Program"); 

		if (uri !=null && uri.toString().startsWith(callbackURL)){
			String oauthVerifier = uri.getQueryParameter("oauth_verifier");
			Log.i(TAG, "string verifier created"); 
			try {
				Log.i(TAG, "trying access token"); 
				AccessToken at = twitter.getOAuthAccessToken(reqTOKEN, oauthVerifier);
				Log.i(TAG, "trying to set token"); 
				twitter.setOAuthAccessToken(at);
				Log.e("Login", "Twitter Initialised");
				


				saveAccessToken(at);
				Log.i(TAG, "Access token saved");
				
				// Set the content view back after we changed from browser 
				setContentView(R.layout.persist_options);
				
				sendTweet();
				Log.i(TAG, "New User tweet sent");
				
		    	textField = (EditText)findViewById(R.id.theTextField);
				

				} catch (Exception e) {
					Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
					
				}
			

		}
		else{
			Log.i(TAG, "Intent from something other that twitter website");
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "STARTED - onResume()");
	}
	
	
	private void saveAccessToken(AccessToken at) {
		mPrefs =  getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
		String token = at.getToken();
		String secret = at.getTokenSecret();
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(userAccessTOKEN, token);
		editor.putString(userAccessTokenSECRET, secret);
		editor.commit();
	}
	
	
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
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
<<<<<<< HEAD

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		
	}

=======
	
	
	
>>>>>>> 99e154e296220d23ddba8348631d8dfeabc2035f
}