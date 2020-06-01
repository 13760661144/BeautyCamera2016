package cn.poco.setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class SettingSliderBtn extends View
{

	public SettingSliderBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public SettingSliderBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public SettingSliderBtn(Context context) {
		super(context);
		initialize();
	}
	
	protected Bitmap mBmpSlider;
	protected Bitmap mBmpWhiteBack;
	protected Bitmap mBmpBlueBack;
	protected boolean mBoolOn = false;
	protected float  mPreX;
	protected float  mPreY;
	protected long   mOldTime = 0;
	protected boolean mIsMove = false;
	protected OnSwitchListener mSwitchListener = null;
	
	protected void initialize()
	{
		setClickable(true);
		mBmpSlider = BitmapFactory.decodeResource(getResources(), R.drawable.setting_slidebtn_slider);
		mBmpWhiteBack = BitmapFactory.decodeResource(getResources(), R.drawable.setting_slidebtn_whitebk);
//		mBmpBlueBack = BitmapFactory.decodeResource(getResources(), R.drawable.setting_slidebtn_bluebk);
		mBmpBlueBack = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.setting_slidebtn_bluebk));
	}
	
	public void setSwitchStatus(boolean on)
	{
		mBoolOn = on;
		invalidate();
	}
	
	public void setOnSwitchListener(OnSwitchListener listener)
	{
		mSwitchListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int l = getPaddingLeft();
		int t = getPaddingTop();
		if(mBoolOn == true)
		{
//			canvas.drawBitmap(mBmpBlueBack, l, t, null);
			canvas.drawBitmap(mBmpBlueBack, l, t+(mBmpSlider.getHeight()-mBmpBlueBack.getHeight())/2, null);
			canvas.drawBitmap(mBmpSlider, l+mBmpBlueBack.getWidth()-mBmpSlider.getWidth()+ShareData.PxToDpi_xhdpi(2), t, null);
		}
		else
		{
//			canvas.drawBitmap(mBmpWhiteBack, l, t, null);
			canvas.drawBitmap(mBmpWhiteBack, l, t+(mBmpSlider.getHeight()-mBmpWhiteBack.getHeight())/2, null);
			canvas.drawBitmap(mBmpSlider, l-ShareData.PxToDpi_xhdpi(6), t, null);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int act = event.getAction();
		float x = event.getX();
		float y = event.getY();
		long time = System.currentTimeMillis();
		if(act == MotionEvent.ACTION_UP)
		{
			if(mIsMove == false)
			{
				mBoolOn = !mBoolOn;
				invalidate();
				if(mSwitchListener != null)
				{
					mSwitchListener.onSwitch(this, mBoolOn);
				}
			}
		}
		if(act == MotionEvent.ACTION_DOWN)
		{
			mIsMove = false;
		}
		if(act ==  MotionEvent.ACTION_MOVE)
		{
			if(time - mOldTime > 50)
			{
				float offsetx = x-mPreX;
				if(mBoolOn == true)
				{
					if(offsetx < 10)
					{
						mIsMove = true;
						mBoolOn = !mBoolOn; 
						invalidate();
						if(mSwitchListener != null)
						{
							mSwitchListener.onSwitch(this, mBoolOn);
						}
					}
				}
				else
				{
					if(offsetx > 10)
					{
						mIsMove = true;
						mBoolOn = !mBoolOn; 
						invalidate();
						if(mSwitchListener != null)
						{
							mSwitchListener.onSwitch(this, mBoolOn);
						}
					}
				}
				mOldTime = time;
			}
		}
		mPreX = x;
		mPreY = y;
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(getPaddingLeft()+getPaddingRight()+mBmpWhiteBack.getWidth(), getPaddingTop()+getPaddingBottom()+mBmpSlider.getHeight());
	}
	
	public interface OnSwitchListener
	{
		void onSwitch(View v, boolean on);
	}
}
