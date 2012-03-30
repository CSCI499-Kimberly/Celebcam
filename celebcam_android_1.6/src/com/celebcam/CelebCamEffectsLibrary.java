package com.celebcam;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.os.AsyncTask;

class Size
{
	int width;
	int height;
	
	public Size() {}
	
	public Size( int width, int height)
	{
		this.width  = width;
		this.height = height;
	}
}

class Ratio
{
	float width;
	float height;
	
	Ratio( float width, float height )
	{
		this.width  = width;
		this.height = height;
	}
}

class Channel
{
	static byte RED_INDEX   = 0;
	static byte GREEN_INDEX = 1;
	static byte BLUE_INDEX  = 2;
	
	static int RED   = 0x00ff0000;
	static int GREEN = 0x0000ff00;
	static int BLUE  = 0x000000ff;
	
	static int getChannel( byte index )
	{
		int channel = RED;
		
		if( index == GREEN_INDEX )
			channel = GREEN;
		else if( index == BLUE_INDEX )
			channel = BLUE;
		
		return channel;
	}
}

class CelebCamBitmap implements CCMemoryWatcher {

	public int getSizeInBytes() {
		int m = 0;
		if( mMergedBitmap != null )
			m = 4*mMergedBitmap.getHeight()*mMergedBitmap.getWidth();
		
		return (width*height*3*2)+(1*width*height)+ m;
	}


	public int getStaticBytes() {

		return 0;
	}
	
	boolean del;
	
	public boolean delete()
	{
		return del;
	}
	
	int width;
	int height;
	
	byte[] alpha;
    short[] red;
	short[] green;
	short[] blue;
	
	float[] colorNotes = new float[]{1,1,1,1};
	
	Bitmap mMergedBitmap;
	
	public CelebCamBitmap( int size )
	{
		alpha = new byte[size];
		red	  = new short[size];
		green = new short[size];
		blue  = new short[size];
		
		colorNotes = new float[3];
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public CelebCamBitmap(int width, int height )
	{
		this.width  = width;
		this.height = height;
		
		int size = width*height;
		
		alpha = new byte[size];
		red	  = new short[size];
		green = new short[size];
		blue  = new short[size];
		
		colorNotes = new float[3];
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public CelebCamBitmap( Bitmap bitmap )
	{
		this( bitmap.getWidth()*bitmap.getHeight());
		
		width = bitmap.getWidth();
		height= bitmap.getHeight();
	
		int pixel;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				pixel = bitmap.getPixel(i, j);
				this.alpha[(i*height) + j] = (byte) ((pixel & 0xff000000) >> 24);
				this.red[(i*height) + j]   = (short) ((pixel & 0x00ff0000) >> 16);
				this.green[(i*height) + j] = (short) ((pixel & 0x0000ff00) >> 8);
				this.blue[(i*height) + j]  = (short) ((pixel & 0x000000ff));
			}
		}
		
		CCDebug.registerMemoryWatcher( this );
	}
	
	public void release()
	{
		width = 0;
		height = 0;
		alpha = null;
		red   = null;
		green = null;
		blue  = null;
		
		if( mMergedBitmap != null )
		{
			mMergedBitmap.recycle();
			mMergedBitmap = null;
		}

		CCDebug.unRegister( this );
	}
	
	public float[] getColorNotes()
	{
		return colorNotes;
	}
	
	public Bitmap toAndroidBitmap()
	{

		if( mMergedBitmap == null )
		{
			mMergedBitmap = Bitmap.createBitmap( width, height , Bitmap.Config.ARGB_8888);

			int pixel;
			
			for( int i = 0; i < width; i++ )
			{
				for( int j = 0; j < height; j++ )
				{
					pixel =
						( ((int)alpha[(i*height) + j]) << 24) |
						( ((int)red[(i*height) + j])   << 16) |
						( ((int)green[(i*height) + j])  << 8) |
						( blue[(i*height) + j]);
					
					mMergedBitmap.setPixel(i, j, pixel);
				}
			}
		}
		
		return mMergedBitmap;
	}
	
	
	public void permanentColorOverlay( int color )
	{
		int r = (0x00ff0000 & color) >> 16;
		int g = (0x0000ff00 & color) >> 8;
		int b = 0x000000ff & color;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				if( alpha[i*height + j ] != 0 )
				{
					Log.d("CelebcamBitmap", "Changing Color");
					red[(i*height) + j]   = (short)r;
					green[(i*height) + j] = (short)g;
					blue[(i*height) + j]  = (short)b;
				
				}
			}
		}	
	}
	
	public void permanentColorBurn( int color )
	{
		int r = (0x00ff0000 & color) >> 16;
		int g = (0x0000ff00 & color) >> 8;
		int b = 0x000000ff & color;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				if( alpha[i*height + j ] != 0 )
				{
					Log.d("CelebcamBitmap", "Changing Color");
					red[(i*height) + j]   *= (short)r;
					green[(i*height) + j] *= (short)g;
					blue[(i*height) + j]  *= (short)b;
				
				}
			}
		}	
	}


	public int getWidth() {

		return width;
	}


	public int getHeight() {
		
		return height;
	}
}

