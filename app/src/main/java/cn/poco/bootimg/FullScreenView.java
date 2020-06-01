package cn.poco.bootimg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;

/**
 * Created by POCO on 2017/5/17.
 * 居中填满view
 */

public class FullScreenView extends View
{
	protected Bitmap mBmp;


	public FullScreenView(Context context)
	{
		super(context);
	}

	public FullScreenView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FullScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	protected Paint mPaint;
	protected Matrix mMatrix;
	@Override
	protected void onDraw(Canvas canvas)
	{
		if(mBmp != null && !mBmp.isRecycled())
		{
			if(mPaint == null)
			{
				mPaint = new Paint();
				mPaint.setFilterBitmap(true);
				mPaint.setAntiAlias(true);
			}
			if(mMatrix == null)
			{
				mMatrix = new Matrix();
				mMatrix.postTranslate((this.getWidth() - mBmp.getWidth()) / 2f, (this.getHeight() - mBmp.getHeight()) / 2f);
				float scale1 = (float)this.getWidth() / (float)mBmp.getWidth();
				float scale2 = (float)this.getHeight() / (float)mBmp.getHeight();
				float scale = scale1 > scale2 ? scale1 : scale2;
				mMatrix.postScale(scale, scale, this.getWidth() / 2f, this.getHeight() / 2f);
			}
			canvas.drawBitmap(mBmp, mMatrix, mPaint);
		}
	}

	public void SetData(Object img)
	{
		Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(getContext(), img, 0, -1, ShareData.m_screenWidth, ShareData.m_screenHeight);
		if(temp != null)
		{
			if(temp.getWidth() > ShareData.m_screenWidth && temp.getHeight() > ShareData.m_screenHeight)
			{
				mBmp = MakeBmp.CreateBitmap(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
			}
			else
			{
				mBmp = temp;
			}
			if(mBmp != temp)
			{
				temp.recycle();
				temp = null;
			}

			if(mBmp != null && mBmp.getWidth() > 0 && mBmp.getHeight() > 0)
			{
				mPaint = null;
				mMatrix = null;
			}
		}
	}

	public void ClearAll()
	{
		if(mBmp != null)
		{
			mBmp.recycle();
			mBmp = null;
		}
	}
}
