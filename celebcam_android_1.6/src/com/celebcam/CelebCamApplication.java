package com.celebcam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import android.net.Uri;

public class CelebCamApplication extends Application {

	private final String TAG = "CelebCamApplication";
	private String       mURLOfMostRecentPhoto;
	private CelebCamFont mFont;
    private boolean      mExternalStorageAvailable = false;
    private boolean      mExternalStorageWriteable = false;
    
    static private CelebCamApplication mCurrent;
	
    public void onCreate()
	{
		super.onCreate();

		mCurrent = this;
		
		CelebCamGlobals.create( getBaseContext() );
		
		CelebCamButton.setColorScheme(new CelebCamColorScheme(0x44de9105, 0x99594a2f, 0x99e8b860, 0xfffaf3e5));
		
		CelebCamFont.setFont(getBaseContext(), R.drawable.font_512, 71, 50);
		 
		CelebCamLibrary.createLibrary();

		
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
	}
	
    static public CelebCamApplication getApplication()
    {
    	return mCurrent;
    }
    
	
	public boolean isWritable()
	{
		return mExternalStorageWriteable;
	}
	
	public boolean isStorageAvailable()
	{
		return mExternalStorageAvailable;
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
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.email_message));

		Uri uri = Uri.parse( getMostRecentURL() );
		
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		
		Intent t = Intent.createChooser(emailIntent, "Send mail");
		
		t.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK	);
		startActivity(t);
		
	}
	
	public String mostRecentFile()
	{
		return mMostRecentFile;
	}
	
	String mMostRecentFile = new String();
	
	File file;
	public void storeInCache(byte[] data, String name)
	{

		Log.d(TAG,"storing in cache...");
		
		    try {
		    	deleteFile(name);
			FileOutputStream tempFile = openFileOutput(name, Context.MODE_PRIVATE);//new FileOutputStream(file);
			
			if( tempFile != null )
			{
				tempFile.write(data);
				tempFile.close();
				
				tempFile = null;
				data     = null;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException");
		}
		
		Log.d(TAG,"store completed.");
	}
	
	public void storeInCache(Bitmap bitmap, String name)
	{

		Log.d(TAG,"storing in cache...");
		
		 try {
		    
		    deleteFile(name);
		    
			FileOutputStream tempFile = openFileOutput(name, Context.MODE_PRIVATE);//new FileOutputStream(file);
			
			if( tempFile != null )
			{
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, tempFile);
				tempFile.close();
				
				tempFile = null;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException");
		}
		
		Log.d(TAG,"store completed.");
	}
	
	
	public Bitmap loadFromCache( String name )
	{
		Log.d(TAG, "loading from cache...");
		
		Bitmap bitmap = null;
		try {
		
			FileInputStream tempFile = openFileInput(name);

			bitmap =  BitmapFactory.decodeStream(tempFile);
		
			tempFile = null;
		
			if(bitmap == null )
				Log.d("DataAcquisitionActivity", "loadFromCache failed");
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("DataAcquisitionActivity", "loadFromCache file Not found");
		}
		
		Log.d(TAG, "load completed.");
		
		return bitmap;
	}

	public void emptyCache()
	{
		if( file != null )
		file.delete();
	}
	
	public void store(short[] data, String name)
	{
		try {
			
			
			FileOutputStream tempFile = openFileOutput(name, Context.MODE_PRIVATE);
			
			
			if( tempFile != null )
			{
				//tempFile.write(data);
				tempFile.close();
				data = null;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean load(byte[] data, String name)
	{
		try {
			FileInputStream tempFile = openFileInput(name );
			
			if( tempFile != null )
			{
				tempFile.read(data);
				tempFile.close();
				return true;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public void writeImageToDisk( String fileName, Bitmap bitmap ) {
		
		if( !isWritable())
			return;
		
	    File path = new File( Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/Celebcam");
	    
	    mMostRecentFile = path +"/"+ fileName;
	    
	    
	    Log.d("FileName: ", mMostRecentFile);
	    
	    File file = new File(path, fileName );

	    try {
	        // Make sure the Pictures directory exists.
	        path.mkdirs();
        
	        OutputStream os = new FileOutputStream(file);
	        
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
	        os.close();

	        // Tell the media scanner about the new file so that it is
	        // immediately available to the user.
	        MediaScannerConnection connection = new MediaScannerConnection( this, 
	        new MediaScannerConnection.MediaScannerConnectionClient() {
	            public void onScanCompleted(String path, Uri uri) {
	                Log.i("ExternalStorage", "Scanned " + path + ":");
	                Log.i("ExternalStorage", "-> uri=" + uri);
	            }


				public void onMediaScannerConnected() {
					// TODO Auto-generated method stub
					
				} });
				
	        connection.connect();
	        
	        if( connection.isConnected() )
	        	connection.scanFile(file.toString(),  null);
	             
	            
	    } catch (IOException e) {
	        // Unable to create file, likely because external storage is
	        // not currently mounted.
	        Log.w("ExternalStorage", "Error writing " + file, e);
	    }
	}
	
}