public final class CelebCamEffectsLibrary implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return 0;
	}


	public int getStaticBytes() {

		return 4;
	}
	
	enum ReturnType { SUCCEEDED, FAILED, NOT_IMPLEMENTED, PARTIAL_IMPLEMENTATION };
	
	static Canvas mCanvas;
	static Canvas mPublishCanvas;
	
	static Bitmap mPublishBitmap;
	static Bitmap mPreviewBitmap;
	static Bitmap mCurrentBitmap;
	static Bitmap mMergedBitmap;
	
	static byte mState;
	
	static Size mPublishSize;
	static Size mPreviewSize;
	static Size mCurrentSize;
	
	static CelebCamBitmap mCCBitmap;
	
	static byte COMPOSE         = 0;
	static byte TEXT            = 1;
	static byte BORDER          = 2;
	static byte SPARKLES        = 3;
	static byte BLACK_AND_WHITE = 4;
	static byte SEPIA           = 5;
	static byte ADJUST_COLOR    = 6;
	
	static byte PREVIEW  = 0;
	static byte PUBLISH  = 1;
	
	static float[] mColorNotes = new float[]{1,1,1,1};
	
	private static boolean[] EFFECTS = new boolean[6];
	
	static void process()
	{
		if( EFFECTS[ COMPOSE ] == true )
		{
			
		}
		
		if( EFFECTS[ BLACK_AND_WHITE ] == true )
		{
			
		}else if( EFFECTS[ SEPIA ] == true )
		{
			
		}
		
		if( EFFECTS[ TEXT ] == true )
		{
			
		}
		
		if( EFFECTS[ SPARKLES ] == true )
		{
			
		}
		
		if( EFFECTS[ BORDER ] == true )
		{
			
		}		
		
	}
	
	static Ratio publishToPreviewRatio()
	{
		return (new Ratio((float)mPublishSize.width/mPreviewSize.width, (float)mPublishSize.height/mPreviewSize.height) );
	}
	
	static void setState( byte state )
	{
		mState = state;

	}
	
	
	static void setState2( byte state )
	{
		mState = state;
		
		if( mState == PREVIEW )
		{
			mCurrentBitmap = mPreviewBitmap;
			mCurrentSize   = mPreviewSize;
		}
		else if ( mState == PUBLISH )
		{
			mCurrentBitmap = mPublishBitmap;
			mCurrentSize   = mPublishSize;
		}
		
		mCanvas = new Canvas();
		mCanvas.setBitmap( mCurrentBitmap );

	}
	
	static void turnOn( byte effect )
	{
		
		EFFECTS[ effect ] = true;
	}
	
