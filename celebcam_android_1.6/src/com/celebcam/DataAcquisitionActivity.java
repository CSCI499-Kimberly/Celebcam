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
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.TextView;
import com.celebcam.R;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Context;
import android.content.pm.ActivityInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class DataAcquisitionActivity extends Activity implements SurfaceHolder.Callback, TextWatcher,OnSharedPreferenceChangeListener,
 CCMemoryWatcher {

	public int getSizeInBytes() {
		int m = 0;
		
		if( mPublishBitmap != null )
			m += mPublishBitmap.getHeight()*mPublishBitmap.getWidth()*4;
		
		if( mPreviewBitmap != null )
			m+= mPreviewBitmap.getHeight()*mPublishBitmap.getWidth()*4;
		
		if( mCameraBitmap  != null )
			m+= mCameraBitmap.getHeight()*mCameraBitmap.getWidth()*4;
		
		return (31*4)+ m;
	}


	public int getStaticBytes() {

		return 4;
	}
	
	private String TAG = "DataAcquisitionActivity";
	
    private Camera        mCamera;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView   mSurfaceView;
    
    private Button 		  mAddText;
    private Button	      mAddSparkles;
    private Button	      mSparklesBrush;
    private Button        mBlackAndWhite;
    private Button        mColor;
    private Button        mEdit;
    private Button        mSave;
    private Button        mEmail;
    private Button        mGallery;
    
    private Button        mDebug;
	
    private View 		  mSliders;
	
	private Bitmap mPublishBitmap;
	private Bitmap mPreviewBitmap;
	private Bitmap mCameraBitmap;
	
	ImageView mImageView;
	
	private CelebCamController   mZoomAndSnapButton;
	
	private CelebCamOverlaidView mCelebView;
	private CelebCamTextView	 mText;
	private CelebCamSparklesView mSparkles;
	private CelebCamBorderView	 mBorder;
	
	private CelebCamEditView     mEditView;
	
	private CelebCamSlider		 mRedSlider;
	private CelebCamSlider		 mGreenSlider;
	private CelebCamSlider	     mBlueSlider;
	
	private EditText mEditText;
	
	private Context  mContext;
	
	private boolean mPreviewRunning;
	
	private String TAKEN_PHOTO   = "tp1";
	
	private String PREVIEW_PHOTO = "pp";

	private CelebCamApplication mApp;
	SharedPreferences prefs;
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	
	
	
	//private static final String TAG = "PersistOptions";

	private static final String callbackURL = "app://twitter";
	private static final String consumerKEY = "8JlWrU08QfMLG3SwVUU4LQ";
	private static final String consumerSECRET = "NrHaVm2My0t2wf2nUaMt2Vno98mPHzg8YOgHBxlt1M";
	
	private static final String userAccessTOKEN = "accessToken";
	private static final String userAccessTokenSECRET = "accessTokenSecret";
	private SharedPreferences mPrefs;

	
	private Twitter twitter;
	private RequestToken reqTOKEN;
	
	EditText textField;
	
	
	

	private CelebCamEnum mLaunch;
	
	static byte NONE		  = 0;
	static final byte SETTINGS_PREF = 1;

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
				
				Log.d(TAG,"length of camera data : " + Integer.toString(data.length));

				mApp.storeInCache(data, TAKEN_PHOTO);
				
				Bitmap b = BitmapFactory.decodeByteArray( data, 0, data.length);
				data = null;
				
				System.gc();
				
				mCelebView.release(mApp);
				
				
				startEditor(b);
			}
		}
	};
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate Called");
        
        setContentView(R.layout.data_acquisition);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        

        Log.i(TAG, "STARTED - onCreate() ");

		mPrefs = getSharedPreferences("twitterPrefs", MODE_PRIVATE);
		Log.i(TAG, "Got Preferences");
        
	//	String link = "http://tinyurl.com/c3lcam";	//get URL from server
		
	//	String celebrityName = "Rihanna";	//get celebrity from server
		
    	textField = (EditText)findViewById(R.id.twitter_text_field);
    	textField.setText("Check out this picture I took with " + ". " + ". #CelebCam" , TextView.BufferType.NORMAL );

		
        //Create new twitter item using 4jtwitter
      	twitter = new TwitterFactory().getInstance();
      	Log.i(TAG, "Got Twitter4j");
      		
      	// Tell twitter4j that we want to use it with our app
      	// Use twitter4j to authenticate
      	twitter.setOAuthConsumer(consumerKEY, consumerSECRET);
      	Log.i(TAG, "Inflated Twitter4j");
		
                
      		
        prefs = PreferenceManager.getDefaultSharedPreferences(this); //
        prefs.registerOnSharedPreferenceChangeListener(this);

        CCDebug.registerMemoryWatcher(this);
        
        mContext  = this;
        
        mDbHelper = new CelebCamDbHelper(this);

        // View that camera preview is render on
        mSurfaceView   = (SurfaceView) findViewById(R.id.camera_surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		
        mCelebView = (CelebCamOverlaidView) findViewById( R.id.celeb_surface );
        
        mZoomAndSnapButton = (CelebCamController) findViewById(R.id.zoom_snap_button);
        
        mZoomAndSnapButton.setDefaultControlledView( mCelebView );
        mZoomAndSnapButton.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View view )
        		{
        			if( mZoomAndSnapButton.isDefault() )
        			{
        				Log.d(TAG, "taking picture...");
    					
    					CelebCamEffectsLibrary.release();
    					mEditView.release();
    					System.gc();
    					mCelebView.restore(mApp);
        				mCamera.takePicture(null, null ,mjpeg);
        				
        			}
        			else
        			{
        				mZoomAndSnapButton.unlockCurrentView();
        			}
        		}
        });
        
        mText     = (CelebCamTextView) findViewById( R.id.text_surface);
        
        mSparkles = (CelebCamSparklesView) findViewById( R.id.sparkles_view);
        
        
        mBorder   = (CelebCamBorderView) findViewById( R.id.border_view);
        
        mEditView = (CelebCamEditView) findViewById( R.id.editing_view);
        mEditView.setVisibility(View.INVISIBLE);
        
        mEditText = (EditText) findViewById(R.id.text_to_add);
        
        mEditText.addTextChangedListener(this);

        mEditText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                
            	if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER ) {
                  // Perform action on key press
                	mText.setText( mEditText.getText().toString());
                	
                  return true;
                }
            	
            	return false;
            }

        });
        
        mAddText = (Button) findViewById( R.id.effect_add_text );
        mAddText.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if( mText.getVisibility() == View.VISIBLE)
					mText.setVisibility(View.GONE);
				else
					mText.setVisibility(View.VISIBLE);

			}
		});
        
        
        String[] borders = getResources().getStringArray(R.array.borders_array);
        
