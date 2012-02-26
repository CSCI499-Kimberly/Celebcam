package com.celebcam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import android.content.Context;

public class CelebCamFont implements CCMemoryWatcher {

	public int getSizeInBytes() {

		return (5*4);
	}


	public int getStaticBytes() {

		return 4;
	}


	class Character implements CCMemoryWatcher {

		public int getSizeInBytes() {
			
			int m = 0;
			if(bitmap != null )
			{
				m += bitmap.getHeight()*bitmap.getWidth()*4;
			}
			
			return (3*4) + m;
		}


		public int getStaticBytes() {

			return 0;
		}
		
		private char   character;
		private Bitmap bitmap;
		private int    offset;
		
		Character()
		{
			CCDebug.registerMemoryWatcher( this );
		}
		
		Character( char character, Bitmap bitmap )
		{
			this.character = character;
			this.bitmap = bitmap;
			
			CCDebug.registerMemoryWatcher( this );
		}
		
		public char getChar()
		{
			return character;
		}
		
		public Bitmap getBitmap()
		{
			return bitmap;
		}
			
	}
	
	Character[] characters = new Character[49];
	int size;
	int spaceBetweenCharacters;
	int numberOfCharacters = 40;
	static CelebCamFont currentFont;
	private Context mContext;
	
	public CelebCamFont( )
	{
	}
	
	public CelebCamFont( Context context, int resId, int size, int spaceBetweenCharacters )
	{
		this.size = size;
		this.spaceBetweenCharacters = spaceBetweenCharacters;
		Bitmap font = BitmapFactory.decodeResource( context.getResources(), resId );
		Bitmap character;
		int count = 0;
		
		for( int i = 0;i < 7; i++ )
		{
			for(int j = 0;j < 7; j++ )
			{
				
				character = Bitmap.createBitmap( font, j*size, i*size, size, size );
				
				if( count < 26 )
				{	
					characters[count] = new Character((char)(count+65), character );
					
				}
				else if ( count >= 26 && count < 37 )
				{
					characters[count] = new Character((char)((count-26)+48), character );
				}
				else if( count == 37 )
				{
					characters[count] = new Character((char)(45), character );
				}
				else if( count == 38 )
				{
					characters[count] = new Character((char)(33), character );
				}
				else if( count == 39 )
				{
					characters[count] = new Character((char)(46), character );
				}
				else if( count == 40 )
				{
					characters[count] = new Character((char)(46), character );
				}				
				
				count++;
			}
			
		}
		
		font.recycle();
		
		font = null;
	}
	
	static int getLengthOf( String text )
	{
		return( text.length()*currentFont.spaceBetweenCharacters );
	}
	
	static int getHeightOfFont()
	{
		return currentFont.size;
	}
	
	static void setFont( Context context, int resId, int size, int spaceBetweenCharacters )
	{
		currentFont = new CelebCamFont(context, resId, size, spaceBetweenCharacters );
	}
	
	static boolean active()
	{
		if( currentFont == null )
			return false;
			
		return true;
	}
	
	static public  int getSize()
	{
		return currentFont.size;
	}
	
	private Bitmap getBitmapOf( char character )
	{
		for( int i = 0; i < numberOfCharacters; i++ )
		{
			if(  ( (int)characters[i].getChar()      == (int)character ) 
			  || ( ((int)characters[i].getChar()+32) == (int)character ) 
			  )
				return characters[i].getBitmap();
		}
		
		return null;
	}
	
	public void paint( Canvas canvas, String text, float x, float y, int z )
	{
	
		char textToPaint[] = text.toCharArray();
		
		Bitmap tmp;
		for( int i = 0; i < text.length(); i++ )
		{
			tmp = getBitmapOf( textToPaint[i] );
			
			if( tmp != null )
				canvas.drawBitmap( tmp , x + (i*spaceBetweenCharacters), y, null );
		}
	}
	
	public void paint( Canvas canvas, String text, float x, float y, Matrix transformMatrix )
	{
	
		char textToPaint[] = text.toCharArray();
		
		Bitmap tmp;
		
		Matrix tmpMatrix = new Matrix();
		Matrix posMatrix = new Matrix();
		
		for( int i = 0; i < text.length(); i++ )
		{
			tmp = getBitmapOf( textToPaint[i] );
			
			if( tmp != null )
			{
				posMatrix.setTranslate(x + (i*spaceBetweenCharacters), y);
				tmpMatrix.setConcat(transformMatrix, posMatrix);
				
				canvas.drawBitmap( tmp , tmpMatrix, null );
			}
		}
	}
	
	static public void paint( Canvas canvas, String text, float x, float y )
	{
		currentFont.paint( canvas, text, x, y, 0 );
	}

	static public void paint( Canvas canvas, String text, Matrix transformMatrix)
	{
		
		currentFont.paint( canvas, text,0,0, transformMatrix );
	}

	
}