//	static Bitmap getCurrentBitmap()
//	{
//		return mCurrentBitmap;
//	}
	static void turnOff( byte effect )
	{
		EFFECTS[ effect ] = false;
	}
	
	static void setCanvas( Canvas canvas )
	{
		mCanvas = canvas;
	}
	
	static void setPreviewBitmap( Bitmap bitmap )
	{
		if( mPreviewSize == null )
			Log.d("DataAcquisitionActivity", "mPreviewSize null ");
		
		mPreviewBitmap = Bitmap.createScaledBitmap(bitmap, mPreviewSize.width, mPreviewSize.height, false);
		
		if( mCanvas == null )
			mCanvas = new Canvas();
		
		mCanvas.setBitmap(mPreviewBitmap);
	}
	
	
	
	static void setPublishBitmap( Bitmap bitmap )
	{
		if( mPublishSize == null )
			Log.d("DataAcquisitionActivity", "mPreviewSize null ");
		
		mPublishBitmap =  Bitmap.createScaledBitmap(bitmap, mPublishSize.width, mPublishSize.height, false);
		
		
		if( mCanvas == null )
			mCanvas = new Canvas();
		
		mCanvas.setBitmap(bitmap);
	}
	
	static CelebCamBitmap getCCBitmap()
	{
		return mCCBitmap;
	}

	static CelebCamBitmap convertToCCBitmap( Bitmap bitmap)
	{
		Log.d("DataAcquisitionActivity", " : slip started");
		

		CelebCamBitmap ccBitmap = new CelebCamBitmap( bitmap.getWidth()*bitmap.getHeight());
		
		int width = bitmap.getWidth();
		int height= bitmap.getHeight();
		
		int pixel;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				pixel = bitmap.getPixel(i, j);
				ccBitmap.alpha[(i*height) + j] = (byte) ((pixel & 0xff000000) >> 24);
				ccBitmap.red[(i*height) + j]   = (short) ((pixel & 0x00ff0000) >> 16);
				ccBitmap.green[(i*height) + j] = (short) ((pixel & 0x0000ff00) >> 8);
				ccBitmap.blue[(i*height) + j]  = (short) ((pixel & 0x000000ff));
			}
		}
		
		Log.d("DataAcquisitionActivity", "slip finished");
		
		return ccBitmap;
	}
	
	static void release()
	{
		if( mCCBitmap != null )
		{
		
			//CelebCamApplication.getApplication().store(mCCBitmap.alpha, "ALPHA");
			//CelebCamApplication.getApplication().store(mCCBitmap.red, "RED");
			//CelebCamApplication.getApplication().store(mCCBitmap.green, "GREEN");
			//CelebCamApplication.getApplication().store(mCCBitmap.blue, "BLUE");
			
			mCCBitmap.release();
			
			mCCBitmap = null;
		}

		mCanvas = null;
		
		if( mPreviewBitmap != null )
		{
		//	mPreviewBitmap.recycle();
			mPreviewBitmap = null;
		}
		
		if( mCurrentBitmap != null )
		{
//			mCurrentBitmap.recycle();
			mCurrentBitmap = null;
		}
		
		Runtime.getRuntime().gc();
	}
	
	static void slipChannels()
	{
		Log.d("DataAcquisitionActivity", " : slip started");
		
		if( mCCBitmap == null )
			mCCBitmap = new CelebCamBitmap( mCurrentBitmap.getWidth(),mCurrentBitmap.getHeight());
		
		int width = mCurrentBitmap.getWidth();
		int height= mCurrentBitmap.getHeight();
		
		int pixel;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				pixel = mCurrentBitmap.getPixel(i, j);
				mCCBitmap.alpha[(i*height) + j] = (byte) ((pixel & 0xff000000) >> 24);
				mCCBitmap.red[(i*height) + j]   = (short) ((pixel & 0x00ff0000) >> 16);
				mCCBitmap.green[(i*height) + j] = (short) ((pixel & 0x0000ff00) >> 8);
				mCCBitmap.blue[(i*height) + j]  = (short) ((pixel & 0x000000ff));
			}
		}
		
		mCurrentBitmap.recycle();
		mPreviewBitmap.recycle();
		
		mCurrentBitmap = null;
		mPreviewBitmap = null;
		
		Runtime.getRuntime().gc();
		
		Log.d("DataAcquisitionActivity", "slip finished");
	}
	
	static Bitmap mergeChannels()
	{
		Log.d("DataAcquisitionActivity", "merge started");
		
		if( mCCBitmap == null )
			return mCurrentBitmap;
		
		if( mMergedBitmap == null )
			mMergedBitmap = Bitmap.createBitmap( mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight(), mCurrentBitmap.getConfig());
		
		int width = mMergedBitmap.getWidth();
		int height= mMergedBitmap.getHeight();
		
		int pixel;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				pixel =
					( ((int)mCCBitmap.alpha[(i*height) + j]) << 24) |
					( ((int)mCCBitmap.red[(i*height) + j])   << 16) |
					( ((int)mCCBitmap.green[(i*height) + j])  << 8) |
					( mCCBitmap.blue[(i*height) + j]);
				
				mMergedBitmap.setPixel(i, j, pixel);
			}
		}		
		
		Log.d("DataAcquisitionActivity", "merge finished");
		
		return mMergedBitmap;
	}
	
	static Bitmap mergeChannels( int[] excludedChannels )
	{
		Log.d("DataAcquisitionActivity", "merge started");
		
		if( mCCBitmap == null )
			return mCurrentBitmap;
		
		if( mMergedBitmap == null )
			mMergedBitmap = Bitmap.createBitmap( mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight(), mCurrentBitmap.getConfig());
		
		int width = mMergedBitmap.getWidth();
		int height= mMergedBitmap.getHeight();
		
		int pixel;
		
		int[] exclusion = new int[] { 1,1,1};
		
		for( int i = 0; i < excludedChannels.length; i++)
		{
			if( excludedChannels[i] == Channel.RED )
				exclusion[0] = 0;
			else if ( excludedChannels[i] == Channel.GREEN)
				exclusion[1] = 0;
			else if ( excludedChannels[i] == Channel.BLUE)
				exclusion[2] = 0;
		}
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
				pixel =
					( ((int)mCCBitmap.alpha[(i*height) + j]) << 24) |
					
					(exclusion[0]) *  ( ((int)mCCBitmap.red[(i*height) + j])   << 16) |
					(exclusion[1]) *  ( ((int)mCCBitmap.green[(i*height) + j])  << 8) |
					(exclusion[2]) *  (       mCCBitmap.blue[(i*height) + j]);
				
				mMergedBitmap.setPixel(i, j, pixel);
			}
		}		
		
		Log.d("DataAcquisitionActivity", "merge finished");
		return mMergedBitmap;
	}
	
	static ReturnType addText( CelebCamTextView text )
	{
		if( mState == PREVIEW )
		{
			text.onDraw(mCanvas);
		}
		else if( mState == PUBLISH )
		{
			text.publish( mCanvas, mPublishSize, mPreviewSize );
		}
		
		return ReturnType.SUCCEEDED;
	}
	
	static ReturnType addBorder( CelebCamBorderView border )
	{
		if( mState == PREVIEW )
		{
			border.onDraw(mCanvas);
		}
		else if( mState == PUBLISH )
		{
			border.publish(mCanvas, mPublishSize, mPreviewSize );
		}
		
		return ReturnType.PARTIAL_IMPLEMENTATION;
	}
	
	static ReturnType addSparkles( CelebCamSparklesView sparkles  )
	{
		if( mState == PREVIEW )
		{
			sparkles.onDraw(mCanvas);
		}
		else if( mState == PUBLISH )
		{
			sparkles.publish(mCanvas);
		}
		
		return ReturnType.PARTIAL_IMPLEMENTATION;
	}
	
	static public void composeImage( Bitmap lowerBitmap, Bitmap upperBitmap)
	{
		mCanvas.drawBitmap( lowerBitmap, 0, 0, null );
		mCanvas.drawBitmap( upperBitmap, 0, 0, null );
		
	}
	
	static public void composeImage( Bitmap lowerBitmap, Matrix lowerBitmapMatrix, Bitmap upperBitmap, Matrix upperBitmapMatrix)
	{
		if( lowerBitmapMatrix == null )
			lowerBitmapMatrix = new Matrix();
		
		if( upperBitmapMatrix == null )
			upperBitmapMatrix = new Matrix();

		if( mState == PREVIEW )
		{
			lowerBitmap = Bitmap.createScaledBitmap(lowerBitmap, mPreviewSize.width, mPreviewSize.height, false);
		}
		else if( mState == PUBLISH )
		{
			upperBitmapMatrix.postScale((float)mPublishSize.width/mPreviewSize.width, (float)mPublishSize.height/mPreviewSize.height );
		}
		
		mCanvas.drawBitmap( lowerBitmap, lowerBitmapMatrix, null );
		mCanvas.drawBitmap( upperBitmap, upperBitmapMatrix, null );

	}
	
	static public void addImage( Bitmap bitmap, Matrix upperBitmapMatrix)
	{
		
		if( upperBitmapMatrix == null )
			upperBitmapMatrix = new Matrix();

		if( mState == PUBLISH )
		{
			upperBitmapMatrix.postScale((float)mPublishSize.width/mPreviewSize.width, (float)mPublishSize.height/mPreviewSize.height );
		}

		mCanvas.drawBitmap( bitmap, upperBitmapMatrix, null );
	}
	
	static public Bitmap negative( CelebCamBitmap ccBitmap )
	{
		Bitmap bitmap = ccBitmap.toAndroidBitmap();
		
		for( int i = 0; i < bitmap.getWidth(); i++ )
		{
			for(int j = 0; j < bitmap.getHeight(); j++ )
			{
				bitmap.setPixel(i, j, 0xffffffff & (1 - ( 0x00ffffff & bitmap.getPixel(i, j) )));
			}
		}
		
		return bitmap;
	}
	
	static public void negative()
	{
		for( int i = 0; i < mCurrentBitmap.getWidth(); i++ )
		{
			for(int j = 0; j < mCurrentBitmap.getHeight(); j++ )
			{
				mCurrentBitmap.setPixel(i, j, 0xffffffff & (1 - ( 0x00ffffff & mCurrentBitmap.getPixel(i, j) )));
			}
		}
	}

	static public Bitmap blackAndWhite( Bitmap bitmap )
	{

		Bitmap bitmap2 = Bitmap.createBitmap( bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		
		int pixel;

		int newPixel;
		
		for( int i = 0; i < bitmap.getWidth(); i++ )
		{
			for( int j = 0; j < bitmap.getHeight(); j++ )
			{
				pixel = ( 0x000000ff & bitmap.getPixel(i, j) );
				//intensity =  ( (int) Math.ceil( ( scaleFactor* Math.sqrt( pixel ) ) )  ) ;
				newPixel = ( 0xff000000  | (pixel << 16 ) | (pixel << 8 ) | ( pixel  ));
		
				Log.d( "PIXEL :: ", Integer.toHexString( newPixel ) );
				bitmap2.setPixel( i, j,
						         newPixel //( 0xff000000 | ( (int) Math.ceil( ( scaleFactor* Math.sqrt( pixel ) ) )  ) )
						       ) ;
			}
		}
				
		return bitmap2;
	}

	static public Bitmap removeChannel( int channel )
	{
		int pixel;
		
		int mask = ( 0xffffffff ^ channel ) ;
		
		Bitmap tmp = Bitmap.createBitmap(mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight(), mCurrentBitmap.getConfig());
		
		for(int i = 0; i < mCurrentBitmap.getWidth(); i ++ )
		{
			for( int j = 0; j < mCurrentBitmap.getHeight(); j++ )
			{
				pixel = mCurrentBitmap.getPixel(i, j);
				pixel = ( mask & pixel);

				tmp.setPixel(i, j, pixel);
			}
		}
		
		return tmp;
	}
	
	static public Bitmap getCurrentBitmap()
	{
		return mCCBitmap.toAndroidBitmap();
	}
	
		static public Bitmap getCurrentBitmap2()
	{
		return mCurrentBitmap;
	}

	static Bitmap adjustChannels( float...amounts )
	{
		Log.d("DataAcquisitionActivity", "adjust starting");
		
		mColorNotes = amounts;
		
		if( mCCBitmap == null )
		{
			return mCurrentBitmap;
		}
		
		if( mMergedBitmap == null )
			mMergedBitmap = Bitmap.createBitmap( mCCBitmap.getWidth(), mCCBitmap.getHeight(), Bitmap.Config.ARGB_8888);

		
		int width = mMergedBitmap.getWidth();
		int height= mMergedBitmap.getHeight();
		
		int pixel;
		
		float[] exclusion = new float[] {1,1,1,1};
		
		for( int i = 0; i < amounts.length && i < exclusion.length; i++ )
		{
			exclusion[i] = amounts[i];
		}

		int a,r,g,b;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
					a = ( ((int)mCCBitmap.alpha[(i*height) + j]) << 24);
					
					if ( exclusion[0] * mCCBitmap.red[(i*height) + j]  > 0xFF )
					{
						r = 0x00FF0000;
					}
					else
					{
						r = ((int)(   (exclusion[0] * mCCBitmap.red[(i*height) + j]  ) )  << 16);
					}
					
					if( exclusion[1] * mCCBitmap.green[(i*height) + j] > 0xFF ) 
					{
						g = 0x0000FF00;
					}
					else
					{
						g = ((int)(   (exclusion[1] * mCCBitmap.green[(i*height) + j]) )  << 8);
					}
				     
					if( exclusion[2] * mCCBitmap.blue[(i*height) + j] > 0xFF )
					{
						b = 0x000000FF;
					}
					else
					{
						b = (int)(    exclusion[2] * mCCBitmap.blue[(i*height) + j]);
					}
				
					pixel = a | r | g | b ;
					
				mMergedBitmap.setPixel(i, j, pixel);
			}
			
		}	
		
		return mMergedBitmap;
	}
	
	static Bitmap adjustChannels2( float...amounts )
	{
		Log.d("DataAcquisitionActivity", "adjust starting");
		
		mColorNotes = amounts;
		
		if( mCCBitmap == null )
		{
			return mCurrentBitmap;
		}
		
		if( mMergedBitmap == null)
			mMergedBitmap = Bitmap.createBitmap(mCCBitmap.getWidth(), mCCBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		int width = mCCBitmap.getWidth();
		
		int height= mCCBitmap.getHeight();
		
		int pixel;
		
		float[] exclusion = new float[] {1,1,1,1};
		
		for( int i = 0; i < amounts.length && i < exclusion.length; i++ )
		{
			exclusion[i] = amounts[i];
		}

		int a,r,g,b;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
					a = ( ((int)mCCBitmap.alpha[(i*height) + j]) << 24);
					
					if ( exclusion[0] * mCCBitmap.red[(i*height) + j]  > 0xFF )
					{
						r = 0x00FF0000;
					}
					else
					{
						r = ((int)(   (exclusion[0] * mCCBitmap.red[(i*height) + j]  ) )  << 16);
					}
					
					if( exclusion[1] * mCCBitmap.green[(i*height) + j] > 0xFF ) 
					{
						g = 0x0000FF00;
					}
					else
					{
						g = ((int)(   (exclusion[1] * mCCBitmap.green[(i*height) + j]) )  << 8);
					}
				     
					if( exclusion[2] * mCCBitmap.blue[(i*height) + j] > 0xFF )
					{
						b = 0x000000FF;
					}
					else
					{
						b = (int)(    exclusion[2] * mCCBitmap.blue[(i*height) + j]);
					}
				
					pixel = a | r | g | b ;
					
				mMergedBitmap.setPixel(i, j, pixel);
			}
			
		}	
		
		return mMergedBitmap;
	}
	
	static Bitmap applyColorNotes( Bitmap bitmap )
	{
		Log.d("DataAcquisitionActivity", "adjust starting");
		

		CelebCamBitmap ccBitmap = convertToCCBitmap( bitmap );
		
		int width = bitmap.getWidth();
		int height= bitmap.getHeight();
		
		int pixel;
		
		float[] exclusion = new float[] {1,1,1,1};
		
		for( int i = 0; i < mColorNotes.length && i < exclusion.length; i++ )
		{
			exclusion[i] = mColorNotes[i];
		}

		int a,r,g,b;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
					a = ( ((int)ccBitmap.alpha[(i*height) + j]) << 24);
					
					if ( exclusion[0] * ccBitmap.red[(i*height) + j]  > 0xFF )
					{
						r = 0x00FF0000;
					}
					else
					{
						r = ((int)(   (exclusion[0] * ccBitmap.red[(i*height) + j]  ) )  << 16);
					}
					
					if( exclusion[1] * ccBitmap.green[(i*height) + j] > 0xFF ) 
					{
						g = 0x0000FF00;
					}
					else
					{
						g = ((int)(   (exclusion[1] * ccBitmap.green[(i*height) + j]) )  << 8);
					}
				     
					if( exclusion[2] * ccBitmap.blue[(i*height) + j] > 0xFF )
					{
						b = 0x000000FF;
					}
					else
					{
						b = (int)(    exclusion[2] * ccBitmap.blue[(i*height) + j]);
					}
				
					pixel = a | r | g | b ;
					
				bitmap.setPixel(i, j, pixel);
			}
			
		}	
		
		return bitmap;
	}
	
	static Bitmap adjustChannel2( int channel, float amount )
	{
		Log.d("DataAcquisitionActivity", "adjust starting");
		if( mCCBitmap == null )
		{
			Log.d("DataAcquisitionActivity", "mCCBitmap is null");
			return mCurrentBitmap;
		}
		
		if( mMergedBitmap == null )
			mMergedBitmap = Bitmap.createBitmap( mCurrentBitmap.getWidth(), mCurrentBitmap.getHeight(), mCurrentBitmap.getConfig());

		int width = mMergedBitmap.getWidth();
		int height= mMergedBitmap.getHeight();
		
		int pixel;
		
		float[] exclusion = new float[] { 1,1,1};
		
		if( channel == Channel.RED )
			exclusion[0] = amount;
		else if ( channel == Channel.GREEN)
			exclusion[1] = amount;
		else if ( channel == Channel.BLUE)
			exclusion[2] = amount;

		int a,r,g,b;
		
		for( int i = 0; i < width; i++ )
		{
			for( int j = 0; j < height; j++ )
			{
//				pixel =
//					( ((int)mCCBitmap.alpha[(i*height) + j]) << 24) |
//					
//					((int)(   (exclusion[0] * mCCBitmap.red[(i*height) + j]  ) )  << 16) |
//					((int)(   (exclusion[1] * mCCBitmap.green[(i*height) + j]) )  << 8)  |
//				     (int)(    exclusion[2] * mCCBitmap.blue[(i*height) + j]);
				
				
					a = ( ((int)mCCBitmap.alpha[(i*height) + j]) << 24);
					
					if ( exclusion[0] * mCCBitmap.red[(i*height) + j]  > 0xFF )
					{
						r = 0x00FF0000;
					}
					else
					{
						r = ((int)(   (exclusion[0] * mCCBitmap.red[(i*height) + j]  ) )  << 16);
					}
					
					if( exclusion[1] * mCCBitmap.green[(i*height) + j] > 0xFF ) 
					{
						g = 0x0000FF00;
					}
					else
					{
						g = ((int)(   (exclusion[1] * mCCBitmap.green[(i*height) + j]) )  << 8);
					}
				     
					if( exclusion[2] * mCCBitmap.blue[(i*height) + j] > 0xFF )
					{
						b = 0x000000FF;
					}
					else
					{
						b = (int)(    exclusion[2] * mCCBitmap.blue[(i*height) + j]);
					}
				
					pixel = a | r | g | b ;
					
				mMergedBitmap.setPixel(i, j, pixel);
			}
		}		
		
		Log.d("DataAcquisitionActivity", "adjust finished");
		
		return mMergedBitmap;
	}


	static Bitmap adjustChannel( int channel, float amount )
	{
		Log.d("DataAcquisitionActivity", "adjust starting");
		if( mCCBitmap == null )
		{
			Log.d("DataAcquisitionActivity", "mCCBitmap is null");
			return mCurrentBitmap;
		}
		
		if( mMergedBitmap == null )
			mMergedBitmap = Bitmap.createBitmap(mCCBitmap.getWidth(), mCCBitmap.getHeight(), Bitmap.Config.ARGB_8888);

		int width = mMergedBitmap.getWidth();
		int height= mMergedBitmap.getHeight();
		
		int pixel;
		
		float[] exclusion = new float[] { 1,1,1};
		
		if( channel == Channel.RED )
			exclusion[0] = amount;
		else if ( channel == Channel.GREEN)
			exclusion[1] = amount;
		else if ( channel == Channel.BLUE)
			exclusion[2] = amount;

		int a,r,g,b;
		
		a = 0xFF000000;
		
		if( channel == Channel.RED )
		{
			for( int i = 0; i < width; i++ )
			{
				for( int j = 0; j < height; j++ )
				{
					mCCBitmap.red[(i*height) + j] *= exclusion[0];
						
					if ( mCCBitmap.red[(i*height) + j]  > 0xFF )
					{
						r = 0x00FF0000;
					}
					else
					{
						r = ((int)(   ( mCCBitmap.red[(i*height) + j]  ) )  << 16);
					}
					
							g = ((int)( mCCBitmap.green[(i*height) + j])  << 8);
					

							b = (int)( exclusion[2] * mCCBitmap.blue[(i*height) + j]);
						
					
						pixel = a | r | g | b ;
						
					mMergedBitmap.setPixel(i, j, pixel);
				}
			}		
		}
		else if( channel == Channel.GREEN )
		{
			for( int i = 0; i < width; i++ )
			{
				for( int j = 0; j < height; j++ )
				{
						
							r = ((int)(   ( mCCBitmap.red[(i*height) + j]  ) )  << 16);
	
							if( exclusion[1] * mCCBitmap.green[(i*height) + j] > 0xFF ) 
							{
								g = 0x0000FF00;
							}
							else
							{
								mCCBitmap.green[(i*height) + j] *= exclusion[1];
								g = (int)(mCCBitmap.green[(i*height) + j]  << 8);
							}
					

							b = (int)( exclusion[2] * mCCBitmap.blue[(i*height) + j]);
					
					
						pixel = a | r | g | b ;
						
					mMergedBitmap.setPixel(i, j, pixel);
				}
			}		
		}
		else if( channel == Channel.BLUE )
		{
			for( int i = 0; i < width; i++ )
			{
				for( int j = 0; j < height; j++ )
				{
						
							r = ((int)(   ( mCCBitmap.red[(i*height) + j]  ) )  << 16);
	
							g = ((int)( mCCBitmap.green[(i*height) + j])  << 8);
					
					     
						if( exclusion[2] * mCCBitmap.blue[(i*height) + j] > 0xFF )
						{
							b = 0x000000FF;
						}
						else
						{
							b = (int)( exclusion[2] * mCCBitmap.blue[(i*height) + j]);
						}
					
						pixel = a | r | g | b ;
						
					mMergedBitmap.setPixel(i, j, pixel);
				}
			}		
		}
		Log.d("DataAcquisitionActivity", "adjust finished");
		
		return mMergedBitmap;
	}

	static void setPreviewSize( int width, int height )
	{
		if( mPreviewSize == null )
			mPreviewSize = new Size();
		
		mPreviewSize.width  = width;
		mPreviewSize.height = height;
	}
	
	static void setPublishSize( int width, int height )
	{
		if( mPublishSize == null )
			mPublishSize = new Size();
		
		mPublishSize.width  = width;
		mPublishSize.height = height;
	}
}

