package com.celebcam;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;


public class FXProcessor extends Activity {
    
	CelebCamEditView mImageSurface;
	TouchSliderGroup mSliders;
	ProgressBar		 mProgressBar;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.data_manipulation);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		Bitmap mBitmap = ((CelebCamApplication) getApplication()).getCurrentBitmap();
		
		if( mBitmap == null )
			finish();
		
		//CelebCamEffectsLibrary.setPreviewSize(250, 250);
		CelebCamEffectsLibrary.setPreviewBitmap(mBitmap);
		CelebCamEffectsLibrary.setState2(CelebCamEffectsLibrary.PREVIEW);
		CelebCamEffectsLibrary.slipChannels();
        
		mBitmap = null;
		
		mProgressBar = (ProgressBar) findViewById( R.id.progressBar );
		mProgressBar.setVisibility( View.INVISIBLE );
		
		mImageSurface = (CelebCamEditView) findViewById( R.id.image_surface);
			
        mSliders = (TouchSliderGroup) findViewById( R.id.touch_sliders);
        
        mSliders.setProgressBar( mProgressBar );
        mSliders.setup(3, mImageSurface, this);
        
       
        
    }
}