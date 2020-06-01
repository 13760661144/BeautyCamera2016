package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageButton extends AppCompatImageView
{

	public ImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageButton(Context context) {
		super(context);
	}

	Bitmap mBmpNormal = null;
	Bitmap mBmpPress = null;
	OnTouchListener mTouchListener;
	public ImageButton(Context context, int resNormal, int resPress) {
		super(context);
		mBmpNormal = BitmapFactory.decodeResource(getResources(), resNormal);
		mBmpPress = BitmapFactory.decodeResource(getResources(), resPress);
		this.setClickable(true);
		this.setImageBitmap(mBmpNormal);
	}
	
	public void setButtonImage(int resNormal, int resPress)
	{
		this.setScaleType(ScaleType.CENTER_INSIDE);
		mBmpNormal = BitmapFactory.decodeResource(getResources(), resNormal);
		mBmpPress = BitmapFactory.decodeResource(getResources(), resPress);
		this.setImageBitmap(mBmpNormal);
	}
	
	public void setButtonImage(Bitmap bmpNormal, Bitmap bmpPress)
	{
		this.setScaleType(ScaleType.CENTER_INSIDE);
		mBmpNormal = bmpNormal;
		mBmpPress = bmpPress;
		this.setImageBitmap(mBmpNormal);
	}
	
	public void setOnTouchListener(OnTouchListener l)
	{
		mTouchListener = l;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int act = event.getAction();
		if(act ==  MotionEvent.ACTION_DOWN)
		{	
			this.setImageBitmap(mBmpPress);
		}
		
		if(act ==  MotionEvent.ACTION_UP)
		{	
			this.setImageBitmap(mBmpNormal);
		}
		if(act == MotionEvent.ACTION_OUTSIDE)
		{
			this.setImageBitmap(mBmpNormal);
		}
		
		if(mTouchListener != null)
		{
			mTouchListener.onTouch(this, event);
		}
		return super.onTouchEvent(event);
	}
}