class Queue
{
	class QueueNode
	{
		CelebCamEffectsProcessor processor;
		QueueNode next;
		
		public QueueNode( CelebCamEffectsProcessor processor )
		{
			this.processor = processor;
		}
	}
	
	QueueNode mHead;
	QueueNode mTail;
	
	public void enQueue( CelebCamEffectsProcessor processor)
	{
		if( mHead == null )
		{
			mHead = new QueueNode( processor );
			mTail = mHead;
		}
		else
		{
			mTail.next = new QueueNode( processor );
			mTail = mTail.next;
		}
	}
	
	public CelebCamEffectsProcessor deQueue()
	{
		if( mHead == null )
			return null;
		
		CelebCamEffectsProcessor tmp = mHead.processor;
		
		if( mTail == mHead )
			mTail = mHead.next;
			
		mHead = mHead.next;
		
		return tmp;
	}
	
	public boolean notHead( CelebCamEffectsProcessor processor )
	{
		if( mHead.processor.id == processor.id )
			return false;
		
		return true;
	}
}

class CelebCamEffectsProcessor extends AsyncTask<CelebCamBitmap, Integer, Bitmap>{
	
	private static CelebCamEditView mCelebCamEditView;
	private static int mIdCounter = -1;
	
	public int   id;
	private byte  mType;
	private int   mChannel;
	private float mAmount;
	private float[] mAmounts;
	
