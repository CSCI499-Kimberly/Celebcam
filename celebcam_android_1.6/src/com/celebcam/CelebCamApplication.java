package com.celebcam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import android.net.Uri;

public class CelebCamApplication extends Application {

	private String mURLOfMostRecentPhoto;
	private CelebCamFont mFont;
	
	public void onCreate()
	{
		super.onCreate();

		CelebCamFont.setFont(getBaseContext(), R.drawable.font_512, 71, 50);
		
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        if( mExternalStorageWriteable )
        {
        	Log.d("CelebCamApplication", "storage writable.");
        	createExternalStoragePublicPicture();
        	
        }
        else
        {
        	Log.d("CelebCamApplication", "cannot write");
        }
	}
	

	public void onTerminate()
	{
		super.onTerminate();
	}
	
	public void setMostRecentURL( String url )
	{
		this.mURLOfMostRecentPhoto = url;
	}
	
	public String getMostRecentURL()
	{
		return mURLOfMostRecentPhoto;
	}
	
	public CelebCamFont getFont()
	{
		return this.mFont;
	}

	public void sendEmail () {
		
		if( getMostRecentURL() == null )
		{
			Toast.makeText(getApplicationContext(), "No Photo To Email.", 10).show();
			return;
		}
		
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		getResources().getString(R.string.email_subject));
		
		emailIntent.setType("image/jpg");
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources()
		// .getString(R.string.email_message));

		Uri uri = Uri.parse( getMostRecentURL() );
		
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		
		Intent t = Intent.createChooser(emailIntent, "Send mail");
		
		t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK	);
		startActivity(t);
		
	}
	
	void createExternalStoragePublicPicture() {
	    // Create a path where we will place our picture in the user's
	    // public pictures directory.  Note that you should be careful about
	    // what you place here, since the user often manages these files.  For
	    // pictures and other media owned by the application, consider
	    // Context.getExternalMediaDir().
	    File path = Environment.getExternalStorageDirectory();
	   
	    
	   
	    //File file = new File(path, "DemoPicture.jpg");

	    Log.d("CelebcamApp data", path.getParent() );
	    try {
	        // Make sure the Pictures directory exists.
	        path.mkdirs();

	        // Very simple code to copy a picture from the application's
	        // resource into the external file.  Note that this code does
	        // no error checking, and assumes the picture is small (does not
	        // try to copy it in chunks).  Note that if external storage is
	        // not currently mounted this will silently fail.
	        InputStream is = getResources().openRawResource(R.drawable.tom_cruise);
	       // OutputStream os = new FileOutputStream(file);
	        byte[] data = new byte[is.available()];
	        is.read(data);
	       // os.write(data);
	        is.close();
	        //os.close();

	        // Tell the media scanner about the new file so that it is
	        // immediately available to the user.
	        MediaScannerConnection connection = new MediaScannerConnection( this, 
	        new MediaScannerConnection.MediaScannerConnectionClient() {
	            public void onScanCompleted(String path, Uri uri) {
	                Log.i("ExternalStorage", "Scanned " + path + ":");
	                Log.i("ExternalStorage", "-> uri=" + uri);
	            }

				@Override
				public void onMediaScannerConnected() {
					// TODO Auto-generated method stub
					
				} });
				
	        connection.connect();
	        Uri uri;
	       //connection.scanFile(file.toString(),  null);
	             
	            
	    } catch (IOException e) {
	        // Unable to create file, likely because external storage is
	        // not currently mounted.
	        //Log.w("ExternalStorage", "Error writing " + file, e);
	    }
	}
//
//	void deleteExternalStoragePublicPicture() {
//	    // Create a path where we will place our picture in the user's
//	    // public pictures directory and delete the file.  If external
//	    // storage is not currently mounted this will fail.
//	    File path = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File file = new File(path, "DemoPicture.jpg");
//	    file.delete();
//	}
//
//	boolean hasExternalStoragePublicPicture() {
//	    // Create a path where we will place our picture in the user's
//	    // public pictures directory and check if the file exists.  If
//	    // external storage is not currently mounted this will think the
//	    // picture doesn't exist.
//	    File path = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File file = new File(path, "DemoPicture.jpg");
//	    return file.exists();
//	}
//	
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