//        ListView lv = (ListView) findViewById(R.id.border_list);
//        
//        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, borders));
//
//        
//        lv.setTextFilterEnabled(true);
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//          public void onItemClick(AdapterView<?> parent, View view,
//              int position, long id) {
//            // When clicked, show a toast with the TextView text
//            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//                Toast.LENGTH_SHORT).show();
//            
//            mBorder.selectBorderByName(((TextView) view).getText().toString());
//        	mBorder.setVisibility(View.VISIBLE);New UserNew User
//          }
//        });

        
        
        mAddSparkles = (Button) findViewById( R.id.effect_add_sparkles );
        mAddSparkles.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				
				if( mSparkles.getVisibility() == View.VISIBLE)
				{
					mSparkles.setVisibility(View.GONE);
				}
				else
				{
					mSparkles.setVisibility(View.VISIBLE);
				}
			}
		});
        
        mSparklesBrush = (Button) findViewById( R.id.sparkles_brush);
        mSparklesBrush.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				
					mSparkles.toggleEnable();
			}
		});       
        mBlackAndWhite = (Button) findViewById( R.id.effect_bw );
        mBlackAndWhite.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				
			}
		});

        mSliders = findViewById( R.id.sliders);
        mColor = (Button) findViewById( R.id.effect_color );
        mColor.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				if( mSliders.getVisibility() == View.VISIBLE)
				{
					mSliders.setVisibility(View.GONE);
				}
				else
				{
					mSliders.setVisibility(View.VISIBLE);
				}
				
			}
		});
        
        mEdit = (Button) findViewById( R.id.edit_button );
        mEdit.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				if( mEditView.getVisibility() == View.VISIBLE)
				{
					mEditView.setVisibility(View.GONE);
					mCamera.startPreview();
					
					CelebCamEffectsLibrary.release();
					mEditView.release();
					System.gc();
					mCelebView.restore(mApp);
				}
				else
				{
					mCelebView.release(mApp);
					mEditView.setBitmap( ((CelebCamApplication)getApplication()).loadFromCache(TAKEN_PHOTO));
					
					mEditView.setVisibility(View.VISIBLE);
					mEditView.invalidate();
				}
				
			}
		});

        
        mSave = (Button) findViewById( R.id.save_button );
        mSave.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				mCelebView.release(mApp);
				mEditView.release();
				
				System.gc();
				
				Bitmap bitmap = ((CelebCamApplication)getApplication()).loadFromCache(TAKEN_PHOTO);
				save(finalProcess( bitmap ));

				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
		});
        
        mGallery = (Button) findViewById( R.id.gallery_button );
        mGallery.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity( new Intent(mContext, PhotoBrowserActivity.class));
			}
		});
        
        mEmail = (Button) findViewById( R.id.email_button );
        mEmail.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				((CelebCamApplication) getApplication() ).sendEmail();
				
			}
		});
        
        mDebug = (Button) findViewById( R.id.debug_button );
        mDebug.setOnClickListener( new View.OnClickListener() {

			public void onClick(View v) {
				CCDebug.toggle();
			}
		});
        
        SliderGroup colorGroup = new SliderGroup(3);
        
        mRedSlider   = (CelebCamSlider) findViewById( R.id.red_controller);
        mGreenSlider = (CelebCamSlider) findViewById( R.id.green_controller);
        mBlueSlider  = (CelebCamSlider) findViewById( R.id.blue_controller);
        
        colorGroup.addSlider( mRedSlider );
        colorGroup.addSlider(mGreenSlider);
        colorGroup.addSlider(mBlueSlider);
        
        mRedSlider.setChannel(Channel.RED);
        mGreenSlider.setChannel(Channel.GREEN);
        mBlueSlider.setChannel(Channel.BLUE);
        
        mRedSlider.setEditView(mEditView);
        mGreenSlider.setEditView(mEditView);
        mBlueSlider.setEditView(mEditView);
        
        mApp = (CelebCamApplication) getApplication();
        
        //System.gc();
        
  		
        prefs = PreferenceManager.getDefaultSharedPreferences(this); //
        prefs.registerOnSharedPreferenceChangeListener(this);

        mLaunch = CelebCamEnum.NONE;
        