	static Queue waitQueue = new Queue();
	
	
	CelebCamEffectsProcessor()
	{
		super();
	}
	
	CelebCamEffectsProcessor(CelebCamEditView viewToPostUpdates )
	{
		super();
		


		mCelebCamEditView = viewToPostUpdates;
	}
	
	CelebCamEffectsProcessor(CelebCamEditView viewToPostUpdates, byte type, int channel, float amount )
	{
		super();

		mIdCounter++;
		
		id = mIdCounter;
		
		waitQueue.enQueue( this );
				
		mCelebCamEditView = viewToPostUpdates;
		mType     = type;
		mChannel  = channel;
		mAmount   = amount;
		
	}
	
	
	CelebCamEffectsProcessor(CelebCamEditView viewToPostUpdates, byte type, float[] amounts )
	{
		super();

		mIdCounter++;
		
		id = mIdCounter;
		
		waitQueue.enQueue( this );
				
		mCelebCamEditView = viewToPostUpdates;
		mType     = type;
		mAmounts   = amounts;
		
	}
	
	void setup()
	{
		
		while( waitQueue.notHead( this ) )
		{
			
		}		
	}
	public void setCelebCamEditView( CelebCamEditView view )
	{
		mCelebCamEditView = view;
	}
	
	public CelebCamEditView getCelebCamEditView()
	{
		return mCelebCamEditView;
	}
	
	public Bitmap doInBackground(CelebCamBitmap... bitmap )
	{
		//mEditView.setVisibility( View.GONE );
		setup();
		Bitmap processedBitmap = null;
		
		if( mType == CelebCamEffectsLibrary.ADJUST_COLOR )
		{
			Log.d("DataAcquisitionActivity", "working, id : " + Integer.toString(id));
			processedBitmap = CelebCamEffectsLibrary.adjustChannel(mChannel, mAmount);
			//CelebCamEffectsLibrary.removeColorChannel(mChannel);
			//processedBitmap = CelebCamEffectsLibrary.getBitmap();
		}
		
		if( processedBitmap == null )
			Log.d("DataAcquisitionActivity", "processedBitmap is null");
		
		return processedBitmap;
	}
	
	protected void onProgressUpdate(Integer... values)
	{
		super.onProgressUpdate(values);
	}
	
	protected void onPostExecute(Bitmap bitmap)
	{
		waitQueue.deQueue();
		
		if( mCelebCamEditView != null && bitmap != null )
		{
			Log.d("DataAcquisitionActivity", "posting update");
			mCelebCamEditView.setBitmap( bitmap );			
			
		}
		
	}
}
