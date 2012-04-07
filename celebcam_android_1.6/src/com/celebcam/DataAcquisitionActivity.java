package com.celebcam;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import com.celebcam.R;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import android.widget.Button;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.ViewStub;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.widget.LinearLayout;
import android.widget.ImageButton;


import java.io.File;
import java.util.Calendar;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class DataAcquisitionActivity extends Activity implements SurfaceHolder.Callback, TextWatcher,OnSharedPreferenceChangeListener,
 CCMemoryWatcher {

	public int getSizeInBytes() {
		int m = 0;
		
		return (31*4)+ m;
	}


	public int getStaticBytes() {

		return 4;
	}
	
	private String TAG = "DataAcquisitionActivity";
	
    private Camera        mCamera;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView   mSurfaceView;
    
    private static final int PERFORM_COLOR_TRANSFORM = 2;
    private static final int TWITTER_AUTHENTICATION = 3;

	private CelebCamApplication  mApp;
	private CelebCamController   mZoomAndSnapButton;
	private LayerManager         mLayerManager;
	
	private Context  mContext;
	
	private boolean mPreviewRunning;
	
	private String TAKEN_PHOTO   = "tp1";

	private SharedPreferences mPrefs;
	private SQLiteDatabase   mDatabase;
	private CelebCamDbHelper mDbHelper;

	
	/******************************************************
	 * 			TWITTER/EMAIL STATIC VARIABLES - STRT
	 *******************************************************/
	private static final String callbackURL = "app://twitter";
	private static final String consumerKEY = "8JlWrU08QfMLG3SwVUU4LQ";
	private static final String consumerSECRET = "NrHaVm2My0t2wf2nUaMt2Vno98mPHzg8YOgHBxlt1M";
	
	private static final String userAccessTOKEN = "accessToken";
	private static final String userAccessTokenSECRET = "accessTokenSecret";

	private Twitter twitter;
	private RequestToken reqTOKEN;
	
	private byte  mHandleTwitterIntent = CelebCamGlobals.NOT_LAUNCH_TWITTER;
	
    private static final String APP_ID = "418584644823688";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};

	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	
	
	EditText textField;
	/******************************************************
	 * 			TWITTER/EMAIL STATIC VARIABLES - END
	 *******************************************************/
	
	
	/*************************************************
	 * 			FACEBOOK PREFERENCES  STRT
	 *************************************************/
	public boolean saveCredentials(Facebook facebook) {
		Log.i(TAG, "STARTED - saveCredentials()");
    	Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		Log.i(TAG, "STARTED - restoreCredentials()");
    	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}
	/*************************************************
	 * 			FACEBOOK PREFERENCES  ENDS
	 *************************************************/
	
	/** MENU VIEWS */
	private enum MenuScheme {NO_MENU, SHOW_MAIN, SHOW_EFFECTS}; 
	private enum EffectsSubMenu {NONE, TEXT, BORDER, SPARKLES, COLOR, TINT};
	private MenuScheme key = MenuScheme.NO_MENU;
	private EffectsSubMenu keyFX = EffectsSubMenu.NONE;
    private LinearLayout panelMain;
    private LinearLayout panelEffects;
    private LinearLayout panelEffectsSub;
    private LinearLayout effectsButtons;
    private View slidemenu_effects_borders = null;
    private View slidemenu_effects_sparkles = null;
    private View slidemenu_effects_text = null;
    private View slidemenu_effects_tints = null;
    private ImageButton btnMasterHandle;
    private ViewStub slidemenu_effects_borders_stub;
    private ViewStub slidemenu_effects_sparkles_stub;
    private ViewStub slidemenu_effects_text_stub;
    private ViewStub slidemenu_effects_tints_stub;
	private EditText textFromUser;
    /** END MENU VIEWS */

	

	public int getPreivewWidth()
	{
		int width = 10;
		
		return width;
	}
	
	public int getPreviewHeight()
	{
		int height = 10;
		
		return height;
	}
	
	ShutterCallback mShutterCallback = new ShutterCallback(){
		public void onShutter() { Log.d(TAG,"shutter");}
	};
	
	PictureCallback mPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {}
	};
	
	PictureCallback mjpeg = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			
			if( data != null ) {
				
				mApp.storeInCache(data, TAKEN_PHOTO);
				;
				
				data = null;
				
				System.gc();
				
				Bitmap b = mApp.loadFromCache( TAKEN_PHOTO );				
				
				onPhotoAcquired(b);
			}
		}
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        
        setContentView(R.layout.data_acquisition);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


		mPrefs = getSharedPreferences("twitterPrefs", MODE_PRIVATE);

    	textField = (EditText)findViewById(R.id.twitter_text_field);
    	textField.setText("Check out this picture I took with " + ". " + ". #CelebCam" , TextView.BufferType.NORMAL );

      		
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this); 
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        
        twitter.setOAuthConsumer(consumerKEY, consumerSECRET);
        
        CCDebug.registerMemoryWatcher(this);
        
        mContext  = this;
        
        mDbHelper = new CelebCamDbHelper(this);

        // View that camera preview is render on
        mSurfaceView   = (SurfaceView) findViewById(R.id.camera_surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		
        
        mZoomAndSnapButton = (CelebCamController) findViewById(R.id.zoom_snap_button);
        
        mZoomAndSnapButton.setDefaultControlledView( null );
        mZoomAndSnapButton.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View view )
        		{
        			if( mZoomAndSnapButton.isDefault() )
        			{
        				Log.d(TAG, "taking picture...");
    					
    					CelebCamEffectsLibrary.release();
        				mCamera.takePicture(null, null ,mjpeg);
        				
        			}
        			else
        			{
        				mZoomAndSnapButton.unlockCurrentView();
        			}
        		}
        });
        
        
        mApp = (CelebCamApplication) getApplication();
        
  		
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this); //
        mPrefs.registerOnSharedPreferenceChangeListener(this);

     
        
    }
    

	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "STARTED - onNewIntent");
		
		if( mHandleTwitterIntent == CelebCamGlobals.NOT_LAUNCH_TWITTER )
		{
			finish();
			return;
		}
		
		/** NECESSARY - TWITTER **/
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
				setContentView(R.layout.data_acquisition);
				stubsStetp();
				
			        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
			        
			        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			        
				 
				sendTweet();
				Log.i(TAG, "New User tweet sent");
				
				// Set textbox
		    	textField = (EditText)findViewById(R.id.twitter_text_field);
				
				} catch (Exception e) {
					Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
					
				}
			

		}
		else{
			Log.i(TAG, "Intent from something other that twitter website");
		}
		/** NECESSARY - TWITTER **/
		
	}
	
	 public void stubsStetp(){
		   
	        /*******************************
	         * MENU PANELS ASSIGN VIEWS
	         *********************************/
	        
	        //assign menu views
	        panelMain = (LinearLayout)findViewById(R.id.main_slide_menu);
	        panelEffects = (LinearLayout)findViewById(R.id.effects_menu);
			panelEffectsSub = (LinearLayout)findViewById(R.id.effects_submenus);
	        btnMasterHandle=(ImageButton)findViewById(R.id.slide_menu_master_btn);
	        effectsButtons = (LinearLayout)findViewById(R.id.effects_buttons);

	        
	        //assign menu stubs
	        slidemenu_effects_borders_stub = (ViewStub) findViewById(R.id.slidemenu_effects_borders);
	        slidemenu_effects_sparkles_stub = (ViewStub) findViewById(R.id.slidemenu_effects_sparkles);
	        slidemenu_effects_text_stub = (ViewStub) findViewById(R.id.slidemenu_effects_text);
	        slidemenu_effects_tints_stub = (ViewStub) findViewById(R.id.slidemenu_effects_tints);

	        //initialize menu scheme to no menu
	        setMenuScheme(MenuScheme.NO_MENU);
	        
	        /*******************************
	         * MAIN MENU SLIDER BEHAVIOR SETUP
	         *********************************/
	      //master handle button behavior (only visible when no windows open
	        btnMasterHandle.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View arg0) {
	            	switch (key) {
	                case NO_MENU:  setMenuScheme(MenuScheme.SHOW_MAIN);
	                	break;
	                default: setMenuScheme(MenuScheme.NO_MENU);
	                	break;
	            	}
	            }
	        });
	        
	        //main menu opened panel handle button
	        final ImageButton btnMainHandle=(ImageButton)findViewById(R.id.main_menu_handle_btn);
	        btnMainHandle.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View arg0) {
	        	switch (key) {
	            case NO_MENU:  setMenuScheme(MenuScheme.SHOW_MAIN);
	            	break;
	            case SHOW_MAIN: setMenuScheme(MenuScheme.NO_MENU);
	                break;
	            default: setMenuScheme(MenuScheme.NO_MENU);
	            	break;
	        	}
	        }
	        });

	        
	        /**********************
	         * MAIN MENU BUTTONS
	         ***********************/
	        // Effects menu button
	        final Button btnEffectsMenu=(Button)findViewById(R.id.effects_menu_btn);
	        btnEffectsMenu.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View arg0) {
	        	switch (key) {
	            case SHOW_MAIN: setMenuScheme(MenuScheme.SHOW_EFFECTS);
	                break;
	            case SHOW_EFFECTS: setMenuScheme(MenuScheme.SHOW_MAIN);
	                break;
	            default: setMenuScheme(MenuScheme.NO_MENU);
	            	break;
	        	}
	        }
	        });
	        
	        //main menu EDIT button
	        final Button editBtn = (Button) findViewById( R.id.edit_menu_button );
	        editBtn.setOnClickListener( new View.OnClickListener() {

				public void onClick(View v) {
					
				}
			});
	        
	        //main menu SAVE button
	        final Button saveBtn = (Button) findViewById( R.id.save_menu_button );
	        saveBtn.setOnClickListener( new View.OnClickListener() {
				
				public void onClick(View v) {
					
					System.gc();
					
					Bitmap bitmap = ((CelebCamApplication)getApplication()).loadFromCache(TAKEN_PHOTO);
					save(finalProcess( bitmap ));

					bitmap.recycle();
					bitmap = null;
					System.gc();
				}
			});
	        
	       //main menu GALLERY button
	        final Button galleryBtn = (Button) findViewById( R.id.gallery_menu_button );
	        galleryBtn.setOnClickListener( new View.OnClickListener() {
				
				public void onClick(View v) {
					startActivity( new Intent(mContext, PhotoBrowserActivity.class));
				}
			});
	        
	        //main menu EMAIL button
	        final Button emailBtn = (Button) findViewById( R.id.email_menu_button );
	        emailBtn.setOnClickListener( new View.OnClickListener() {
				public void onClick(View v) {
					((CelebCamApplication) getApplication() ).sendEmail();
				}
			});
	        
	        
	        //main menu DEBUG button 
	        final Button debugBtn = (Button) findViewById( R.id.debug_menu_button );
	        debugBtn.setOnClickListener( new View.OnClickListener() {

				public void onClick(View v) {
					CCDebug.toggle();
				}
			});
	        
	        /*************************************************
	         * EFFECTS SUBMENUS SETUP
	         * (submenus created dynamically from stubs)
	         *************************************************/
	        // Effects menu handle: toggles effects menu open/ close
	        final ImageButton btnEffectsHandle=(ImageButton)findViewById(R.id.effects_menu_handle_btn);
	        btnEffectsHandle.setOnClickListener(new View.OnClickListener() {
	        
	        	public void onClick(View arg0) {
	        	switch (key) {
	            case SHOW_MAIN: setMenuScheme(MenuScheme.SHOW_EFFECTS);
	                break;
	            case SHOW_EFFECTS:
	            	//if there is an effects submenu inflated, collapse it
	            	//and make all effects buttons 'unselected' style
	            	if (keyFX != EffectsSubMenu.NONE){
	            		makeChildBtnsUnselected(effectsButtons);
	            		hideAllChildren(panelEffectsSub);
	                    keyFX=EffectsSubMenu.NONE;
	                   	}
	            	else setMenuScheme(MenuScheme.SHOW_MAIN);
	            	
	                break;
	            default: setMenuScheme(MenuScheme.NO_MENU);
	            	break;
	        	}
	        }
	        });
	        
	        /**EFFECT TEXT OPEN SUBMENU BUTTON*/
	        final Button btnEffectsTextMenu=(Button)findViewById(R.id.text_btn);
	        btnEffectsTextMenu.setOnClickListener(clickTextMenuBtn);

	        /**EFFECT BORDER OPEN SUBMENU BUTTON */
	        final Button btnEffectsBorderMenu=(Button)findViewById(R.id.border_btn);
	        btnEffectsBorderMenu.setOnClickListener(clickBorderMenuBtn);
	        
	        /**EFFECT MENU SPARKLE OPEN SUBMENU BUTTON */
	        final Button btnEffectsSparkle=(Button)findViewById(R.id.sparkles_btn);
	        btnEffectsSparkle.setOnClickListener(clickSparkleMenuBtn);    
	        
	        /**EFFECT TINT(Color) OPEN SUBMENU BUTTON */
	        final Button btnEffectsTint=(Button)findViewById(R.id.tints_btn);
	        btnEffectsTint.setOnClickListener(clickTintsMenuBtn); 
	    }
	 

	
	/***********************************************************
	 * 			TWITTER/EMAIL FUNCTIONS STRT
	 ***********************************************************/
    public void pressTweet(View button) {
		Log.i(TAG, "STARTED - pressTweet()");
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
			/*	Intent intent = new Intent( this, TwitterLauncherActivity.class );
				startActivityForResult( intent, RESULT_OK );*/
			}
		}
    }
	

	private void loginAuthorisedUser() {
		Log.i(TAG, "STARTED - loginAuthorisedUser()" );
		String token = mPrefs.getString(userAccessTOKEN, null);
		String secret = mPrefs.getString(userAccessTokenSECRET, null);

		// Create the twitter access token from the credentials we got previously
		AccessToken at = new AccessToken(token, secret);

		twitter.setOAuthAccessToken(at);
		Log.i(TAG, "Old User credentials set");
		
		sendTweet();
		Log.i(TAG, "Old User tweet sent");
	}
	
	private void authent(){
		Log.i(TAG, "STARTED - authent()");
		try {
			
			mHandleTwitterIntent = CelebCamGlobals.LAUNCH_TWITTER;
			
			reqTOKEN = twitter.getOAuthRequestToken(callbackURL);
			
			Log.i(TAG, "Starting Oauth from WebView"); 
			WebView twitterSite = new WebView(this);
			twitterSite.loadUrl(reqTOKEN.getAuthenticationURL());
			setContentView(twitterSite);

			/*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reqTOKEN.getAuthenticationURL()));
			Start new intent on Web browser
			startActivity(intent);*/
			} 
		catch (Exception e) {
			   Log.w("oauth fail", e);
		   }
	   }
	
	
	private void sendTweet(){ 
		Log.i(TAG, "sendTweet()" );
		try {
			twitter.updateStatus( textField.getText().toString() );

			Toast.makeText(this, "Tweet Successful!", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "Post Sent");
		} 
		catch (TwitterException e) {
			Toast.makeText(this, "Tweet error, try again later", Toast.LENGTH_SHORT).show();
			Log.i(TAG,  textField.getText().toString());
			Log.e(TAG, "Post NOT Sent", e);
		}	
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
	
	
	public void sendEmail(View button) {

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
	
	/***********************************************************
	 * 			TWITTER/EMAIL FUNCTIONS END
	 ***********************************************************/

	/***********************************************************
	 * 				FACEBOOK FUNCTIONS STRT
	 ***********************************************************/

	public void shareFacebook(View button){
		Log.i(TAG, "STARTED - shareFacebook()");

    	//textField = (EditText)findViewById(R.id.theTextField);
        String facebookMessage = textField.getText().toString();

		if (facebookMessage == null){
			Log.i(TAG, "FB message is null");
			facebookMessage = "More text...";
		}
		else{
		messageToPost = facebookMessage;}
		Log.i(TAG, "FB message is "+ messageToPost);
		
		if (! facebook.isSessionValid()) {
			Log.i(TAG, "FB session is NOT valid");
			loginAndPostToWall();
		}
		else {
			Log.i(TAG, "FB session is valid. Message to post " + messageToPost);
			postToWall(messageToPost);
		}
	}

	public void loginAndPostToWall(){
		Log.i(TAG, "STARTED - saveCredentials()");
		 facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	public void postToWall(String message){
		Log.i(TAG, "STARTED - postToWall()");
		Bundle parameters = new Bundle();
		parameters.putString("message", message);
		parameters.putString("description", "topic share");
		try {
			facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
			Log.d("Tests", "got response: " + response);
			if (response == null || response.equals("") ||
			        response.equals("false")) {
				showToast("Blank response.");
			}
			else {
				showToast("Message posted to your facebook wall!");
			}
		} catch (Exception e) {
			showToast("Failed to post to wall!");
			e.printStackTrace();
		}
	}

	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	if (messageToPost != null){
			postToWall(messageToPost);
		}
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed!");
	        finish();
	    }
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed!");
	        finish();
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled!");
	        finish();
	    }
	}

	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	/***********************************************************
	 * 				FACEBOOK FUNCTIONS ENDS
	 ***********************************************************/
	
	/**
	 *  ******************************** Leonard Collins ************************************
	 */
	
	
    public void surfaceCreated(SurfaceHolder holder) {

    	Log.d(TAG, "surfaceCreated called");
    	
        mCamera = Camera.open();
   
        if( mCamera == null )
        	return;

        Size mPictureSize = mCamera.getParameters().getPictureSize();
        
        int height = 0;
        int width  = 0;
        
        if( mLayerManager.getWidth() < mLayerManager.getHeight())
        {
        	 width = mLayerManager.getWidth();
        	 height = (mPictureSize.height*mLayerManager.getWidth())/mPictureSize.width;
        }
        else if( mLayerManager.getWidth() > mLayerManager.getHeight())
        {
        	Log.d(TAG, "width greater than height");
       	 	height = mLayerManager.getHeight();
       	    width = ((mPictureSize.width*height)/mPictureSize.height);
        }

        CelebCamEffectsLibrary.setPublishSize( mPictureSize.width, mPictureSize.height );
        CelebCamEffectsLibrary.setPreviewSize( width, height );
        
        //mSurfaceView.layout(0, 0, width, height);
        
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	
    	Log.d(TAG, "surfaceDestroyed called");
        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	Log.d(TAG, "surfaceChanged called");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mSurfaceHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
        	if(mPreviewRunning) {
            mCamera.stopPreview();
            mPreviewRunning = false;
        	}

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(w, h);
        p.setPictureSize(w, h);
        mCamera.setParameters(p);
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
            
        mPreviewRunning = true;
        } catch (Exception e){
        
        Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
 
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}


	void onPhotoAcquired(Bitmap bitmap)
	{

		try{

			CelebCamEffectsLibrary.setPreviewBitmap(bitmap);
					
			CelebCamEffectsLibrary.setState2( CelebCamEffectsLibrary.PREVIEW );	

		
		}
		catch(Exception e)
		{

			Toast.makeText(this, "Error: Cannot start editor.", 10).show();
			
			e.printStackTrace();
		}
		
	}
	Bitmap finalProcess(Bitmap bitmap) {

		if(bitmap == null )
		{
			Toast.makeText( mContext, "No photo to save", 10).show();
			return null;
		}
		
		CelebCamEffectsLibrary.setPublishBitmap( bitmap );
		
		CelebCamEffectsLibrary.setState( CelebCamEffectsLibrary.PUBLISH );
		
				
		mCamera.startPreview();
		
		return CelebCamEffectsLibrary.getCurrentBitmap();
		
	}
	
	public void afterTextChanged( Editable text )
	{
		//mText.setText( text.toString());
	}
		
	public void beforeTextChanged(CharSequence sequence, int start, int count, int after )
	{
		
	}
	
	public void onTextChanged(CharSequence sequence, int start, int before, int after )
	{
		
	}
	
	public void save(Bitmap bitmap)
	{
		Calendar calendar = Calendar.getInstance();
		
		String url = Long.toString(calendar.getTimeInMillis());
		
		String createdAt = 
			Integer.toString(calendar.get(Calendar.DATE))  + "-" +
			Integer.toString(calendar.get(Calendar.MONTH)) + "-" +
			Integer.toString(calendar.get(Calendar.YEAR));
		
		//((CelebCamApplication) getApplication() ).writeImageToDisk( url, CelebCamEffectsLibrary.applyColorNotes(CelebCamEffectsLibrary.getCurrentBitmap()) );
		((CelebCamApplication) getApplication() ).writeImageToDisk( url, bitmap );	
		((CelebCamApplication) getApplication() ).setMostRecentURL( url );
		
		if( url != null ) {
			
			Bundle bundle = new Bundle();

			mDatabase = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues(); 


			values.put(CelebCamDbHelper.C_CREATED_AT, createdAt );
			values.put(CelebCamDbHelper.C_SOURCE, ((CelebCamApplication) getApplication() ).mostRecentFile());
			values.put(CelebCamDbHelper.C_TEXT, "CelebCam Photo");
			values.put(CelebCamDbHelper.C_USER, "CelebCam");

			try {
				mDatabase.insertOrThrow(CelebCamDbHelper.TABLE, null, values); 

			} catch (SQLException e) { 
			// Ignore exception
				Log.d(TAG, e.getMessage());
			}
			finally
			{
				mDatabase.close();
			}
				
			bundle.putString("url", url);
			
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			
		}
	}
	

	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		
	}

	public void startPreferenceActivity( View v )
	{
		startActivity( new Intent( this, SettingsPrefActivity.class));
	}
	

 

	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");
	}


	
     
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		
		Log.d(TAG, "onActivityResult");
		if (requestCode == PERFORM_COLOR_TRANSFORM) {
		
		if( resultCode == RESULT_OK )
		{
			Log.d(TAG, "RESULT_OK");
			//mEditView.setBitmap( ((CelebCamApplication) getApplication()).getCurrentBitmap());
		}
		
		}
		else if (requestCode == TWITTER_AUTHENTICATION) {
			
			if( resultCode == RESULT_OK )
			{
				Log.d(TAG, "RESULT_OK");
				//mEditView.setBitmap( ((CelebCamApplication) getApplication()).getCurrentBitmap());
			}
			
		}
	
	}

	