//        if( prefs.getString("twitter_pin", "NOT_SET").equals("NOT_SET"))
//        	mLaunch = CelebCamEnum.TWITTER_PREF;
    }
    
    
    public void surfaceCreated(SurfaceHolder holder) {

    	Log.d(TAG, "surfaceCreated called");
    	
        mCamera = Camera.open();
        //mZoomAndSnapButton.setCamera( mCamera );
        if( mCamera == null )
        	return;
        
        Parameters p = mCamera.getParameters();

        Size mPictureSize = mCamera.getParameters().getPictureSize();
        
        int height = 0;
        int width  = 0;
        
        if( mEditView.getWidth() < mEditView.getHeight())
        {
        	 width = mEditView.getWidth();
        	 height = (mPictureSize.height*mEditView.getWidth())/mPictureSize.width;
        }
        else if( mEditView.getWidth() > mEditView.getHeight())
        {
        	Log.d(TAG, "width greater than height");
       	 	height = mEditView.getHeight();
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
        Log.d(TAG, "width: " + Integer.toString(w) + " height: " + Integer.toString(h));
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

	void startEditor(Bitmap bitmap)
	{
		
		String strFunction = " ";
		try{
			if( bitmap == null)
				Log.d(TAG, "Editor was passed a null pointer.");
			
			strFunction = "setPreviewBitmap";
			
			CelebCamEffectsLibrary.setPreviewBitmap(bitmap);
			
			strFunction = "setState";
			
			CelebCamEffectsLibrary.setState2( CelebCamEffectsLibrary.PREVIEW );	
			
			strFunction = "restore";
			
			mCelebView.restore(mApp);
			
			strFunction = "addImage";
			
			CelebCamEffectsLibrary.addImage( mCelebView.getBitmap(), mCelebView.getMatrix() );
			
			strFunction = "addText";
			
			CelebCamEffectsLibrary.addText(mText);
			
			strFunction = "addSparkles";
			
			CelebCamEffectsLibrary.addSparkles( mSparkles );
			
			strFunction = "addBorder";
			
			CelebCamEffectsLibrary.addBorder(mBorder);
	
			//CelebCamEffectsLibrary.slipChannels();
			
			strFunction = "mEditView.setBitmap";
			
			mEditView.setBitmap(CelebCamEffectsLibrary.getCurrentBitmap2());
			
			strFunction = "save";
			
			save(CelebCamEffectsLibrary.getCurrentBitmap2());
			
			strFunction = "setVisibility";
			mEditView.setVisibility(View.VISIBLE);
		
		}
		catch(Exception e)
		{
			Log.d(TAG, "startEditor encountered a problem at " + strFunction);
			Toast.makeText(this, "Error: Cannot start editor.", 10);
			
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
		
		mCelebView.restore(mApp);
		CelebCamEffectsLibrary.addImage( mCelebView.getBitmap(), mCelebView.getMatrix() );
		
		CelebCamEffectsLibrary.addText(mText);
		
		CelebCamEffectsLibrary.addSparkles( mSparkles );
		
		CelebCamEffectsLibrary.addBorder(mBorder);
				
		mCamera.startPreview();
		
		return CelebCamEffectsLibrary.getCurrentBitmap();
		
	}
	
	public void afterTextChanged( Editable text )
	{
		mText.setText( text.toString());
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
				mDatabase.insertOrThrow(CelebCamDbHelper.TABLE, null, values); //

			} catch (SQLException e) { //
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
		Log.i(TAG, "STARTED - onResume()");

//		ConfigurationBuilder cb;
//		switch( mLaunch )
//		{
//		case NONE:
//			cb = new ConfigurationBuilder();
//	  		cb.setDebugEnabled(true)
//			  .setOAuthConsumerKey(consumerKEY)
//			  .setOAuthConsumerSecret(consumerSECRET)
//			  .setOAuthAccessToken(prefs.getString("oauth_access_token", ""))
//			  .setOAuthAccessTokenSecret(prefs.getString("oauth_access_secret", ""));
//			
//			twitter = new TwitterFactory(cb.build()).getInstance();
//			break;
//		case TWITTER_PREF:
//			cb = new ConfigurationBuilder();
//			
//	  		cb.setDebugEnabled(true)
//			  .setOAuthConsumerKey(consumerKEY)
//			  .setOAuthConsumerSecret(consumerSECRET);
//	  		
//	  		twitter = new TwitterFactory(cb.build()).getInstance();
//			authenticate_pre_pin();
//			break;
//		default:
//			authenticate_post_pin(prefs.getString("twitter_pin", ""));
//			break;
//		}
		
//		mLaunch = CelebCamEnum.PERSIST_OPTIONS;
  		

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
				
				Intent intent = new Intent( this, TwitterLauncherActivity.class );
				
				startActivityForResult( intent, RESULT_OK );
			}
			/*
			//Toast.makeText(this, fieldContents, Toast.LENGTH_SHORT).show();
			Log.i(TAG, "New User");
			authent();
			Log.i(TAG, "New User authentication");
			*/
		}

    }
	
    
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		sendTweet();
		
		
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