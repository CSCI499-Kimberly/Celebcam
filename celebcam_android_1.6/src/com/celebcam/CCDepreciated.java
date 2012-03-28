package com.celebcam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

class CameraView extends SurfaceView
{

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void draw( Canvas canvas )
	{
		
		Matrix matrix = new Matrix();
		matrix.setRotate(45);
		 canvas.setMatrix(matrix);
		 
		 Log.d("DataAcquisitionActivity", "now calling onDraw for camera view called");
		 super.draw(canvas);
	}
}

public class CCDepreciated {

	private float angle(float[] mTouchPoints)
	{
		float angle = 0;
		
		float dotProduct = (mTouchPoints[0]*mTouchPoints[2]) + (mTouchPoints[1]*mTouchPoints[3]);
		
		float magnitude1 = (float) Math.sqrt(( mTouchPoints[0]* mTouchPoints[0]) + (mTouchPoints[1]*mTouchPoints[1]));
		float magnitude2 = (float) Math.sqrt(( mTouchPoints[2]* mTouchPoints[2]) + (mTouchPoints[3]*mTouchPoints[3]));
		
		float qout = dotProduct/(magnitude1*magnitude2);
		
		angle = 1/(float)Math.cos(qout);
		// cos theta = dotProduct/ 
		return angle;
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(
	              ), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
