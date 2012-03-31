package com.celebcam;

import java.io.File;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class TwitterLauncherActivity extends Activity {



		private static final String TAG = "TwitterLauncherActivity";

		private static final String callbackURL = "app://twitter";
		private static final String consumerKEY = "8JlWrU08QfMLG3SwVUU4LQ";
		private static final String consumerSECRET = "NrHaVm2My0t2wf2nUaMt2Vno98mPHzg8YOgHBxlt1M";
		
		private static final String userAccessTOKEN = "accessToken";
		private static final String userAccessTokenSECRET = "accessTokenSecret";
		private SharedPreferences mPrefs;

		
		private Twitter twitter;
		private RequestToken reqTOKEN;
		
		EditText textField;
		
		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			
			mPrefs = getSharedPreferences("twitterPrefs", MODE_PRIVATE);
			Log.i(TAG, "Got Preferences");
	        
			String link = "http://tinyurl.com/c3lcam";	//get URL from server
			
			String celebrityName = "Rihanna";	//get celebrity from server

			
	        //Create new twitter item using 4jtwitter
	      	twitter = new TwitterFactory().getInstance();
	      	Log.i(TAG, "Got Twitter4j");
	      		
	      	// Tell twitter4j that we want to use it with our app
	      	// Use twitter4j to authenticate
	      	twitter.setOAuthConsumer(consumerKEY, consumerSECRET);
	      	Log.i(TAG, "Inflated Twitter4j");
			
	      	authent();
		}

		
	    public void pressTweet(View button) {
	      //  String fieldContents = textField.getText().toString();
	    	
		//	Toast.makeText(this, textField.getText().toString(), Toast.LENGTH_SHORT).show();
//	    	textField.invalidate();
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
					
					
					Log.i(TAG, "New User tweet sent");
					
			    	textField = (EditText)findViewById(R.id.theTextField);
					

					} catch (Exception e) {
						Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
						
					}
				

			}
			else{
				Log.i(TAG, "Intent from something other that twitter website");
			}
			
			finish();
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
		
		
		
	}
