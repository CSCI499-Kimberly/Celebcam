package com.celebcam;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.celebcam.R;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Context;

import java.io.File;


public class DataAcquisitionActivity extends Activity implements SurfaceHolder.Callback, TextWatcher, CCMemoryWatcher {

	public int getSizeInBytes() {
		int m = 0;
		
		if( tempdata != null )
			m= tempdata.length;
		
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
    private Size 		  mPictureSize;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView   mSurfaceView;
    
    private Button 		  mAddText;
    private Button	      mAddBorder;
    private Button	      mAddSparkles;
    private Button	      mSparklesBrush;
    private Button        mBlackAndWhite;
    private Button        mColor;
    private Button        mEdit;
    private Button        mSave;
    private Button        mEmail;
    
    private Button        mDebug;
	
    private View 		  mSliders;
    
	private byte[] tempdata;
	
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
	
	private static int id = 0;
	
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	

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
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_acquisition);
        
        Log.d(TAG, "onCreate Called");
        CCDebug.registerMemoryWatcher(this);
        
        mContext = this;
        
        mDbHelper = new CelebCamDbHelper(this);

        // View that camera preview is render on
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface);
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
        				mCamera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
        			else
        				mZoomAndSnapButton.unlockCurrentView();
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
            	mText.setText( mEditText.getText().toString());
            	Log.d(TAG, "key pressed in textview");
                if ((event.getAction() == KeyEvent.ACTION_DOWN) ) {
                  // Perform action on key press
                	mText.setText( mEditText.getText().toString());
                	
                  return true;
                }
                return true;
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
        
        mAddBorder = (Button) findViewById( R.id.effect_add_border );
        mAddBorder.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if( mBorder.getVisibility() == View.VISIBLE)
					mBorder.setVisibility(View.GONE);
				else
					mBorder.setVisibility(View.VISIBLE);
				
			}
		});
        
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
				}
				else
				{
					mEditView.setVisibility(View.VISIBLE);
					mEditView.invalidate();
				}
				
			}
		});

        mSave = (Button) findViewById( R.id.save_button );
        mSave.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				done();
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
        
    }
    
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        
    	Log.d(TAG, "surfaceCreated called");
    	
        mCamera = Camera.open();
        //mSurfaceView.layout(0, 0, 200, 200);
        if( mCamera == null )
        	return;
        
        mPictureSize = mCamera.getParameters().getPictureSize();
        
        mText.setPublishSize(     mPictureSize );
        mSparkles.setPublishSize( mPictureSize );
        mBorder.setPublishSize(   mPictureSize );
        
        Log.d(TAG, "cam width" + Integer.toString(mPictureSize.width) + " cam height: " + Integer.toString( mPictureSize.height));
        
        if( mPublishBitmap == null )
        	mPublishBitmap = Bitmap.createBitmap( mPictureSize.width, mPictureSize.height, Bitmap.Config.ARGB_8888  );
        
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
       	    width = (mPictureSize.width*height)/mPictureSize.height;
        }
        
        if( mPreviewBitmap == null )
        	mPreviewBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);

        CelebCamEffectsLibrary.setPublishSize( mPictureSize.width, mPictureSize.height );
        CelebCamEffectsLibrary.setPreviewSize( width, height );
        
        CelebCamEffectsLibrary.setPreviewBitmap(mPreviewBitmap);
        CelebCamEffectsLibrary.setPublishBitmap(mPublishBitmap);
        
        mZoomAndSnapButton.setCamera( mCamera );
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	
    	Log.d(TAG, "surfaceDestroyed called");
        mCamera.stopPreview();
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
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(w, h);
        //mSurfaceView.layout(0, 0, 200, 200);

        mCamera.setParameters(p);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            

        } catch (Exception e){
        
        Log.d("ERROR", "Error starting camera preview: " + e.getMessage());
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

	ShutterCallback mShutterCallback = new ShutterCallback(){
		public void onShutter() {}
	};
	
	PictureCallback mPictureCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {}
	};
	
	PictureCallback mjpeg = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			if( data != null ) {
				tempdata = data;
				startEditor();
			}
		}
	};

	void startEditor()
	{
		mCameraBitmap = BitmapFactory.decodeByteArray( tempdata, 0, tempdata.length);

		CelebCamEffectsLibrary.setState( CelebCamEffectsLibrary.PREVIEW );	
		
		CelebCamEffectsLibrary.composeImage( mCameraBitmap, null, CelebCamOverlaidView.current.getBitmap(), CelebCamOverlaidView.current.getMatrix() );
		
		CelebCamEffectsLibrary.addText(mText);
		
		CelebCamEffectsLibrary.addSparkles( mSparkles );
		
		CelebCamEffectsLibrary.addBorder(mBorder);
		
		CelebCamEffectsLibrary.slipChannels();

		mEditView.setBitmap(mPreviewBitmap);
		mEditView.setVisibility(View.VISIBLE);
		
	}
	
	void done() {

		if( tempdata == null )
		{
			Toast.makeText( mContext, "No photo to save", 10).show();
			return;
		}
		
		mCameraBitmap = BitmapFactory.decodeByteArray( tempdata, 0, tempdata.length);
		
		CelebCamEffectsLibrary.setState( CelebCamEffectsLibrary.PUBLISH);
		
		CelebCamEffectsLibrary.composeImage( mCameraBitmap, null, CelebCamOverlaidView.current.getBitmap(), CelebCamOverlaidView.current.getMatrix() );
		
		CelebCamEffectsLibrary.addText(mText);
		
		CelebCamEffectsLibrary.addSparkles( mSparkles );
		
		CelebCamEffectsLibrary.addBorder(mBorder);
		
		String url = Images.Media.insertImage( getContentResolver(), CelebCamEffectsLibrary.getCurrentBitmap(), "celebcam_photo","Taken with celebcam" );
	
		((CelebCamApplication) getApplication() ).setMostRecentURL( url );
		
		mCameraBitmap.recycle();
		
		Bundle bundle = new Bundle();

		mDatabase = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); //

		id++;
		values.put(CelebCamDbHelper.C_ID, id);
		values.put(CelebCamDbHelper.C_CREATED_AT, 1);
		//values.put(CelebCamDbHelper.C_SOURCE, "Hello");
		values.put(CelebCamDbHelper.C_TEXT, "CelebCam Photo");
		values.put(CelebCamDbHelper.C_USER, "CelebCam");

		try {
			mDatabase.insertOrThrow(CelebCamDbHelper.TABLE, null, values); //

			} catch (SQLException e) { //
			// Ignore exception
			}
			
			mDatabase.close();
		if( url != null ) {
			bundle.putString("url", url);
			
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			
	

		}else{
			Toast.makeText( this, "Picture can not be saved", Toast.LENGTH_SHORT).show();
		}
		
		mCamera.startPreview();
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
	
	
	
}