/** 
 * ******************** GRACE CAREY  ******************************************************************
 */
	
	/** Hide all children of a view */

    private void hideAllChildren( ViewGroup view ){
    	view.getChildCount(); 
    	for (int i=0;i<view.getChildCount();i++){
    		view.getChildAt(i).setVisibility(View.GONE);
    	}
    }
    
    /** Make button 'selected' style and all button siblings 'unselected' style */
    private void makeBtnSelected(View view){
    	ViewGroup parent = (ViewGroup)view.getParent();
    	makeChildBtnsUnselected(parent);
    	Button b=(Button)view;
    	b.setBackgroundResource(R.drawable.slidemenu_btn_selected_bg);
    }
    
    /** Make all child buttons of this view unselected style */
    private void makeChildBtnsUnselected(ViewGroup view){
    	for (int i=0; i<view.getChildCount();i++){
    		if (view.getChildAt(i) instanceof Button){
    			view.getChildAt(i).setBackgroundResource(R.drawable.slidemenu_btn_bg);
    		}
    	}	
    }
    /** Make a effect button style 'toggle on' and sibling buttons 'toggle off' **/
    private void toggleEffectBtnOn(View view){
        ViewGroup parent = (ViewGroup)view.getParent();
        for (int i=0;i<parent.getChildCount();i++){
        	Button btn =  (Button)parent.getChildAt(i);
        	btn.setBackgroundResource(R.drawable.slidemenu_btn_bg);
        }
        view.setBackgroundResource(R.drawable.slidemenu_btn_toggleon_bg);  
    }  
    
    /********************************
     * MENU PANELS HELPER FUNCTIONS
     ********************************/
	private void setMenuScheme( MenuScheme _key ){
        
    	switch (_key) {
        case NO_MENU:  
        	btnMasterHandle.setVisibility(View.VISIBLE);
        	panelMain.setVisibility(View.GONE);
	    	panelEffects.setVisibility(View.GONE);
        	key= MenuScheme.NO_MENU;
        	keyFX=EffectsSubMenu.NONE;  
        	break;
        case SHOW_MAIN:
        	btnMasterHandle.setVisibility(View.GONE);
        	panelMain.setVisibility(View.VISIBLE);
	    	panelEffects.setVisibility(View.GONE);
        	key= MenuScheme.SHOW_MAIN;
        	keyFX=EffectsSubMenu.NONE;  
            break;
        case SHOW_EFFECTS:
        	btnMasterHandle.setVisibility(View.GONE);
        	panelMain.setVisibility(View.GONE);
	    	panelEffects.setVisibility(View.VISIBLE);
        	key= MenuScheme.SHOW_EFFECTS;
        	keyFX=EffectsSubMenu.NONE;  
            break;
        default: 
        	btnMasterHandle.setVisibility(View.GONE);
        	panelMain.setVisibility(View.GONE);
	    	panelEffects.setVisibility(View.GONE);
        	key= MenuScheme.NO_MENU;
        	keyFX=EffectsSubMenu.NONE;  
        	break;
    	}
    	
	}
    /********************************
     * EFFECTS MENU BUTTON HANDLERS *
     ********************************/
    private OnClickListener clickTextMenuBtn = new OnClickListener(){
    	public void onClick(View thisBtn){
        	hideAllChildren(panelEffectsSub);
        	if (slidemenu_effects_text == null){
        		slidemenu_effects_text = slidemenu_effects_text_stub.inflate();
        		
        		textFromUser = (EditText) findViewById(R.id.text_from_user);  
        		textFromUser.addTextChangedListener(new TextWatcher() {
        	        public void onTextChanged(CharSequence s, int start, int before, int count) {

        	        }
        	        public void afterTextChanged(Editable text) {
        	        	//mText.setText( text.toString());

        	        }
        	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	        	
        	        }
        	    });

        		textFromUser.setOnKeyListener(new OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        
                    	if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER ) {
                          // Perform action on key press
                        	//mText.setText( textFromUser.getText().toString());
                        	
                          return true;
                        }
                    	
                    	return false;
                    }

                });
                
                Button addTextBtn = (Button) findViewById( R.id.effect_add_text );
                addTextBtn.setOnClickListener( new View.OnClickListener() {
        			public void onClick(View v) {
        				
        				mLayerManager.addTextViewWithText( textFromUser.getText().toString() );
        			}
        		});
                Button removeTextBtn = (Button) findViewById( R.id.effect_remove_text );
                removeTextBtn.setOnClickListener( new View.OnClickListener() {
        			public void onClick(View v) {
        			}
        		});
        	
        	
        	}
        	else slidemenu_effects_text.setVisibility(View.VISIBLE);
        	makeBtnSelected(thisBtn);

            keyFX=EffectsSubMenu.TEXT;
    	}
    	
    };
    
    private OnClickListener clickTintsMenuBtn = new OnClickListener(){
    	public void onClick(View thisBtn){
    		hideAllChildren(panelEffectsSub);
    		if (slidemenu_effects_tints == null){
        		slidemenu_effects_tints = slidemenu_effects_tints_stub.inflate();
        		Button tintsBWBtn = (Button)findViewById(R.id.effects_tints_blackwhite);
          		tintsBWBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);


            		}
          		});	
        		Button tintsSepiaBtn = (Button)findViewById(R.id.effects_tints_sepia);
          		tintsSepiaBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);

            		}
          		});
        		Button tintsRGBBtn = (Button)findViewById(R.id.effects_tints_rgb);
          		tintsRGBBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);
        				
        				//((CelebCamApplication) getApplication()).setBitmap(mEditView.getBitmap());

        				startActivityForResult( new Intent(mContext, FXProcessor.class), PERFORM_COLOR_TRANSFORM);
            		}
          		});
        		Button tintsNoneBtn = (Button)findViewById(R.id.effects_tints_none);
          		tintsNoneBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);

            		}
          		});

        	}
        	else slidemenu_effects_tints.setVisibility(View.VISIBLE);
        	makeBtnSelected(thisBtn);
            keyFX=EffectsSubMenu.TINT;      
    	}	
    };
  
     
    /** Click the border button in effects menu to open border submenu */
    private OnClickListener clickBorderMenuBtn = new OnClickListener() {
        public void  onClick(View thisBtn) {
        	hideAllChildren(panelEffectsSub);
        	if (slidemenu_effects_borders == null){
        		//inflate border submenu and add button handlers
        		slidemenu_effects_borders = slidemenu_effects_borders_stub.inflate();
        		final ViewGroup borderBtns = (ViewGroup) findViewById( R.id.effects_border_buttons );	
        		//assign the same button handler to all border buttons
            	for (int i=0;i<borderBtns.getChildCount();i++){
            		final Button btn = (Button)borderBtns.getChildAt(i);
            		btn.setOnClickListener(clickBorderBtn);
            		

            	}            		
        	}
        	else slidemenu_effects_borders.setVisibility(View.VISIBLE);

        	makeBtnSelected(thisBtn);
            keyFX=EffectsSubMenu.BORDER;
        }
     };
     /** Click a particular border style button to apply an effect */
     private OnClickListener clickBorderBtn = new OnClickListener() {
         public void  onClick(View thisBtn) {
             Button b = (Button)thisBtn;
             
             toggleEffectBtnOn(thisBtn);
             
         }
      };
      /** Sparkle effects menu sparkles button **/
      private OnClickListener clickSparkleMenuBtn = new OnClickListener() {
          public void  onClick(View thisBtn) {
          	hideAllChildren(panelEffectsSub);
          	if (slidemenu_effects_sparkles == null){
          		//inflate border submenu and add button handlers
          		slidemenu_effects_sparkles = slidemenu_effects_sparkles_stub.inflate();
          		//assign listeners to buttons
          		Button spreadSparklesBtn = (Button)findViewById(R.id.effects_sparkle_spreadable_btn);
          		spreadSparklesBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);
            		}
          		});	

           		Button brushSparklesBtn = (Button)findViewById(R.id.effects_sparkle_brushable_btn);
          		brushSparklesBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);

            		}
          		});
           		Button noSparklesBtn = (Button)findViewById(R.id.effects_sparkle_none_btn);
          		noSparklesBtn.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View thisBtn ){ 
            			toggleEffectBtnOn(thisBtn);
            		}
          		});
          	}
          	else slidemenu_effects_sparkles.setVisibility(View.VISIBLE);

          	makeBtnSelected(thisBtn);
            keyFX=EffectsSubMenu.SPARKLES;
          }
       };
 
}