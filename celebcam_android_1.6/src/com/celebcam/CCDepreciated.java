package com.celebcam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
	

}
