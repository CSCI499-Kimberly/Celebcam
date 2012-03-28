package com.celebcam;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.celebcam.celebcamapi.CelebcamApi;
import com.celebcam.celebcamapi.DownloadFileTask;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

public final class CelebCamLibrary {
	
	static CelebCamLibrary mLibrary;
	
	List<Bitmap>     mBitmaps;
	SQLiteDatabase   mDatabase;
	CelebCamDbHelper mDbHelper;
	

	private JSONObject  data;
	private JSONArray   items;
	private CelebcamApi celebcamApi;

	public void getCutouts( ){
		celebcamApi.getCutouts(this);
	}
	
	public void getCutoutsLoaded(JSONObject jsonObject) throws JSONException{
		Log.d("CelebcamApi", "getCutouts Finished");
		this.data = jsonObject;
		this.items = this.data.getJSONArray("items");
		
		this.layoutItems();
	}

	public void layoutItems() throws JSONException{
		Log.d("CelebcamApi", "layoutItems");
		
		Log.d("CelebcamApi", "Item 0: " + this.items.getJSONObject(0));
		
		for( int i = 0; i < items.length() ; i++ )
		{
			JSONObject item = items.getJSONObject(i);
			String imageLink = item.getString("image_link");
			Log.d("CelebcamApi", "image_link: " + imageLink);
			
			this.downloadImage(imageLink);
		}
		
		
	}
	
	public void downloadImage(String imageLink){
		Log.d("CelebcamApi", "downloadImage");
		Log.d("CelebcamApi", "imageLink: " + imageLink);
		
		DownloadFileTask downloadFileTask = new DownloadFileTask(this, "downloadImageLoaded", imageLink);
		downloadFileTask.execute();
		
	}
	
	public void downloadImageLoaded(DownloadFileTask downloadFileTask){
		Log.d("CelebcamApi", "downloadImageLoaded");
	}

	class BitmapList
	{
		class Node
		{
			Bitmap bitmap;
			Node   prev;
			Node   next;
			
			public Node()
			{
				
			}
			
			public Node( Bitmap bitmap )
			{
				this.bitmap = bitmap;
			}
			
			public Node( Bitmap bitmap, Node next, Node prev )
			{
				this.bitmap = bitmap;
				this.next = next;
				this.prev = prev;
				prev.next = this;
			}
		}
		
		Node mFirstNode;
		Node mLastNode;
		Node mCurrentNode;
		int  mSize;
		
		public BitmapList()
		{
			
		}
		
		public void addBitmap( Bitmap bitmap )
		{
			mSize++;
			
			if( mFirstNode == null )
			{
				mFirstNode = new Node( bitmap );

				mFirstNode.prev = mFirstNode;
				mFirstNode.next = mFirstNode;
				
				mCurrentNode = mFirstNode;
				mLastNode    = mFirstNode;
			}
			else
			{
				Node tmp= new Node( bitmap );	
				
				tmp.next = mFirstNode;
				tmp.prev = mLastNode;
				mLastNode.next = tmp;
				mLastNode      = tmp;
				mFirstNode.prev = mLastNode;
			}
		}
		
		public Bitmap getCurrentBitmap()
		{
			return mCurrentNode.bitmap;
		}
		
		public void next()
		{
			if(mCurrentNode != null)
			{
				mCurrentNode = mCurrentNode.next;
			}
		}
		
		public void prev()
		{
			if(mCurrentNode != null)
			{
				mCurrentNode = mCurrentNode.prev;
			}
		}
		
		public int getSize()
		{
			return mSize;
		}
	}
	
	private BitmapList mList;
	private RectF	   mCelebRectF;
	
	private CelebCamLibrary()
	{
		mList = new BitmapList();
		mCelebRectF = new RectF();
		
		celebcamApi = new CelebcamApi();
		this.getCutouts();
	}

	static void createLibrary()
	{
		if( mLibrary == null )
		{
			mLibrary = new CelebCamLibrary();
		}
	}
	
	public static CelebCamLibrary getLibrary()
	{
		if( mLibrary == null )
		{
			mLibrary = new CelebCamLibrary();
		}
		
		return mLibrary;
	}

	public static void addToLibrary(Bitmap bitmap)
	{
		mLibrary.mList.addBitmap(bitmap);
	}
	
	public void addToLibrary(byte[] imageData)
	{
		if( imageData != null )
			mLibrary.mList.addBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
		else
			Log.d("CelebCamLibrary", "imageData is null.");
	}
	
	public void addToLibrary(byte[] imageData, String...imageInformation)
	{
		mList.addBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
	}
	
	public void startCaching()
	{
		
	}
	
	public void next()
	{
		mList.next();
	}
	
	public void prev()
	{
		mList.prev();
	}
	
	public Bitmap getImage()
	{
		Bitmap bitmap = null;
		
		if( mList.getSize() > 0 )
			bitmap = mList.getCurrentBitmap();
		
		if( bitmap == null )
			bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
		
		mCelebRectF = new RectF( 0, 0, bitmap.getWidth(), bitmap.getHeight() );
		
		return bitmap;
	}
	
	public boolean doTouchEvent(float x, float y)
	{
		if( mCelebRectF.contains( x, y))
			return true;
		
		return false;
	}
}
