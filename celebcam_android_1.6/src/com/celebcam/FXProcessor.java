package com.celebcam;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;


public class FXProcessor extends Activity {
    
	CelebCamEditView mImageSurface;
	TouchSliderGroup    mSliders;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.data_manipulation);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		
		CelebCamEffectsLibrary.setPreviewSize(250, 250);
		CelebCamEffectsLibrary.setPreviewBitmap(mBitmap);
		CelebCamEffectsLibrary.setState2(CelebCamEffectsLibrary.PREVIEW);
		CelebCamEffectsLibrary.slipChannels();
        
		mBitmap = null;
		
		mImageSurface = (CelebCamEditView) findViewById( R.id.image_surface);
		
		
        mSliders = (TouchSliderGroup) findViewById( R.id.touch_sliders);
        
        mSliders.setup(3, mImageSurface, this);
        
        
    }
}